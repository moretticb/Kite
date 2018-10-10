//////////////////////////////////////////////////////////////////////////////
//
//     PreferencesForm.java - Kite Messenger - Threaded chat
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

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import data.Preferences;

public class PreferencesForm extends JPanel {
	
	private Preferences prefs;
	
	private JTextField txtHost;
	private JTextField txtPort;
	
	private JButton save;
	private JButton cancel;
	
	private final ActionListener alist = new ActionListener(){
		
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == save){
				setForm();
			} else if(e.getSource() == cancel){
				resetForm();
			}
		}
		
	};
	
	public PreferencesForm(){
		prefs = new Preferences("localhost", 2323);
		setLayout(null);
		
		setOpaque(false);
		
		txtHost = new JTextField(prefs.getHost());
		txtHost.setBorder(null);
		txtHost.setOpaque(false);
		txtHost.setBounds(38, 130, 205, 16);
		add(txtHost);
		
		txtPort = new JTextField(Integer.toString(prefs.getPort()));
		txtPort.setBorder(null);
		txtPort.setOpaque(false);
		txtPort.setBounds(txtHost.getBounds().x+txtHost.getBounds().width+5, txtHost.getBounds().y, 50, 16);
		add(txtPort);
		
		save = new JButton("Save");
		save.setBorder(null);
		save.setBounds(105, 245, 55, 20);
		save.addActionListener(alist);
		add(save);
		
		cancel = new JButton("Cancel");
		cancel.setBorder(null);
		cancel.setBounds(save.getBounds().x+save.getBounds().width+5, save.getBounds().y, 65, 20);
		cancel.addActionListener(alist);
		add(cancel);
	}
	
	public void setForm(){
		prefs.setHost(txtHost.getText());
		prefs.setPort(Integer.parseInt(txtPort.getText()));
	}
	
	public void resetForm(){
		txtHost.setText(prefs.getHost());
		txtPort.setText(Integer.toString(prefs.getPort()));
	}
	
	public JButton getSave(){
		return this.save;
	}
	
	public JButton getCancel(){
		return this.cancel;
	}
	
	public Preferences getPreferences(){
		return this.prefs;
	}
	
	public void paint(Graphics g){
		g.drawImage(ImageManager.getImage("kitePrefs.png"), 0, 0, null);
		super.paint(g);
	}

}
