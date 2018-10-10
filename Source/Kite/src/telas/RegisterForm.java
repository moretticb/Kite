package telas;

import imgs.ImageManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import data.Connector;
import data.QSProtocol;

public class RegisterForm extends JFrame {
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 330;
	
	private static final int MALE = 0;
	private static final int FEMALE = 1;
	
	private JTextField name;
	private JTextField birthDateYear;
	private JTextField birthDateMonth;
	private JTextField birthDateDay;
	private JRadioButton[] genderRadios;
	private ButtonGroup genderGroup;
	private FlagSelector flag;
	private JTextField username;
	private JPasswordField password;
	private JPasswordField pConfirm;
	
	private JButton submit;
	
	private MainWindow w;
	
	private static final BufferedImage BACKGROUND = ImageManager.getImage("registerWindow.png");
	private static final BufferedImage BALAO = ImageManager.getImage("flagBalao.png");
	
	public RegisterForm(MainWindow window){
		super("Creating a Kite Messenger account");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setLayout(null);
		
		setContentPane(new JPanel(){
			
			public void paint(Graphics g){
				g.drawImage(BACKGROUND, 0, 0, null);
				super.paint(g);
			}
			
		});
		JPanel c = (JPanel) getContentPane();
		c.setOpaque(false);
		c.setLayout(null);
				
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.w = window;
		
		name = new JTextField("");
		name.setOpaque(false);
		name.setBorder(null);
		name.setBounds(37, 118, 180, 20);
		c.add(name);
		
		birthDateYear = new JTextField("YYYY");
		birthDateYear.setOpaque(false);
		birthDateYear.setBorder(null);
		birthDateYear.setBounds(40, 170, 30, 20);
		c.add(birthDateYear);
		
		birthDateMonth = new JTextField("MM");
		birthDateMonth.setOpaque(false);
		birthDateMonth.setBorder(null);
		birthDateMonth.setBounds(80, 170, 20, 20);
		c.add(birthDateMonth);
		
		birthDateDay = new JTextField("DD");
		birthDateDay.setOpaque(false);
		birthDateDay.setBorder(null);
		birthDateDay.setBounds(110, 170, 20, 20);
		c.add(birthDateDay);
		
		genderGroup = new ButtonGroup();
		genderRadios = new JRadioButton[]{
			new JRadioButton("   "),
			new JRadioButton("   ")
		};
		genderRadios[MALE].setBounds(155, 168, 50, 20);
		genderRadios[FEMALE].setBounds(205, 168, 50, 20);
		for(int i=0;i<genderRadios.length;i++){
			genderRadios[i].setOpaque(false);
			genderGroup.add(genderRadios[i]);
			c.add(genderRadios[i]);
		}
		
		flag = new FlagSelector();
		flag.setToolTipText("Click to change");
		flag.setBounds(224, 123);
		c.add(flag);
		flag.initCont();
		
		c.addMouseListener(flag.getList());
		
		username = new JTextField("");
		username.setOpaque(false);
		username.setBorder(null);
		username.setBounds(335, 118, 200, 20);
		c.add(username);
		
		password = new JPasswordField("");
		password.setOpaque(false);
		password.setBorder(null);
		password.setBounds(335, 170, 200, 20);
		c.add(password);
		
		pConfirm = new JPasswordField("");
		pConfirm.setOpaque(false);
		pConfirm.setBorder(null);
		pConfirm.setBounds(335, 222, 200, 20);
		c.add(pConfirm);
		
		submit = new JButton("Register");
		submit.setBounds(30, 220, 100, 25);
		c.add(submit);
		submit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				QSProtocol p = new QSProtocol();
				p.add("op", Connector.REGISTER);
				p.add("name", name.getText());
				p.add("gender", genderRadios[MALE].isSelected()?"M":"F");
				p.add("nickname", username.getText());
				p.add("password", new String(password.getPassword()));
				p.add("birthdate", String.format("%s-%s-%s", birthDateYear.getText(), birthDateMonth.getText(), birthDateDay.getText()));
				p.add("flag", flag.getFlag());
				
				Connector c = w.getConnector(); 
				if(c == null || !c.getSocket().isClosed()){
					try {
						c = new Connector(w);
						c.sendProtocol(p);
						System.out.println("MANDANDO: "+p);
					} catch (IOException ex) {
//						ex.printStackTrace();
						JOptionPane.showMessageDialog(w.getRegisterForm(), String.format("Unable to connect to %s:%d. Access Preferences section and make sure the server is correctly configured and try again.", w.getPreferences().getHost(), w.getPreferences().getPort()), "Connection failed", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}
			
		});
		
		setVisible(false);
	}
	
	public void resetForm(){
		name.setText("");
		birthDateYear.setText("");
		birthDateMonth.setText("");
		birthDateDay.setText("");
		genderRadios[0].setSelected(false);
		genderRadios[1].setSelected(false);
//		flag;
		username.setText("");
		password.setText("");
		pConfirm.setText("");
	}
	
	/* INNER CLASSES */
	
	private class FlagSelector extends JPanel {
		
		private static final int WIDTH = 16;
		private static final int HEIGHT = 11;
		private static final int CONT_DETAIL = 3;
		private static final int CONT_MARGIN = 5;
		private static final int VPORT_WIDTH = 64;
		private static final int VPORT_HEIGHT = 55;
		
		private int flag;
		
		private JPanel cont;
		private JPanel vport;
		private JScrollPane scroll;
		private Point selPoint;
		
		private MList list;
		
		public FlagSelector(){
			super();
			setBackground(Color.GREEN);
			setLayout(null);
			
			flag = 0;
			list = new MList();
			addMouseListener(list);
			
			vport = new JPanel(){
				
				public void paint(Graphics g){
					g.drawImage(Avatar.FLAG_SPRITE, 0, 0, null);
					
					if(selPoint != null){
						g.setColor(Color.YELLOW);
						Point realPoint = getRealPoint(selPoint);
						g.drawRect(realPoint.x, realPoint.y, Avatar.FLAG_SIZE.width, Avatar.FLAG_SIZE.height);
					}
					super.paint(g);
				}
				
			};
			vport.addMouseListener(list);
			vport.addMouseMotionListener(list);
			vport.setOpaque(false);
			vport.setPreferredSize(new Dimension(VPORT_WIDTH, VPORT_HEIGHT));
			scroll = new JScrollPane(vport);
			scroll.setBounds(CONT_DETAIL+CONT_MARGIN, CONT_MARGIN, VPORT_WIDTH, VPORT_HEIGHT);
			scroll.setAutoscrolls(true);
			scroll.setBorder(null);
			cont = new JPanel(){
				
				public void paint(Graphics g){
					g.drawImage(BALAO.getSubimage(0, 0, CONT_DETAIL+CONT_MARGIN, BALAO.getHeight()), 0, 0, null);
					g.drawImage(BALAO, 8, 0, CONT_DETAIL+CONT_MARGIN+VPORT_WIDTH, BALAO.getHeight(), 8, 0, 9, BALAO.getHeight(), null);
					g.drawImage(BALAO, CONT_DETAIL+CONT_MARGIN+VPORT_WIDTH, 0, CONT_DETAIL+CONT_MARGIN*2+VPORT_WIDTH, BALAO.getHeight(), 9, 0, 13, BALAO.getHeight(), null);
					
					super.paint(g);
				}
				
			};
			cont.setLayout(null);
			cont.setVisible(false);
			cont.setOpaque(false);
			cont.addMouseListener(list);
			cont.addMouseMotionListener(list);
			cont.add(scroll);
			
			this.flag = 0;
		}
		
		public Point getRealPoint(Point p){
			return new Point(p.x-p.x%Avatar.FLAG_SIZE.width, p.y-p.y%Avatar.FLAG_SIZE.height);
		}
		
		public void initCont(){
			cont.setBounds(getX()+getWidth(), getY(), CONT_DETAIL+CONT_MARGIN*2+VPORT_WIDTH, CONT_MARGIN*2+VPORT_HEIGHT);
			getParent().add(cont);
		}
		
		public int getFlag(){
			return this.flag;
		}
		
		public void setFlag(int flag){
			this.flag=flag;
		}
		
		public void setBounds(int x, int y){
			setBounds(x, y, WIDTH, HEIGHT);
		}
		
		private JPanel getCont(){
			return cont;
		}
		
		private JPanel getContVPort(){
			return vport;
		}
		
		private void setSelPoint(Point p){
			this.selPoint = p;
		}
		
		public MList getList(){
			return list;
		}
		
		public void paint(Graphics g){
			g.drawImage(Avatar.getFlag(flag), 0, 0, null);
		}
		
	}
	
	private class MList extends MouseAdapter implements MouseMotionListener {

		@Override
		public void mouseMoved(MouseEvent e) {
			if(e.getSource() == flag.getContVPort()){
				flag.setSelPoint(e.getPoint());
				flag.getCont().repaint();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == flag.getContVPort()){
				Point p = flag.getRealPoint(e.getPoint());
				int offset = (int) (p.getY()/Avatar.FLAG_SIZE.height*Avatar.FLAGS_PER_LINE + p.getX()/Avatar.FLAG_SIZE.width);
				flag.setFlag(offset);
				flag.repaint();
			}
			
			if(e.getSource() == flag){
				flag.getCont().setVisible(!flag.getCont().isVisible());
			} else {
				flag.getCont().setVisible(false);
			}
		}

		@Override
		public void mouseExited(MouseEvent e){
			if(e.getSource() == flag.getContVPort()){
				flag.setSelPoint(null);
				flag.getContVPort().repaint();
			}
		}

	}

}
