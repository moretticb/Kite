//////////////////////////////////////////////////////////////////////////////
//
//        ChatMessage.java - Kite Messenger - Threaded chat
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import data.Connector;
import data.QSProtocol;

public class ChatMessage extends JPanel {
	
	private String mID;
	private String nickname;
	private String nick;
	private int total;
	private int unread;
	
	private JLabel person;
	private JTextArea msg;
	private JPanel indentedMsgs;
//	private JLabel lblUnread;
	
	private JButton indentHandle;
	
	private JTextArea indentedText;
	private JScrollPane indentedTextScroll;
	
	private final int ONE_LINE_HEIGHT = 20;
	private final int MAX_LINES_HEIGHT = 60;
	
	private static final BufferedImage SPRITE = ImageManager.getImage("messageSpr.png");
	
	private ChatWindow chatWindow;
	
	private static final Color SEPARATOR = new Color(240, 240, 240);
	private static final Color UNREAD_BG = new Color(252, 252, 252);
	
	public ChatMessage(ChatWindow cw, String nickname, final String nick, String message, Color bgColor){
		setBackground(bgColor);
		setLayout(new MessageLayout());

		this.chatWindow=cw;
		this.nickname=nickname;
		this.nick=nick;
		this.unread=0;
		generateID();
		
		person = new JLabel(nick);
		person.setFont(person.getFont().deriveFont(Font.BOLD));
		add(person);

		indentedText = new JTextArea();
		indentedText.setFont(person.getFont().deriveFont(Font.PLAIN));
		indentedText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMsgOrBreakLine");
		indentedText.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "sendMsgOrBreakLine");
		indentedText.getActionMap().put("sendMsgOrBreakLine", new AbstractAction("sendMsgOrBreakLine"){

			@Override
			public void actionPerformed(ActionEvent e) {
				int modifiers = e.getModifiers();
				if(modifiers==0 && indentedText.getText().length()>0 && !chatWindow.isLocked()){ //ENTER
					ChatMessage msg = new ChatMessage(chatWindow, chatWindow.getItem().getList().getMainWindow().getCurrentUser().getNickname(), chatWindow.getItem().getList().getMainWindow().getCurrentUser().getNick(), indentedText.getText(), Color.WHITE); 
					appendMessage(msg);
					
					Connector c = chatWindow.getItem().getList().getMainWindow().getConnector(); 
					QSProtocol p = new QSProtocol();
					p.add("op", Connector.MESSAGE);
					p.add("type", "C");
					p.add("selfMID", msg.getMID());
					p.add("mID", mID);
					p.add("msg", indentedText.getText());
					p.add("friend", chatWindow.getItem().getNickname());
					c.sendProtocol(p);
					
					indentedText.setText("");
					indentedTextScroll.setSize(0, ONE_LINE_HEIGHT);
				} else if(modifiers==1) { //SHIFT+ENTER
					indentedText.append("\n");
					int height = indentedTextScroll.getPreferredSize().height;
					indentedTextScroll.setSize(0, height>MAX_LINES_HEIGHT?MAX_LINES_HEIGHT:height);
					indentedMsgs.revalidate();
					fixViewport();
				}
			}
			
		});
		indentedTextScroll = new JScrollPane(indentedText);
		indentedTextScroll.setSize(0, 20);
		
		msg = new JTextArea(message);
		msg.setFont(indentedText.getFont());
		msg.setLineWrap(true);
		msg.setEditable(false);
		msg.setOpaque(false);
		add(msg);
		
		indentedMsgs = new JPanel();
		indentedMsgs.setLayout(new ListLayout());
//		indentedMsgs.setBackground(Color.GREEN);
		indentedMsgs.setVisible(false);
		add(indentedMsgs);
		
//		lblUnread = new JLabel(getMessageStatus());
//		add(lblUnread);
		
		indentHandle = new JButton(" ");
		indentHandle.setBorder(null);
		indentHandle.setContentAreaFilled(false);
		indentHandle.setBounds(5,15,12,12);
		add(indentHandle);
		indentHandle.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(indentedMsgs.isVisible())
					collapse();
				else
					expand();
			}
			
		});
		
	}
	
	public void appendMessage(ChatMessage msg){
		total++;
		if(!indentedMsgs.isVisible()){
			unread++;
			setBackground(UNREAD_BG);
//			lblUnread.setText(getMessageStatus());
		}
		indentedMsgs.remove(indentedTextScroll);
		indentedMsgs.add(msg);
		indentedMsgs.add(indentedTextScroll);
		indentedMsgs.validate();	
		fixViewport();
	}

	public void expand(){
		unread=0;
		setBackground(Color.WHITE);
//		lblUnread.setText(getMessageStatus());
		indentedMsgs.setVisible(true);
		indentedMsgs.add(indentedTextScroll);
		indentedText.requestFocus();
		fixRealHeight();
		fixViewport();
	}
	
	public void collapse(){
		indentedMsgs.remove(indentedTextScroll);
		indentedMsgs.setVisible(false);
		fixRealHeight();
		fixViewport();
	}
	
	public int fixRealHeight(){
		int altura = 20; //altura de person
		altura+=msg.getPreferredSize().height;
		altura+=getIndentedHeight();
		setSize(new Dimension(10, altura));
		return altura;
	}
	
	public int getIndentedHeight(){
		if(!indentedMsgs.isVisible()) return 0;
		int height = 0; 
		for(int i=0, ii=indentedMsgs.getComponentCount();i<ii;i++){
			try {
				ChatMessage cm = (ChatMessage) indentedMsgs.getComponent(i);
				height+=cm.fixRealHeight();
			} catch (ClassCastException ex){
				height += indentedMsgs.getComponent(i).getHeight();
			}
		}
		return height;
	}
	
	private void fixViewport(){
		fixRealHeight();
		Component parent = getParent();
		while(!ChatWindow.class.isInstance(parent)){
			Container current = (Container) parent;
			if(current.getLayout() != null)
				current.getLayout().layoutContainer(current);
			parent = parent.getParent();
		}
		((ChatWindow) parent).fixViewportSize();
	}
	
	public String toString(){
		return String.format("%s: %s", person.getText(), msg.getText());
	}
	
	public String getPersonText(){
		return person.getText();
	}
	
	public String getMsgText(){
		return msg.getText();
	}
	
	private void generateID(){
		this.mID = String.format("%s%d", nickname, System.currentTimeMillis());
	}
	
	public String getMID(){
		return this.mID;
	}
	
	public void setMID(String mID){
		this.mID=mID;
	}
	
	public String getNickname(){
		return this.nickname;
	}
	
	public String getNick(){
		return this.nick;
	}
	
	public JPanel getIndentedMsgs(){
		return this.indentedMsgs;
	}
	
	private String getMessageStatus(){
		if(unread > 0)
			return String.format("%d / %d", unread, total);
		else if(total > 0)
			return String.format("%d", total);
		else
			return null;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(unread>0?Color.RED:total>0?Color.GREEN:SEPARATOR);
		g.fillRect(0, 1, 2, getHeight()-2);
		int[] offset = {indentedMsgs.isVisible()?1:0, unread>0?0:1};
		
		if(SPRITE != null){
			g.drawImage(SPRITE, indentHandle.getX(), indentHandle.getY(), indentHandle.getX()+10, indentHandle.getY()+10, offset[0]*10, offset[1]*10, offset[0]*10+10, offset[1]*10+10, null);
		}
//		String msgStatus = getMessageStatus();
//		if(msgStatus != null){
//			int strWidth = g.getFontMetrics().stringWidth(msgStatus);
//			
//			g.setFont(msg.getFont());
//			g.setColor(Color.DARK_GRAY);
//			
//			Point[] p = {new Point(getWidth()-strWidth-16-5, 4), new Point(getWidth()-16+5, 16+4)};
//			g.drawImage(SPRITE, p[0].x, p[0].y, p[0].x+4, p[1].y, 20, offset[1]*16, 24, offset[1]*16+16, null);
//			g.drawImage(SPRITE, p[0].x+4, p[0].y, p[1].x-4, p[1].y, 24, offset[1]*16, 25, offset[1]*16+16, null);
//			g.drawImage(SPRITE, p[1].x-4, p[0].y, p[1].x, p[1].y, 26, offset[1]*16, 30, offset[1]*16+16, null);
//			
//			g.drawString(msgStatus, getWidth()-strWidth-16, 16);
//		}
	}
	
}
