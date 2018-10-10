package telas;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextField;

public class TxtField extends JTextField {
	
	private final Color CAPTION_COLOR = Color.GRAY;
	private final Color TEXT_COLOR = Color.BLACK;
	
	private String fieldCaption;
	private boolean edited;
	private boolean pwd = false;
		
	public TxtField(String caption, boolean passwordMask){
		super();
		this.fieldCaption=caption;
		this.edited = false;
		this.pwd=passwordMask;
		setOpaque(false);
		setBorder(null);
	}
	
	public void paint(Graphics g){
		if(getText().length()==0){
			g.setColor(CAPTION_COLOR);
			g.drawString(fieldCaption, 0, 16);
		}
		super.paint(g);
	}

}
