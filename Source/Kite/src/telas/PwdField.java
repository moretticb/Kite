//////////////////////////////////////////////////////////////////////////////
//
//         PwdField.java - Kite Messenger - Threaded chat
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

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPasswordField;

public class PwdField extends JPasswordField {
	
	private final Color CAPTION_COLOR = Color.GRAY;
	
	private String fieldCaption;
	private boolean edited;
	private boolean pwd = false;
		
	public PwdField(String caption, boolean passwordMask){
		super();
		this.fieldCaption=caption;
		this.edited = false;
		this.pwd=passwordMask;
		setOpaque(false);
		setBorder(null);
	}
	
	public void paint(Graphics g){
		if(getPassword().length==0){
			g.setColor(CAPTION_COLOR);
			g.drawString(fieldCaption, 0, 16);
		}
		super.paint(g);
	}

}
