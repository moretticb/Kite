package telas;

import imgs.ImageManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import data.Connector;
import data.Preferences;
import data.QSProtocol;
import data.User;

public class MainWindow extends JFrame {
	
	private final int SCREEN_INITIAL = 0;
	private final int SCREEN_CONTACTS = 1;
	private final Dimension[] minimum = {new Dimension(340, 330), new Dimension(640, 310)};
	private JPanel[] screens;
	private int currentScreen;
	
	private Connector connector;
	private User currentUser;
	
	private Container c;
	
	private MainWindow here;
	private RegisterForm form;
	private PreferencesForm prefs;
	
	private List<ContactListItem> usersToList;
	
	private static final Dimension ICON_SIZE = new Dimension(77, 76);
	
	private static final BufferedImage ICON_SPRITE = ImageManager.getImage("icons.png");
	private static final BufferedImage KITE_CONTACTS = ImageManager.getImage("kitecontacts.png");
	private static final BufferedImage KITE_CONTACTS_BOTTOM = ImageManager.getImage("kitecontacts_bottom.png");
	private static final BufferedImage PANEL_SPRITE = ImageManager.getImage("panelSprite.png");
	
	private final ActionListener alist = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == btnConnect){
				btnConnect.setEnabled(false);
				try {
					connector = new Connector(here);
					System.gc();
					QSProtocol p = new QSProtocol();
					p.add("op", Connector.AUTH);
					p.add("nickname", txtNick.getText());
					p.add("senha", txtSenha.getText());
					connector.sendProtocol(p);
				} catch (IOException ex){
					JOptionPane.showMessageDialog(here, String.format("Unable to connect to %s:%d. Access Preferences section and make sure the server is correctly configured and try again.", getPreferences().getHost(), getPreferences().getPort()), "Connection failed", JOptionPane.ERROR_MESSAGE);
				} finally {
					btnConnect.setEnabled(true);
				}
			} else if(e.getSource() == btnConfig){
				showPrefsScreen();
			} else if(e.getSource() == prefs.getCancel() || e.getSource() == prefs.getSave()){
				hidePrefsScreen();
			} else if(e.getSource() == btnRegister){
				form.setVisible(true);
			} else if(e.getSource() == logoutBtn){
				resetContactsScreen();
				getCurrentPanel().setVisible(false);
				setCurrentScreen(SCREEN_INITIAL);
				getCurrentPanel().setVisible(true);
				setResizable(false);
				setMinimumSize(minimum[currentScreen]);
				setSize(minimum[currentScreen]);
				setIconImage(getIcon(0));
				setLocationRelativeTo(getRootPane());
			} else if(e.getSource() == addUser){
				String nickToAdd = JOptionPane.showInputDialog(screens[0], "Insert your friend's nickname", "Add user", JOptionPane.QUESTION_MESSAGE);
				if(nickToAdd != null){
					QSProtocol p = new QSProtocol();
					p.add("op", Connector.ADD_USER);
					p.add("requested", nickToAdd);
					connector.sendProtocol(p);
				}
			}
		}
		
	};
	
	private final MouseListener mlist = new MouseAdapter(){ //listener para o panel SCREEN_CONTACTS

		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getSource() == txtNickname){
				txtNickname.setOpaque(true);
				txtNickname.setEditable(true);
				txtNickname.setToolTipText("Press enter to change");
			} else if(e.getSource() == txtMsg){
				txtMsg.setOpaque(true);
				txtMsg.setEditable(true);
				txtMsg.setToolTipText("Press enter to change");
			}
		}
		
	};
	
	private final AbstractAction changeTxtAction = new AbstractAction("sendMsgOrBreakLine"){

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == txtNickname){
				txtNickname.setOpaque(false);
				txtNickname.setEditable(false);
				txtNickname.setCaretPosition(0);
				txtNickname.setToolTipText("Click to change");
				currentUser.setNick(txtNickname.getText(), User.UPD_FROM_CLIENT);
			} else if(e.getSource() == txtMsg){
				txtMsg.setOpaque(false);
				txtMsg.setEditable(false);
				txtMsg.setCaretPosition(0);
				txtMsg.setToolTipText("Click to change");
				currentUser.setMsg(txtMsg.getText(), User.UPD_FROM_CLIENT);
			}
		}
		
	};
	
	private final ComponentListener clist = new ComponentAdapter(){
		public void componentResized(ComponentEvent arg0) {
			if(contactList != null) contactList.fixHeight();
		}
	};
	
	/* ATTRIBUTOS PARA TELA INICIAL */
	private JTextField txtNick;
	//colocar combo para bandeiras
	private JTextField txtSenha;
	private JButton btnConnect;
	private JButton btnConfig;
	private JButton btnRegister;
	
	/* ATTRIBUTOS PARA TELA DE CONTATOS */
	private JPanel headerArea;
	private JPanel headerLeftArea;
	private JPanel headerLeft;
	private JPanel headerRightArea;
	private JPanel headerRight;
	
	private Avatar myAvatar;
//	private JLabel lblNickname;
//	private JLabel lblMsg;
	
	private JTextField txtNickname;
	private JTextField txtMsg;
	
	private JButton logoutBtn;
	private JButton addUser;
	
	private ContactList contactList;
	private JPanel contacts;
	
	private JPanel footerArea;
	private JPanel footerLeftArea;
	private JPanel footerLeft;
	private JPanel footerRightArea;
	private JPanel footerRight;
	
	private JTextField currentServer;
	
	public MainWindow(){
		super("Kite");
		setIconImage(getIcon(0));
		this.here=this;
//		setLayout(null);
		
		form = new RegisterForm(here);
		
		screens = new JPanel[2];
		screens[0] = new JPanel(){ //janela inicial
			
			public void paint(Graphics g){
				BufferedImage bg = ImageManager.getImage("kitebg.png");
				g.drawImage(bg, 0, 0, null);
				super.paint(g);
			}
			
		};
		screens[0].setOpaque(false);
		screens[1] = new JPanel();
//		screens[1].setOpaque(false);
//		addComponentListener(clist);
		
		prefs = new PreferencesForm();
		prefs.getCancel().addActionListener(alist);
		prefs.getSave().addActionListener(alist);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		c = getContentPane();
		initInitialScreen();

		usersToList = new ArrayList<ContactListItem>();
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public BufferedImage getIcon(int index){
		return ICON_SPRITE.getSubimage(ICON_SIZE.width*index, 0, ICON_SIZE.width, ICON_SIZE.height);
	}
	
	public void clearLoginFields(){
		txtNick.setText("");
		txtSenha.setText("");
	}
	
	public void resetContactsScreen(){
		currentServer.setText("");
		myAvatar.setFlag(0);
		myAvatar.changePic(null);
		txtNickname.setText("");
		txtMsg.setText("");
		contactList.clear();
		
		connector.terminate();
	}
	
	private void showPrefsScreen(){
		getCurrentPanel().setVisible(false);
		if(prefs.getBounds().width == 0){
			prefs.setVisible(true);
			prefs.setBounds(0, 0, getCurrentDimension().width, getCurrentDimension().height);
			c.add(prefs);
		} else {
			prefs.setVisible(true);
		}
	}
	
	private void hidePrefsScreen(){
		prefs.setVisible(false);
		getCurrentPanel().setVisible(true);
	}
	
	private void initInitialScreen(){
		setResizable(false);
		setCurrentScreen(SCREEN_INITIAL);
		JPanel initialPanel = getCurrentPanel();
		initialPanel.setBounds(0, 0, getCurrentDimension().width, getCurrentDimension().height);
		initialPanel.setLayout(null);
		initialPanel.setBackground(Color.RED);
		c.add(initialPanel);
		
		initialPanel.add(new JTextField()); //segurar o foco
		
		txtNick = new TxtField("Enter your nickname", false);
		txtNick.setBounds(getCurrentDimension().width/2-100+8, 192, 200, 25);
		initialPanel.add(txtNick);
		
		txtSenha = new PwdField("Enter your password", true);
		txtSenha.setBounds(getCurrentDimension().width/2-100+8, 221, 200, 25);
		initialPanel.add(txtSenha);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(getCurrentDimension().width/2-37, 255, 75, 25);
		btnConnect.addActionListener(alist);
		initialPanel.add(btnConnect);

		btnConfig = new JButton("");
		btnConfig.setToolTipText("Preferences");
		btnConfig.setContentAreaFilled(false);
		btnConfig.setBounds(307, 267, 28, 28);
		btnConfig.addActionListener(alist);
		initialPanel.add(btnConfig);

		btnRegister = new JButton("Sign up");
		btnRegister.setToolTipText("Click here to create an account.");
		btnRegister.setFont(btnRegister.getFont().deriveFont(Font.ITALIC));
		btnRegister.setContentAreaFilled(false);
		btnRegister.setBorder(null);
		btnRegister.setBounds(0, 273, 55, 20);
		btnRegister.addActionListener(alist);
		initialPanel.add(btnRegister);
	}
	
	public void initContactsScreen(){
		setIconImage(getIcon(1));
		setResizable(true);
		getCurrentPanel().setVisible(false);
		
		setCurrentScreen(SCREEN_CONTACTS);
		getCurrentPanel().setVisible(true);
		setMinimumSize(getCurrentDimension());
		
		if(headerArea != null) return;
		
		JPanel contactsPanel = getCurrentPanel();
//		contactsPanel.setBackground(Color.BLUE);
		contactsPanel.setLayout(new BorderLayout());
		contactsPanel.setBounds(0, 0, getCurrentDimension().width, getCurrentDimension().height);
		contactsPanel.setPreferredSize(new Dimension(100, 100));
		c.add(contactsPanel, BorderLayout.CENTER);
		
		headerArea = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS.getSubimage(310, 0, 1, 80);
				g.drawImage(img, 0, 0, headerArea.getWidth(), 80, 0, 0, 1, 80, null);
				super.paint(g);
			}
			
		};
		headerArea.setLayout(new GridLayout(1, 2));
//		headerArea.setBackground(Color.GRAY);
		headerArea.setOpaque(false);
		contactsPanel.add(headerArea, BorderLayout.NORTH);
		
		headerLeftArea = new JPanel();
		headerLeftArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		headerLeftArea.setBackground(Color.RED);
		headerLeftArea.setOpaque(false);
		headerArea.add(headerLeftArea);
		
		headerLeft = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS.getSubimage(0, 0, 311, 80);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		headerLeft.setLayout(null);
		headerLeft.setPreferredSize(new Dimension(320, 80));
//		headerLeft.setBackground(Color.GREEN);
		headerLeft.setOpaque(false);
		headerLeftArea.add(headerLeft);
		
		myAvatar = new Avatar(currentUser, true, Avatar.AVATAR_MEDIUM);
		myAvatar.setLayout(null);
		myAvatar.setBounds(5, 5, myAvatar.getRealSize(), myAvatar.getRealSize());
		headerLeft.add(myAvatar);
		
		txtNickname = new JTextField();
		txtNickname.setToolTipText("Click to change");
		txtNickname.setOpaque(false);
		txtNickname.setBorder(null);
		txtNickname.setEditable(false);
		
		txtNickname.addMouseListener(mlist);
		txtNickname.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "changeTxt");
		txtNickname.getActionMap().put("changeTxt", changeTxtAction);
		
		txtNickname.setBounds(myAvatar.getX()*2+myAvatar.getWidth(), 3, 200, 30);
		txtNickname.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		headerLeft.add(txtNickname);
		
		txtMsg = new JTextField();
		txtMsg.setToolTipText("Click to change");
		txtMsg.setOpaque(false);
		txtMsg.setBorder(null);
		txtMsg.setEditable(false);
		
		txtMsg.addMouseListener(mlist);
		txtMsg.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "changeTxt");
		txtMsg.getActionMap().put("changeTxt", changeTxtAction);
		
		txtMsg.setBounds(myAvatar.getX()*2+myAvatar.getWidth(), txtNickname.getY()+txtNickname.getHeight()+8, txtNickname.getWidth(), 20);
		headerLeft.add(txtMsg);
		
		headerRightArea = new JPanel();
		headerRightArea.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//		headerRightArea.setBackground(Color.MAGENTA);
		headerRightArea.setOpaque(false);
		headerArea.add(headerRightArea);
		
		headerRight = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS.getSubimage(313, 0, 311, 80);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		headerRight.setLayout(null);
		headerRight.setPreferredSize(new Dimension(311, 80));
//		headerRight.setBackground(Color.GREEN);
		headerRight.setOpaque(false);
		headerRightArea.add(headerRight);
		
		logoutBtn = new JButton(){
			
			public void paint(Graphics g){
				BufferedImage img = PANEL_SPRITE.getSubimage(24, 0, 24, 24);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		logoutBtn.setContentAreaFilled(false);
		logoutBtn.setToolTipText("Logout");
		logoutBtn.setBounds(280, 20, 24, 24);
		logoutBtn.addActionListener(alist);
		headerRight.add(logoutBtn);
		
		addUser = new JButton(){
			
			public void paint(Graphics g){
				BufferedImage img = PANEL_SPRITE.getSubimage(0, 0, 24, 24);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		addUser.setContentAreaFilled(false);
		addUser.setToolTipText("Add a friend");
		addUser.setBounds(logoutBtn.getBounds().x-10-logoutBtn.getWidth(), logoutBtn.getBounds().y, logoutBtn.getWidth(), logoutBtn.getHeight());
		addUser.addActionListener(alist);
		headerRight.add(addUser);
		
		contactList = new ContactList(here);
		contactList.fixHeight();
		contactList.addComponentListener(clist);
		contactsPanel.add(contactList, BorderLayout.CENTER);
		
		while(usersToList.size() > 0){
			ContactListItem item = usersToList.remove(0);
			item.setList(contactList);
			appendUserToList(item);
		}
		
		footerArea = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS_BOTTOM.getSubimage(310, 0, 1, 30);
				g.drawImage(img, 0, 0, footerArea.getWidth(), 30, 0, 0, 1, 30, null);
				super.paint(g);
			}
			
		};
		footerArea.setLayout(new GridLayout(1, 2));
//		footerArea.setBackground(Color.GRAY);
		footerArea.setOpaque(false);
		contactsPanel.add(footerArea, BorderLayout.SOUTH);
		
		footerLeftArea = new JPanel();
		footerLeftArea.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		footerLeftArea.setBackground(Color.RED);
		footerLeftArea.setOpaque(false);
		footerArea.add(footerLeftArea);
		
		footerLeft = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS_BOTTOM.getSubimage(0, 0, 311, 30);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		footerLeft.setLayout(null);
		footerLeft.setPreferredSize(new Dimension(320, 30));
		footerLeft.setBackground(Color.GREEN);
		footerLeft.setOpaque(false);
		footerLeftArea.add(footerLeft);
		
		footerRightArea = new JPanel();
		footerRightArea.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//		footerRightArea.setBackground(Color.MAGENTA);
		footerRightArea.setOpaque(false);
		footerArea.add(footerRightArea);
		
		footerRight = new JPanel(){
			
			public void paint(Graphics g){
				BufferedImage img = KITE_CONTACTS_BOTTOM.getSubimage(313, 0, 311, 30);
				g.drawImage(img, 0, 0, null);
				super.paint(g);
			}
			
		};
		footerRight.setLayout(null);
		footerRight.setPreferredSize(new Dimension(311, 30));
//		footerRight.setBackground(Color.GREEN);
		footerRight.setOpaque(false);
		footerRightArea.add(footerRight);
		
		currentServer = new JTextField(String.format("%s:%s", prefs.getPreferences().getHost(),prefs.getPreferences().getPort()));
		currentServer.setEditable(false);
		currentServer.setOpaque(false);
		currentServer.setBorder(null);
		currentServer.setBounds(7, 9, 200, 20);
		footerLeft.add(currentServer);
		
		validate();
	}
	
	private JPanel getCurrentPanel(){
		if(screens[currentScreen] == null) screens[currentScreen] = new JPanel();
		return screens[currentScreen];
	}
	
	private Dimension getCurrentDimension(){
		return minimum[currentScreen];
	}
	
	private void setCurrentScreen(int currentScreen){
		this.currentScreen = currentScreen;
		setSize(minimum[currentScreen]);
		setLocationRelativeTo(getRootPane());
	}
	
	public User getCurrentUser(){
		return this.currentUser;
	}
	
	public void setCurrentUser(User currentUser){
		this.currentUser=currentUser;
	}
	
	public JTextField getLblNickname(){
		return this.txtNickname;
	}
	
	public JTextField getLblMsg(){
		return this.txtMsg;
	}
	
	public List<ContactListItem> getUsersToList(){
		return this.usersToList;
	}
	
	public ContactList getContactList(){
		return this.contactList;
	}
	
	public void appendUserToList(String nickname, String nick, String msg, BufferedImage img, int flag, int status){
		ContactListItem item = new ContactListItem(contactList, nickname, nick, msg, flag, img, status);
		appendUserToList(item);
	}
	
	public void appendUserToList(ContactListItem item){
		if(contactList != null){
			if(contactList.addItem(item.getNickname(), item))
				contactList.validate();
		} else {
			usersToList.add(item);
		}
	}
	
	public void removeUserFromList(String nickname){
		ContactListItem removed = contactList.removeItem(nickname);
		if(removed != null){
			removed.setStatus(Connector.STATUS_OFFLINE);
			if(removed.getWindow() != null){
				removed.getWindow().lock();
			}
		}

		contactList.validate();
		contactList.repaint();
	}
	
	public Connector getConnector(){
		return this.connector;
	}
	
	public RegisterForm getRegisterForm(){
		return this.form;
	}
	
	public Avatar getMyAvatar(){
		return this.myAvatar;
	}
	
	public Preferences getPreferences(){
		return prefs.getPreferences();
	}
	
	public JButton getBtnConnect(){
		return this.btnConnect;
	}
	
}
