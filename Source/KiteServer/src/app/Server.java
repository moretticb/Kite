package app;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLEngineResult.Status;

import data.QSProtocol;
import data.QSProtocolException;
import database.DBConnection;


public class Server {
	
	private Map<String, Connection> connections;
	private ServerSocket server;
	private int port;
	private DBConnection conn;
	private MessageDigest digest;
	
	public Server(int portToListen) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		connections = new HashMap<String, Connection>();
		this.port = portToListen;
		conn = new DBConnection("localhost", "SA", "");
		digest = MessageDigest.getInstance("MD5");
	}
	
	public void initiate() throws IOException, SQLException {
		MainClass.log("Initiating server...");
		conn.execute("UPDATE usuario SET status='0'");
		try {
			server = new ServerSocket(port);
			MainClass.log("Listening on port "+port);
			MainClass.log("Server initiated.\n***");
			while(!server.isClosed()){
				Socket s = server.accept();
				QSProtocol p = new QSProtocol(s.getInputStream());
				
				if(p.getString("op") != null){
					Connection c = new Connection(this, s);
					if(p.getInt("op") == Connection.AUTH){
						QSProtocol pResp = new QSProtocol();
						pResp.add("op", Connection.SERVER_ANSWER);
						pResp.add("cod", Connection.AUTH);
						
						ResultSet authRs = auth(p); 
						if(authRs != null){
							MainClass.log(p.getString("nickname")+" has logged in");
							
							connections.put(p.getString("nickname"), c);
							c.setNickname(p.getString("nickname"));
							c.retrieveAddingFriends();
							c.retrieveFriendsInfo();
							c.notifyFriends(Connection.STATUS_ONLINE);
							c.activate();
							
							pResp.add("result", 1);
							pResp.add("nickname", p.getString("nickname"));
							pResp.add("nick", authRs.getString("nick"));
							pResp.add("msg", authRs.getString("msg"));
							pResp.add("status", Connection.STATUS_ONLINE);
							pResp.add("flag", authRs.getString("flag"));
							pResp.add("img", authRs.getString("img"));
							//adicionar tbm a imagem codificada
							
							c.sendProtocol(pResp);
						} else {
							MainClass.log("Unable to authenticate "+p.getString("nickname"));
							pResp.add("result", 0);
							c.sendProtocol(pResp);
							s.close();
						}
					} else if(p.getInt("op") == Connection.REGISTER){
						String sql = String.format("INSERT INTO usuario VALUES ('%s', '%s', '%s', %d, '%s', '', '%s', '%s', '', %d);",
								DBConnection.realEscape(p.getString("name")),
								DBConnection.realEscape(p.getString("nickname")),
								DBConnection.realEscape(p.getString("name")),
								Connection.STATUS_OFFLINE,
								md5(p.getString("password")),
								DBConnection.realEscape(p.getString("birthdate")),
								DBConnection.realEscape(p.getString("gender")),
								p.getInt("flag")
						);
						
						QSProtocol pResp = new QSProtocol();
						pResp.add("op", Connection.SERVER_ANSWER);
						pResp.add("cod", Connection.REGISTER);
						try{
							pResp.add("result", !conn.execute(sql)?1:0);
							MainClass.log(String.format("New registered user: %s (%s)", p.getString("name"), p.getString("nickname")));
						} catch (SQLException e){
							e.printStackTrace();
							pResp.add("result", 0);
							MainClass.log(String.format("Unable to register user: %s (%s)", p.getString("name"), p.getString("nickname")));
						}
						c.sendProtocol(pResp);
						s.close();
					}
				} else {
					s.close();
				}
				
			}
		} catch (QSProtocolException e) { //ENCERRAR CONEXAO
			e.printStackTrace();
		}
	}
	
	public void dispose() throws IOException {
		server.close();
	}
	
	public Connection getConnection(String nickname){
		return connections.get(nickname);
	}
	
	public synchronized void removeConnection (String nickname){
		connections.remove(nickname);
	}
	
	private ResultSet auth(QSProtocol p){
		ResultSet rs = conn.executeQuery(String.format("SELECT nick, senha, msg, status, flag, img FROM usuario WHERE nickname='%s';", DBConnection.realEscape(p.getString("nickname"))));
		try {
			rs.next();
			if(rs.getString("senha").equals(md5(p.getString("senha")))){
				String sql = String.format("UPDATE usuario SET status='%d' WHERE nickname='%s'", Connection.STATUS_ONLINE, p.getString("nickname"));
				conn.execute(sql);
				return rs;
			} else
				return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	private String md5(String str){
		byte[] bs = digest.digest(str.getBytes());
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<bs.length;i++)
			sb.append(Integer.toHexString((int) bs[i]));
		
		return sb.toString().substring(0, 32);
	}
	
	public synchronized DBConnection getDBConnection(){
		return conn;
	}

}
