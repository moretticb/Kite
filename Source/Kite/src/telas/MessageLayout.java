//////////////////////////////////////////////////////////////////////////////
//
//      MessageLayout.java - Kite Messenger - Threaded chat
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
