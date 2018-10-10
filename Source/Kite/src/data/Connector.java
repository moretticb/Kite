//////////////////////////////////////////////////////////////////////////////
//
//         Connector.java - Kite Messenger - Threaded chat
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


package data;

import imgs.ImageManager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import telas.ChatMessage;
import telas.ChatWindow;
import telas.ContactListItem;
import telas.MainWindow;

public class Connector {
	
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
	
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	private MainWindow window;
	
	public Connector(MainWindow window) throws IOException {
		socket = new Socket(window.getPreferences().getHost(), window.getPreferences().getPort());
		input = socket.getInputStream();
		output = socket.getOutputStream();
		
		this.window=window;
		checkForProtocol();
	}
	
	private void checkForProtocol(){
		new Thread(new Runnable(){
			
			public void run(){
				while(true){
					try {
						QSProtocol p = new QSProtocol(input);
						handleProtocol(p);
					} catch (QSProtocolException e){
						e.printStackTrace();
						if(e.getFlag() == QSProtocolException.CONNECTION_FAILED){
							System.out.println("Connection closed");
							break;
						}
					}
				}
			}
			
		}).start();
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public boolean sendProtocol(QSProtocol p){
		String qs = p.toString();
		try {
			output.write(qs.getBytes(), 0, qs.getBytes().length);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void handleProtocol(QSProtocol p) throws QSProtocolException {
		int op = p.getInt("op");
		if(op == SERVER_ANSWER){
			int cod = p.getInt("cod"); 
			if(cod == AUTH){
				if(p.getInt("result") == 1){
					window.clearLoginFields();
					User u = new User(window, p.getString("nickname"), p.getString("nick"), p.getString("msg"), p.getInt("status"), p.getInt("flag"), ImageManager.string2Image(p.getString("img")));
					window.setCurrentUser(u);
					window.initContactsScreen();
					u.updateGUI();
				} else {
					JOptionPane.showMessageDialog(window, "Username and/or password not correct.", "Authentication error", JOptionPane.ERROR_MESSAGE);
					window.getBtnConnect().setEnabled(true);
					throw new QSProtocolException(QSProtocolException.CONNECTION_FAILED);
				}
			} else if(cod == REGISTER){
				if(p.getInt("result") == 1){
					JOptionPane.showMessageDialog(window.getRegisterForm(), "Your account has been created.", "Register successful", JOptionPane.INFORMATION_MESSAGE);
					window.getRegisterForm().resetForm();
					window.getRegisterForm().setVisible(false);
				} else {
					JOptionPane.showMessageDialog(window.getRegisterForm(), "Username already in use.", "Authentication error", JOptionPane.ERROR_MESSAGE);
				}
				throw new QSProtocolException(QSProtocolException.CONNECTION_FAILED);
			}
			
		} else if(op == ADD_USER){
			// SIM - 0 / NAO - 1 (no protocolo será normal: o contrário)
			int resp = JOptionPane.showConfirmDialog(window, p.getString("requester")+" wants to add you as a friend. Accept "+p.getString("requester")+"?", "Friendship request from "+p.getString("requester"), JOptionPane.YES_NO_OPTION);
			p.add("op", ADD_CONFIRM);
			p.add("answer", resp==0?1:0);
			sendProtocol(p);
		} else if(op == CHANGE_DATA){
			String field = p.getString("field");
			if(field.equals("status")){
				int status = p.getInt("value");
				if(status == STATUS_OFFLINE){
					window.removeUserFromList(p.getString("nickname"));
				} else {
					ContactListItem item = window.getContactList().getItem(p.getString("nickname"));
					if(item != null){
						item.setStatus(p.getInt("value"));
					} else {
						String details = p.getString("details");
						if(details != null){
							QSProtocol d = new QSProtocol(details);
							window.appendUserToList(p.getString("nickname"), d.getString("nick"), d.getString("msg"), ImageManager.string2Image(d.getString("img")), d.getInt("flag"), d.getInt("status"));
						} else {
							window.appendUserToList(p.getString("nickname"), p.getString("nickname"), p.getString("nickname"), null, 0, 1);
						}
					}
				}
			} else if(field.equals("nick")){
				window.getContactList().getItem(p.getString("nickname")).setNick(p.getString("value"));
			} else if(field.equals("msg")){
				window.getContactList().getItem(p.getString("nickname")).setMsg(p.getString("value"));
			} else if(field.equals("img")){
				BufferedImage img = ImageManager.string2Image(p.getString("value"));
				ContactListItem item = window.getContactList().getItem(p.getString("nickname"));
				if(item != null){
					item.getAvatar().changePic(img);
					item.getAvatar().getCurrentUser().setImg(img, User.UPD_FROM_SERVER);
					if(item.getWindow() != null) item.getWindow().getPerson().changePic(img);
				}
			}
			
		} else if(op == ADDING_FRIENDS){
			int count=1;
			String info = p.getString(String.format("r%d",count++));
			while(info != null){
				QSProtocol addp = new QSProtocol();
				addp.add("op", ADD_USER);
				addp.add("requester", info);
				handleProtocol(addp);
				info = p.getString(String.format("r%d",count++));
			}
		} else if(op == FRIENDS_INFO){
			int count=1;
			String info = p.getString(String.format("u%d",count++));
			while(info != null){
				try {
					QSProtocol userp = new QSProtocol(info);
					window.appendUserToList(userp.getString("nickname"), userp.getString("nick"), userp.getString("msg"), ImageManager.string2Image(userp.getString("img")), userp.getInt("flag"), userp.getInt("status"));
				} catch (QSProtocolException e) {
				}
				info = p.getString(String.format("u%d",count++));
			}
		} else if(op == MESSAGE){
			ContactListItem item = window.getContactList().getItem(p.getString("friend"));
			ChatWindow w;
			ChatMessage msg;
			
			if(item.getWindow() == null) item.openWindow(false);
			w = item.getWindow();
			msg = new ChatMessage(w, item.getNickname(), item.getNick(), p.getString("msg"), Color.WHITE);
			msg.setMID(p.getString("selfMID"));
			
			if(p.getString("type").equals("N")){
				w.appendMessage(msg);
			} else {
				ChatMessage m = w.getMessage(p.getString("mID"));
				if(m != null) m.appendMessage(msg);
			}
		}
	}
	
	public void terminate(){
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
