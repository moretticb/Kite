package telas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	@Override
	public void layoutContainer(Container parent) {
		JLabel person = (JLabel) parent.getComponent(0);
		JTextArea msg = (JTextArea) parent.getComponent(1);
		JPanel indented = (JPanel) parent.getComponent(2);
//		JLabel unread = (JLabel) parent.getComponent(3);
		
		person.setBounds(20, 5, parent.getWidth(), 20);
		msg.setBounds(20, 20, parent.getWidth()-20, msg.getPreferredSize().height);
		msg.setSize(msg.getPreferredSize());
		indented.setBounds(40, 20+msg.getPreferredSize().height, parent.getWidth()-40, ((ChatMessage) parent).getIndentedHeight());
//		unread.setBounds(parent.getWidth()-30, 0, 20, 10);
		
		Rectangle r = parent.getBounds();
		r.setSize(r.getSize().width, 20+msg.getPreferredSize().height+indented.getBounds().getSize().height);
		parent.setBounds(r);
	}

	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		return new Dimension(0,0);
	}

	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		return new Dimension(0,0);
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
	}

}
