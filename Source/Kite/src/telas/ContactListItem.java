//////////////////////////////////////////////////////////////////////////////
//
//     ContactListItem.java - Kite Messenger - Threaded chat
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


package telas;

import imgs.ImageManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Connector;
import data.User;

public class ContactListItem extends JPanel {
	
	public static final int HEIGHT = 40;
	
	private String nickname;
	private String nick;
	private String msg;
	private int flag;
	private JLabel lblNickname;
	private JLabel lblMsg;
	private Avatar avatar;
	private JLabel flagArea;
	private boolean rolled = false;
	private int status;
	
	private ChatWindow window;
	private ContactList list;
	
	private static BufferedImage highlight;
	private static BufferedImage quote;
	
	public ContactListItem(ContactList list, String nickname, String nick, String msg, int flag, BufferedImage img, int status){
		super();
		this.list=list;
		setSize(new Dimension(HEIGHT, HEIGHT));
		setLayout(null);
		
//		setBackground(Color.ORANGE);
		setOpaque(false);
		
		this.nickname = nickname;
		this.nick = nick;
		this.msg = msg;
		this.status = status;
		this.flag = flag;
		
		avatar = new Avatar(new User(null, nickname, nick, msg, status, flag, img), false, Avatar.AVATAR_SMALL);
		avatar.setBounds(5, 3, avatar.getRealSize(), avatar.getRealSize());
		add(avatar);

		if(highlight == null){
			highlight = ImageManager.getImage("contact_status_spr.png");
			quote = ImageManager.getImage("quote.png");
		}
		
		Font f;
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("MankSans.ttf"));
			f = f.deriveFont(Font.PLAIN, 19);
		} catch (Exception ex){
			f = new Font("Segoe UI", Font.PLAIN, 15);
		}
		
		lblNickname = new JLabel(nick);
		lblNickname.setBounds(10+avatar.getRealSize(), 0, 300, 20);
		lblNickname.setFont(f);
		add(lblNickname);
		
		lblMsg = new JLabel(msg);
		lblMsg.setBounds(lblNickname.getX()+25, 20, lblNickname.getWidth(), lblNickname.getHeight());
		add(lblMsg);
		
	}
	
	public String getNickname(){
		return this.nickname;
	}
	
	public String getNick(){
		return this.nick;
	}
	
	public void setNick(String nick){
		this.nick=nick;
		lblNickname.setText(nick);
		if(window != null){
			window.getLblTitle().setText(nick);
		}
	}
	
	public String getMsg(){
		return this.msg;
	}
	
	public void setMsg(String msg){
		this.msg=msg;
		lblMsg.setText(msg);
		if(window != null){
			window.getLblMsg().setText(msg);
		}
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int status){
		this.status=status;
		avatar.setStatus(status);
		avatar.getCurrentUser().setStatus(status, User.UPD_FROM_SERVER);
		if(window != null)
			window.getPerson().setStatus(status);
	}
	
	public ChatWindow getWindow(){
		return this.window;
	}
	
	public void setWindow(ChatWindow window){
		this.window=window;
		if(window != null){
			window.unlock();
			window.getPerson().setStatus(avatar.getCurrentUser().getStatus());
			window.getPerson().changePic(avatar.getCurrentUser().getImg());
			window.getLblTitle().setText(nick);
			window.getLblMsg().setText(msg);
		}
	}
	
	public void openWindow(boolean meant){
		openWindow("Chat with "+nick, meant);
	}
	
	public synchronized void openWindow(String title, boolean meant){
		if(window == null){
			window = new ChatWindow(this);
			if(!meant){
				window.setState(ChatWindow.ICONIFIED);
			}
		} else {
			if(window.getState()==ChatWindow.ICONIFIED) // minimizado
				window.setState(ChatWindow.NORMAL);
			else
				window.transferFocus();
		}
		window.setVisible(true);
	}
	
	public void hover(){
		this.rolled = true;
		repaint();
	}
	
	public void idle(){
		this.rolled = false;
		repaint();
	}
	
	public ContactList getList(){
		return this.list;
	}
	
	public void setList(ContactList list){
		this.list=list;
	}
	
	public Avatar getAvatar(){
		return this.avatar;
	}
	
	public void paint(Graphics g){
		final int BORDER_WIDTH = 3;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
		
		if(rolled){
			BufferedImage left = highlight.getSubimage(0, 0, BORDER_WIDTH, HEIGHT);
			BufferedImage mid = highlight.getSubimage(BORDER_WIDTH, 0, 1, HEIGHT);
			BufferedImage right = highlight.getSubimage(highlight.getWidth()-BORDER_WIDTH, 0, BORDER_WIDTH, HEIGHT);
			g.drawImage(left, 0, 0, null);
			g.drawImage(mid, BORDER_WIDTH, 0, getWidth()-BORDER_WIDTH, HEIGHT, 0, 0, mid.getWidth(), mid.getHeight(), null);
			g.drawImage(right, getWidth()-BORDER_WIDTH, 0, null);
		}

		if(msg != null && msg.length() > 0)
			g.drawImage(quote, 55, 25, null);
	}
	
}
