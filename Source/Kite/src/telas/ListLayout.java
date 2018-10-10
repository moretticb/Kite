//////////////////////////////////////////////////////////////////////////////
//
//       ListLayout.java - Kite Messenger - Threaded chat
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

public class ListLayout implements LayoutManager {
	
	public static final int LIST_HORIZONTAL = 1;
	public static final int LIST_VERTICAL = 2;
	
	private int gap;
	private int orientation;
	private int listHeight;
	
	public ListLayout(){
		this.listHeight=0;
		this.gap=0;
		orientation = LIST_HORIZONTAL;
	}
	
	public ListLayout(int orientation){
		this.listHeight=0;
		this.gap=0;
		this.orientation = orientation;
	}
	
	public ListLayout(int orientation, int gap){
		this(orientation);
		this.gap=gap;
	}

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	@Override
	public void layoutContainer(Container parent) {
		int offset=0;
		for(int i=0, ii=parent.getComponentCount(); i<ii;i++){
			Component c = parent.getComponent(i);
			Dimension theDim = c.getBounds().getSize();
			
			if(orientation == LIST_HORIZONTAL){
				c.setBounds(0, offset, parent.getWidth(), theDim.height);
				offset+=theDim.height+gap;
			} else {
				c.setBounds(offset, 0, theDim.width, parent.getHeight());
				offset+=theDim.width+gap;
			}
		}
		this.listHeight=offset;
	}
	
	public int getListHeight(){
		return this.listHeight;
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
