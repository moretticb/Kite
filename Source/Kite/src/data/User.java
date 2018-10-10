//////////////////////////////////////////////////////////////////////////////
//
//          User.java - Kite Messenger - Threaded chat
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

import java.awt.image.BufferedImage;

import telas.ContactListItem;
import telas.MainWindow;

public class User {
	
	public static final int UPD_FROM_SERVER = 1;
	public static final int UPD_FROM_CLIENT = 2;
	
	private String nickname; //nickname ID
	private String nick; //nickname de exibição
	private String msg;
	private int status;
	private int flag;
	private BufferedImage img;

	private MainWindow window;
	
	private Connector c;
	
	public User(MainWindow window, String nickname, String nick, String msg, int status, int flag, BufferedImage img){
		this.flag=flag;
		this.status=status;
		this.window=window;
		this.nickname=nickname;
		this.nick=nick;
		this.msg=msg;
		this.img=img;
		
//		updateGUI();
//		System.out.println("Novo usuario: "+toString());
	}
	
	public String getNickname(){
		return this.nickname;
	}

	public String getNick(){
		return this.nick;
	}
	
	public void setNick(String nick, int updateType){
		this.nick=nick;
		if(updateType == UPD_FROM_SERVER){
			window.getLblNickname().setText(this.nick);
		} else if(updateType == UPD_FROM_CLIENT){
			QSProtocol p = new QSProtocol();
			p.add("op", Connector.CHANGE_DATA);
			p.add("field", "nick");
			p.add("value", nick);
			window.getConnector().sendProtocol(p);
		}
	}
	
	public String getMsg(){
		return this.msg;
	}
	
	public void setMsg(String msg, int updateType){
		this.msg=msg;
		if(updateType == UPD_FROM_SERVER){
			window.getLblMsg().setText(this.msg);
		} else if(updateType == UPD_FROM_CLIENT){
			QSProtocol p = new QSProtocol();
			p.add("op", Connector.CHANGE_DATA);
			p.add("field", "msg");
			p.add("value", msg);
			window.getConnector().sendProtocol(p);
		}
	}
	
	public Connector getConnector(){
		return this.c;
	}
	
	public void setConnector(Connector c){
		this.c=c;
	}
	
	public void updateGUI(){
		setNick(this.nick, UPD_FROM_SERVER);
		setMsg(this.msg, UPD_FROM_SERVER);
		setImg(this.img, UPD_FROM_SERVER);
		setStatus(this.status, UPD_FROM_SERVER);
		window.getMyAvatar().setFlag(this.flag);
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int status, int updateType){
		this.status=status;
		if(window != null){
			window.getMyAvatar().setStatus(status);
			
			int count=0;
			ContactListItem item = window.getContactList().getItem(count++);
			while(item != null){
				if(item.getWindow() != null) item.getWindow().getMe().setStatus(status);
				item = window.getContactList().getItem(count++);
			}
		}
		
		if(updateType == UPD_FROM_CLIENT){
			QSProtocol p = new QSProtocol();
			p.add("op", Connector.CHANGE_DATA);
			p.add("field", "status");
			p.add("value", status);
			window.getConnector().sendProtocol(p);
		}
	}
	
	public BufferedImage getImg(){
		return this.img;
	}
	
	public void setImg(BufferedImage img, int updateType){
		this.img = img;
		if(window != null){
			window.getMyAvatar().changePic(img);
			
			int count=0;
			ContactListItem item = window.getContactList().getItem(count++);
			while(item != null){
				if(this!=window.getCurrentUser())
					item.getAvatar().changePic(img);
				if(item.getWindow() != null){
					if(this==window.getCurrentUser()) item.getWindow().getMe().changePic(img);
					else item.getWindow().getPerson().changePic(img);
				}
				item = window.getContactList().getItem(count++);
			}
		}
		
		if(updateType == UPD_FROM_CLIENT){
			QSProtocol p = new QSProtocol();
			p.add("op", Connector.CHANGE_DATA);
			p.add("field", "img");
			p.add("value", ImageManager.img2String(img));
			window.getConnector().sendProtocol(p);
		}
	}

	public MainWindow getMainWindow(){
		return this.window;
	}
	
	public int getFlag(){
		return this.flag;
	}
	
	public String toString(){
		return String.format("%s (%s - %d) - %s", this.nick, this.nickname, this.status, this.msg);
	}

}
