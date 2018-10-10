//////////////////////////////////////////////////////////////////////////////
//
//       Connection.java - Kite Messenger - Threaded chat
//  Copyright (c) 2012 Caio Benatti Moretti <caiodba@gmail.com>
//                 http://www.moretticb.com/Kite
//
//  Last update: 9 October 2018
//
//  This is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program. If not, see <http://www.gnu.org/licenses/>.
//
//////////////////////////////////////////////////////////////////////////////


package app;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import data.QSProtocol;
import data.QSProtocolException;
import database.DBConnection;


public class Connection implements Runnable {
	
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_BUSY = 2;
	public static final int STATUS_OUT = 3;
	
	public static final int SERVER_ANSWER = 1;
	public static final int REGISTER = 2;
	public static final int AUTH = 3;
	public static final int CHANGE_DATA = 4;
	public static final int MESSAGE = 5;
	public static final int ADD_USER = 6;
	public static final int ADD_CONFIRM = 7;
	public static final int FRIENDS_INFO = 8;
	public static final int ADDING_FRIENDS = 9;
	
	private Server server;
	private String nickname;
//	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private Thread thread;
	
	public Connection(Server server, Socket socket) throws IOException {
		this(null, server, socket);
	}
	
	public Connection(String nickname, Server server, Socket socket) throws IOException {
		this.nickname = nickname;
		this.server = server;
//		this.socket = socket;
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
	}
	
	public boolean sendProtocol(QSProtocol protocol){
		byte[] bs = protocol.toString().getBytes();
		try {
			output.write(bs, 0, bs.length);
			return true;
		} catch (IOException ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public String getNickname(){
		return this.nickname;
	}
	
	public void setNickname(String nickname){
		this.nickname=nickname;
	}
	
	public void activate(){
		thread = new Thread(this);
		thread.start();
	}
	
	public void deactivate(){
		thread.interrupt();
	}
	
	public void run(){
		while(true){
			try {
				QSProtocol p = new QSProtocol(input);
				handleProtocol(p);
			} catch (QSProtocolException e) {
				if(e.getFlag() == QSProtocolException.CONNECTION_FAILED){
					MainClass.log(nickname+" has logged out");
					server.removeConnection(nickname);
					notifyFriends(STATUS_OFFLINE);
					String sql = String.format("UPDATE usuario SET status='%d' WHERE nickname='%s'", STATUS_OFFLINE, DBConnection.realEscape(nickname));
					try {
						server.getDBConnection().execute(sql);
					} catch (SQLException ex){
						ex.printStackTrace();
					}
					break;
				} else {
					System.out.println("Error on reading protocol: "+e);
				}
			}
		}
	}
	
	private ResultSet getOnlineFriends(boolean includingMe, String sqlColumns){
		String sql = String.format("SELECT %s FROM usuario WHERE nickname='%s' UNION ALL", sqlColumns, DBConnection.realEscape(nickname));
		sql = String.format("%s SELECT %s FROM friendship, usuario WHERE requester=nickname AND status!='%d' AND requested='%s' AND accept='Y';", includingMe?sql:"", sqlColumns, STATUS_OFFLINE, DBConnection.realEscape(nickname));
		
		return server.getDBConnection().executeQuery(sql);
	}
	
	public void notifyFriends(int status){
//		String sql = String.format("SELECT nickname, status, nick, msg, img, flag FROM usuario WHERE nickname='%s' UNION ALL", nickname);
//		sql = String.format("%s SELECT nickname, status, nick, msg, img, flag FROM friendship, usuario WHERE requester=nickname AND status='%d' AND requested='%s' AND accept='Y';", sql, STATUS_ONLINE, nickname);
//		ResultSet rs = server.getDBConnection().executeQuery(sql);
		
		ResultSet rs = getOnlineFriends(true, "nickname, status, nick, msg, img, flag");
		if(rs != null){
			
			QSProtocol p = new QSProtocol();
			p.add("op", CHANGE_DATA);
			p.add("nickname", nickname);
			p.add("field", "status");
			p.add("value", status);
			
			try {
				int count=1;
				while(rs.next()){
					if(count > 1){
						Connection c = server.getConnection(rs.getString("nickname"));
						if(c != null) c.sendProtocol(p);
					} else {
						QSProtocol details = new QSProtocol();
						details.add("nickname", rs.getString("nickname"));
						details.add("nick", rs.getString("nick"));
						details.add("msg", rs.getString("msg"));
						details.add("img", rs.getString("img"));
						details.add("flag", rs.getInt("flag"));
						details.add("status", rs.getString("status"));
						p.add("details", details.toString());
					}
					count++;
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	public void retrieveAddingFriends(){
		String sql = String.format("SELECT * FROM friendship WHERE requested='%s' AND accept='N'", DBConnection.realEscape(nickname));
		ResultSet rs = server.getDBConnection().executeQuery(sql);
		
		QSProtocol p = new QSProtocol();
		p.add("op", ADDING_FRIENDS);
		try {
			while(rs.next())
				p.add(String.format("r%d", rs.getRow()), rs.getString("requester"));
			
			sendProtocol(p);
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void retrieveFriendsInfo(){
		String sql = String.format("SELECT nickname, nick, msg, status, img, flag FROM usuario, friendship WHERE requester=nickname AND status!='%d' AND requested='%s' AND accept='Y';", STATUS_OFFLINE, DBConnection.realEscape(nickname));
		ResultSet rs = server.getDBConnection().executeQuery(sql);
		
		QSProtocol p = new QSProtocol();
		p.add("op", FRIENDS_INFO);
		try {
			while(rs.next()){
				QSProtocol userp = new QSProtocol();
				userp.add("nickname", rs.getString("nickname"));
				userp.add("nick", rs.getString("nick"));
				userp.add("msg", rs.getString("msg"));
				userp.add("img", rs.getString("img"));
				userp.add("flag", rs.getInt("flag"));
				userp.add("status", rs.getInt("status"));
				p.add(String.format("u%d", rs.getRow()), userp.toString());
			}
			
			sendProtocol(p);
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	private void handleProtocol(QSProtocol protocol){
		int op = protocol.getInt("op"); 
		if(op==ADD_USER){
			MainClass.log(nickname+" sent to "+protocol.getString("requested")+" a friendship request");
			String sql = String.format("INSERT INTO friendship VALUES ('%s', '%s', 'N'), ('%s', '%s', 'Y');", DBConnection.realEscape(nickname), DBConnection.realEscape(protocol.getString("requested")), DBConnection.realEscape(protocol.getString("requested")), DBConnection.realEscape(nickname));
			try {
				server.getDBConnection().execute(sql);
				Connection c = server.getConnection(protocol.getString("requested"));
				if(c != null){
					protocol.remove("requested");
					protocol.add("requester", nickname);
					c.sendProtocol(protocol);
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		} else if(op==ADD_CONFIRM){
			String sql;
			try {
				if(protocol.getInt("answer") == 0) {
					sql = String.format("DELETE FROM friendship WHERE (requester='%s' AND requested='%s') OR (requester='%s' AND requested='%s');", DBConnection.realEscape(nickname), DBConnection.realEscape(protocol.getString("requester")), DBConnection.realEscape(protocol.getString("requester")), DBConnection.realEscape(nickname));
					server.getDBConnection().execute(sql);
				} else {
					sql = String.format("UPDATE friendship SET accept='Y' WHERE requester='%s' AND requested='%s';", DBConnection.realEscape(protocol.getString("requester")), DBConnection.realEscape(nickname));
					
					server.getDBConnection().execute(sql);
					
					sql = String.format("SELECT nickname, nick, msg, status, img, flag FROM usuario WHERE status!='%d' AND nickname IN ('%s', '%s');", STATUS_OFFLINE, DBConnection.realEscape(nickname), DBConnection.realEscape(protocol.getString("requester")));
					ResultSet rs = server.getDBConnection().executeQuery(sql);
					
					while(rs.next()){
						QSProtocol details = new QSProtocol();
						details.add("nickname", rs.getString("nickname"));
						details.add("nick", rs.getString("nick"));
						details.add("msg", rs.getString("msg"));
						details.add("status", rs.getString("status"));
						details.add("img", rs.getString("img"));
						details.add("flag", rs.getInt("flag"));
						
						QSProtocol p = new QSProtocol();
						p.add("op", CHANGE_DATA);
						p.add("field", "status");
						p.add("value", rs.getString("status"));
						p.add("details", details.toString());
						
						if(rs.getString("nickname").equals(nickname)){
							p.add("nickname", nickname);
							
							Connection connection = server.getConnection(protocol.getString("requester"));
							if(connection != null)
								connection.sendProtocol(p);
						} else {
							p.add("nickname", protocol.getString("requester"));
							sendProtocol(p);
						}
						
					}
					
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		} else if(op==MESSAGE){
			Connection friend = server.getConnection(protocol.getString("friend"));
			protocol.add("friend", getNickname());
			if(friend != null){
				friend.sendProtocol(protocol);
			} else {
				System.out.println("Connnection not available");
				//Guardar no banco para enviar quando a pessoa estiver online
			}
		} else if(op==CHANGE_DATA){
			String sql = String.format("UPDATE usuario SET %s='%s' WHERE nickname='%s'", "%s", DBConnection.realEscape(protocol.getString("value")), DBConnection.realEscape(nickname));
			String field=protocol.getString("field");
			if(field.equals("nick") || field.equals("msg") || field.equals("status") || field.equals("img")) {
				sql = String.format(sql, field);
			}
			
			try {
				server.getDBConnection().execute(sql);
				
				protocol.add("nickname", nickname);
				ResultSet rs = getOnlineFriends(false, "nickname");
				while(rs.next()){
					Connection c = server.getConnection(rs.getString("nickname"));
					if(c != null) c.sendProtocol(protocol);
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}

}
