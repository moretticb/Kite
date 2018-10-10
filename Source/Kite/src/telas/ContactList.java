package telas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ContactList extends JScrollPane {
	
	private MainWindow mainWindow;
	private JPanel vport;
	private Map<String, ContactListItem> contacts;
	private Map<String, ChatWindow> deadWindows;
	
	public ContactList(MainWindow window){
		super();
		this.mainWindow=window;
		contacts = new HashMap<String, ContactListItem>();
		deadWindows = new HashMap<String, ChatWindow>();
		
		vport = new JPanel();
//		vport.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		vport.setLayout(new ListLayout(ListLayout.LIST_HORIZONTAL));
		vport.setBackground(Color.WHITE);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setViewportView(vport);
		
		addMouseMotionListener(new MouseList());
		addMouseListener(new MouseList());
		
		setBorder(null);
	}
	
	public ContactListItem getItem(String nickname){
		return contacts.get(nickname);
	}
	
	public boolean addItem(String nickname, ContactListItem item){
		if(contacts.put(nickname, item) == null){
			vport.add(item);
			if(deadWindows.get(nickname) != null) item.setWindow(deadWindows.remove(nickname));
			return true;
		}
		return false;
	}
	
	public ContactListItem removeItem(String nickname){
		ContactListItem c = contacts.get(nickname); 
		if(c != null) {
			if(c.getWindow() != null) deadWindows.put(nickname, c.getWindow());
			vport.remove(c);
			return contacts.remove(nickname);
		}
		return null;
	}
	
	public void fixHeight(){
		int height = contacts.size()*ContactListItem.HEIGHT;
		height = height>getHeight()?height:getHeight();
		vport.setPreferredSize(new Dimension(getWidth(), height));
	}
	
	
	public void validate(){
		super.validate();
		fixHeight();
	}
	
	public ContactListItem getItem(int index){
		Iterator<Entry<String, ContactListItem>> i = contacts.entrySet().iterator();
		while(i.hasNext()){
			ContactListItem listItem = i.next().getValue();
			if(listItem.getBounds().contains(10, index*listItem.getHeight())){
				return listItem;
			}
		}
		return null;
	}
	
	public MainWindow getMainWindow(){
		return this.mainWindow;
	}
	
	public Map<String, ChatWindow> getDeadWindows(){
		return this.deadWindows;
	}
	
	public void clear(){
//		vport.removeAll();
		for(int i=0, ii=vport.getComponentCount();i<ii;i++){
			ContactListItem item = (ContactListItem) vport.getComponent(i);
			if(item.getWindow() != null) item.getWindow().dispose();
			vport.remove(item);
		}
		
		vport.repaint();
		contacts.clear();
		
		Set<Entry<String, ChatWindow>> set = deadWindows.entrySet();
		Iterator<Entry<String, ChatWindow>> i = set.iterator();
		while(i.hasNext()){
			Entry<String, ChatWindow> e = (Entry<String, ChatWindow>) i.next();
			e.getValue().dispose();
			deadWindows.remove(e.getKey());
		}
	}
	
	//INNER CLASSES:
	
	private class MouseList extends MouseAdapter implements MouseMotionListener {
		
		private int last = -1;

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			int vportPos = e.getY()+((ContactList) e.getSource()).getVerticalScrollBar().getValue();
			int currentItem = vportPos/ContactListItem.HEIGHT;
			try {
				if(last!=-1){
					ContactListItem theLast = getItem(last);
					if(theLast != null) theLast.idle();
				}

				ContactListItem hovered = getItem(currentItem);
				if(hovered != null)
					hovered.hover();
				last=currentItem;
			} catch (IndexOutOfBoundsException ex){ //nao existe item com o atual valor de currentItem
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				int vportPos = e.getY()+((ContactList) e.getSource()).getVerticalScrollBar().getValue();
				int currentItem = vportPos/ContactListItem.HEIGHT;
				try {
					ContactListItem item = getItem(currentItem);
					if(item != null)
						item.openWindow(true);
				} catch (IndexOutOfBoundsException ex){
				}
			}
		}

	}
	
}
