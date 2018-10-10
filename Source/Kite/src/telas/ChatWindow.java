package telas;

import imgs.ImageManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import data.Connector;
import data.QSProtocol;
import data.User;

public class ChatWindow extends JFrame {
	
	private final int AVATAR_GAPS = 5;
	private final int MIN_WIDTH = 640;
	private final int MIN_HEIGHT = 400;
	
	private final int CHAT_AREA_VGAP = 12;
	private final int CHAT_AREA_HGAP = 5;
	
	private static JPanel[] dummies;
	private static final String[] PLACES = {BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.WEST};
	
	private Container c;
	
	private ContactListItem item;
	private boolean locked;
	
	private JPanel avatars;
	private JPanel meAvatarArea;
	private JPanel personAvatarArea;
	private Avatar me;
	private Avatar person;
	
	private JPanel rightArea;
	
	private JPanel titleArea;
	private JPanel titleRealArea;
	private JLabel lblTitle;
	private JLabel lblMsg;
	
	private JScrollPane chatArea;
	private JPanel chatViewport;
	
	private JPanel typingArea;
	private JTextArea textArea; //temporário
	private JScrollPane typingScroll;
	
	private ChatWindow here;
	
	public ChatWindow(ContactListItem listItem){
		super("Chat with "+listItem.getNick());
		setIconImage(listItem.getList().getMainWindow().getIcon(2));
		setLayout(new BorderLayout());
//		setUndecorated(true);
		setSize(MIN_WIDTH, MIN_HEIGHT+80);
		setVisible(false);
		
		here = this;
		locked = false;
		
		c = getContentPane();
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		if(dummies == null){
			dummies = new JPanel[4];
			for(int i=0;i<4;i++){
				dummies[i] = new JPanel();
				dummies[i].setOpaque(false);
				dummies[i].setPreferredSize(new Dimension(i<2?10:CHAT_AREA_VGAP*(1+i/3), i<2?CHAT_AREA_HGAP:10));
			}
		}
		
		this.item = listItem;
		final Color gray = new Color(249, 249, 249);
		
		avatars = new JPanel(){
			
			public void paint(Graphics g){
				super.paint(g);
				BufferedImage shad = ImageManager.getImage("shads.png");
				g.drawImage(shad, this.getWidth()-3, getTitleHeight()-3, this.getWidth(), getTitleHeight(), 4, 0, 7, 3, null);
				g.drawImage(shad, this.getWidth()-3, getTitleHeight(), this.getWidth(), this.getHeight(), 0, 0, 3, 1, null);
				
			}
			
		};
		avatars.setLayout(new BorderLayout());
		avatars.setBackground(gray);
		c.add(avatars, BorderLayout.WEST);
		
		me = new Avatar(item.getList().getMainWindow().getCurrentUser(), true, Avatar.AVATAR_LARGE);
		me.setBounds(AVATAR_GAPS, AVATAR_GAPS, me.getRealSize(), me.getRealSize());
		
		meAvatarArea = new JPanel();
		meAvatarArea.setOpaque(false);
		meAvatarArea.setPreferredSize(new Dimension(me.getRealSize()+AVATAR_GAPS*2+5, me.getRealSize()+AVATAR_GAPS*2));
		avatars.add(meAvatarArea, BorderLayout.SOUTH);
		meAvatarArea.add(me);
		
		person = new Avatar(item.getAvatar().getCurrentUser(), false, Avatar.AVATAR_LARGE);
		person.setBounds(AVATAR_GAPS, AVATAR_GAPS, person.getRealSize(), person.getRealSize());
		
		personAvatarArea = new JPanel();
		personAvatarArea.setOpaque(false);
		personAvatarArea.setPreferredSize(new Dimension(person.getRealSize()+AVATAR_GAPS*2, person.getRealSize()+AVATAR_GAPS*2));
		avatars.add(personAvatarArea, BorderLayout.NORTH);
		personAvatarArea.add(person);
		
		rightArea = new JPanel();
		rightArea.setLayout(new BorderLayout());
		rightArea.setBackground(Color.WHITE);
		c.add(rightArea, BorderLayout.CENTER);
		
		titleArea = new JPanel(){
			
			public void paint(Graphics g){
				g.setColor(gray);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				BufferedImage right = ImageManager.getImage("right.png");
				g.drawImage(right, this.getWidth()-right.getWidth(), 0, null);
				
				super.paint(g);
				
				BufferedImage shad = ImageManager.getImage("shads.png");
				g.drawImage(shad, 0, getTitleHeight()-3, this.getWidth(), getTitleHeight(), 6, 0, 7, 3, null);
			}
			
		};
		titleArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		titleArea.setBackground(gray);
		titleArea.setOpaque(false);
		rightArea.add(titleArea, BorderLayout.NORTH);
		
		titleRealArea = new JPanel();
		titleRealArea.setLayout(null);
//		titleRealArea.setBackground(gray);
		titleRealArea.setOpaque(false);
//		titleRealArea.setPreferredSize(new Dimension(MIN_WIDTH, person.getRealSize()+AVATAR_GAPS*2));
		titleRealArea.setPreferredSize(new Dimension(MIN_WIDTH, getTitleHeight()));
		titleRealArea.setBounds(0, 0, titleRealArea.getPreferredSize().width, titleRealArea.getPreferredSize().height);
		titleArea.add(titleRealArea);
		
		lblTitle = new JLabel(item.getNick());
		lblTitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 25));
		lblTitle.setBounds(20, 8, 200, 30);
		titleRealArea.add(lblTitle);
		
		lblMsg = new JLabel(item.getMsg());
		lblMsg.setBounds(lblTitle.getBounds().x+15, lblTitle.getBounds().y+lblTitle.getBounds().height+5, 300, 20);
		titleRealArea.add(lblMsg);
		
		chatViewport = new JPanel();
		chatViewport.setBackground(Color.WHITE);
		
		chatArea = new JScrollPane(chatViewport);
		chatArea.setBorder(null);
		rightArea.add(chatArea, BorderLayout.CENTER);
		chatArea.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				chatViewport.setPreferredSize(new Dimension(100, ((ListLayout) chatViewport.getLayout()).getListHeight()));
			}
			
		});
		chatArea.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			
			private int lastMaximum=0;
			private int lastScroll=0;
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int currentMaximum = e.getAdjustable().getMaximum();
				lastScroll=e.getValue();
				
//				System.out.println("dif: "+(currentMaximum-lastScroll));
				
				if(currentMaximum != lastMaximum){ //REVER MELHOR O COMPORTAMENTO DA SCROLLBAR
					chatArea.getVerticalScrollBar().setValue(currentMaximum);
					lastMaximum=currentMaximum;
				}
			}
		});
		
		chatViewport.setLayout(new ListLayout(ListLayout.LIST_HORIZONTAL));
		
		typingArea = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage bbSpch = ImageManager.getImage("typingArea.png");
				g.drawImage(bbSpch, 4, 5, 27, 105, 0, 0, 23, 100, null);
				g.drawImage(bbSpch, this.getWidth()-17, 5, this.getWidth()-10, 105, 24, 0, 30, 100, null);
				g.drawImage(bbSpch, 27, 5, this.getWidth()-17, 105, 23, 0, 24, 100, null);
				super.paint(g);
			}
			
		};
		typingArea.setLayout(new BorderLayout());
//		typingArea.setBackground(Color.WHITE);
		typingArea.setOpaque(false);
		rightArea.add(typingArea, BorderLayout.SOUTH);
		
		textArea = new JTextArea();
		textArea.setOpaque(false);
		textArea.setFont(lblMsg.getFont());
		textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMsgOrBreakLine");
		textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "sendMsgOrBreakLine");
		textArea.getActionMap().put("sendMsgOrBreakLine", new AbstractAction("sendMsgOrBreakLine"){

			@Override
			public void actionPerformed(ActionEvent e) {
				int modifiers = e.getModifiers();
				if(modifiers==0 && textArea.getText().length()>0 && !locked){ //ENTER
					User me = item.getList().getMainWindow().getCurrentUser();
					ChatMessage msg = new ChatMessage(here, me.getNickname(), me.getNick(), textArea.getText(), Color.WHITE);
					appendMessage(msg);
					
					Connector c = item.getList().getMainWindow().getConnector(); 
					QSProtocol p = new QSProtocol();
					p.add("op", Connector.MESSAGE);
					p.add("type", "N");
					p.add("selfMID", msg.getMID());
					p.add("msg", textArea.getText());
					p.add("friend", item.getNickname());
					c.sendProtocol(p);
					
					textArea.setText("");
				} else if(modifiers==1) { //SHIFT+ENTER
					textArea.append("\n");
				}
			}
			
		});
		
		typingScroll = new JScrollPane(textArea);
		typingScroll.setBorder(null);
		typingScroll.getViewport().setOpaque(false);
		typingScroll.setOpaque(false);
		typingScroll.setPreferredSize(new Dimension(1, me.getRealSize()));
		typingArea.add(typingScroll, BorderLayout.CENTER);
		
		for(int i=0,ii=dummies.length;i<ii;i++){
			typingArea.add(dummies[i], PLACES[i]);
		}
		 
		fixViewportSize();
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e){
				ChatWindow rw = item.getList().getDeadWindows().remove(item.getNickname());
				item.setWindow(null);
			}
			
		});
		
	}
	
	private int getTitleHeight(){
		return (int)(person.getRealSize()*0.7)+AVATAR_GAPS*2;
	}
	
	public void appendMessage(ChatMessage msg){
		chatViewport.add(msg);
		msg.fixRealHeight();
		fixViewportSize();
		repaint();
	}
	
	public void fixViewportSize(){
//		chatViewport.revalidate();
		chatViewport.getLayout().layoutContainer(chatViewport);
		chatViewport.setPreferredSize(new Dimension(100, ((ListLayout) chatViewport.getLayout()).getListHeight()));
	}
	
	public ChatMessage getMessage(String mID){
		return getMessage(chatViewport, mID);
	}
	
	public ChatMessage getMessage(JPanel container, String mID){
		if(container == null) return null;
		for(int i=0,ii=container.getComponentCount();i<ii;i++){
			try {
				ChatMessage m = (ChatMessage) container.getComponent(i);
				if(m.getMID().equals(mID))
					return m;
				else
					m = getMessage(m.getIndentedMsgs(), mID);
				
				if(m != null) return m;
			} catch (ClassCastException e){
//				return null;
			}
		}
		return null;
	}
	
	public ContactListItem getItem(){
		return this.item;
	}
	
	public JLabel getLblTitle(){
		return this.lblTitle;
	}
	
	public JLabel getLblMsg(){
		return this.lblMsg;
	}
	
	public Avatar getMe(){
		return me;
	}
	
	public Avatar getPerson(){
		return person;
	}
	
	public void lock(){
		locked = true;
	}
	
	public void unlock(){
		locked = false;
	}
	
	public boolean isLocked(){
		return this.locked;
	}
	
}
