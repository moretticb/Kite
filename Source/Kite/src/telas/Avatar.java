//////////////////////////////////////////////////////////////////////////////
//
//         Avatar.java - Kite Messenger - Threaded chat
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.User;

public class Avatar extends JPanel {
	
	public static final int AVATAR_SMALL = 1;
	public static final int AVATAR_MEDIUM = 2;
	public static final int AVATAR_LARGE = 3;
	
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_BUSY = 2;
	public static final int STATUS_OUT = 3;
	
	private final int AVATAR_SIZE = 33;
	private final int SCALE_SIZE = 88;
	
	private final int STATUS_BTN_SIZE = 11;
	
	private final int FLAG_WIDTH = 16;
	private final int FLAG_HEIGHT = 11;
	
	private boolean editable;
	private int flag;
	private int status;
	private int size;
	private BufferedImage flagImg;
	private JLabel picture;
	private JLabel flagPic;
	
	private boolean showStatusBtns;
	
	private final int[] SIZEPOS = new int[]{166, 100, 0};
//	private final Point GAP = size==AVATAR_LARGE?new Point(6,7):size==AVATAR_MEDIUM?new Point(6,5):new Point(3,3);
	private final Point[] GAP = {null, new Point(3,3), new Point(6,5), new Point(6,7)};
	
	private JFileChooser chooser;
	private final FileNameExtensionFilter FILTER = new FileNameExtensionFilter("Image files", "jpg", "png");
	
	private static BufferedImage sprite;
	public static final BufferedImage FLAG_SPRITE = ImageManager.getImage("spriteFlag.png");
	public static final Dimension FLAG_SIZE = new Dimension(16, 11);
	public static final int FLAGS_PER_LINE=4;
	
	private User currentUser;
	
	public Avatar(User currUser, boolean isEditable, int avatarSize){
		super();
		setLayout(null);
//		setBackground(Color.MAGENTA);
		setOpaque(false);
//		this.img = img;
		showStatusBtns=false;
		this.currentUser = currUser;
		this.editable = isEditable;
		this.flag = currentUser.getFlag();
		this.status = currentUser.getStatus();
		this.size = avatarSize;
		
		addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(editable){
					if(e.getPoint().x < picture.getX()+STATUS_BTN_SIZE && e.getPoint().y < picture.getY()+STATUS_BTN_SIZE*3){
						Point p = new Point(e.getPoint().x-picture.getX(), e.getPoint().y-picture.getY());
						status = 1+p.y/STATUS_BTN_SIZE;
						
						currentUser.setStatus(status, User.UPD_FROM_CLIENT);
					} else if(picture.getBounds().contains(e.getPoint()) && !flagPic.getBounds().contains(e.getPoint())){
						changePic();
					}
				}
			}
			
			public void mouseEntered(MouseEvent e){
				showStatusBtns = true;
				repaint(GAP[size].x-1, GAP[size].y, GAP[size].x+11, GAP[size].y+33);
			}
			
			public void mouseExited(MouseEvent e){
				showStatusBtns = false;
				repaint(GAP[size].x-1, GAP[size].y, GAP[size].x+11, GAP[size].y+33);
			}

		});
		
		chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(FILTER);
		
		if(sprite == null){
			sprite = ImageManager.getImage("avatarSPR.png");
		}
		
		int realSize = avatarSize==AVATAR_LARGE?88:avatarSize==AVATAR_MEDIUM?54:28;
		
		setPreferredSize(new Dimension(avatarSize*AVATAR_SIZE+avatarSize%2, avatarSize*AVATAR_SIZE+avatarSize%2));
		
		flagPic = new JLabel(new ImageIcon(getFlag(flag)));
		flagPic.setBounds(realSize-FLAG_WIDTH+GAP[size].x, realSize-FLAG_HEIGHT+GAP[size].y, FLAG_WIDTH, FLAG_HEIGHT);
		add(flagPic);
		
		picture = new JLabel(){
			
			public void paint(Graphics g){
				super.paint(g);
				if(showStatusBtns && size!=AVATAR_SMALL && editable){
					for(int i=0;i<3;i++){
						int margin = status!=i+1?0:2;
						g.drawImage(sprite, -margin, 11*i, 11-margin, 11*(i+1), sprite.getWidth()-11, sprite.getHeight()-33+11*i, sprite.getWidth(), sprite.getHeight()-33+11*(i+1), null);
					}
				}
			}
			
		};
		picture.setBounds(GAP[size].x, GAP[size].y, realSize, realSize);
		add(picture);
		
		changePic(currentUser.getImg());

	}
	
	public int getRealSize(){
		return size*AVATAR_SIZE+size%2;
	}
	
	public void paint(Graphics g){
		BufferedImage theImage = sprite.getSubimage(SIZEPOS[size-1], 100*status, getRealSize(), getRealSize());
		g.drawImage(theImage, 0, 0, null);
		
		super.paint(g);
	}
	
	public void changePic(){
		int res = chooser.showOpenDialog(this);
		if(res == 0){
			BufferedImage img = resizeImage(ImageManager.openImage(chooser.getSelectedFile()));
			changePic(img);
			currentUser.setImg(img, User.UPD_FROM_CLIENT);
		}
	}
	
	public void changePic(BufferedImage img){
		if(img != null){
			int scaleSize = picture.getSize().width;
//			currentUser.setImg(img, User.UPD_FROM_SERVER);
			picture.setIcon(new ImageIcon(resizeImage(img).getScaledInstance(scaleSize, scaleSize, Image.SCALE_SMOOTH)));
		} else {
			picture.setIcon(null);
		}
	}
	
	public BufferedImage resizeImage(BufferedImage img){
		BufferedImage subImg, imgOut;
		if(img.getWidth() < img.getHeight()){
			subImg = img.getSubimage(0, img.getHeight()/2-img.getWidth()/2, img.getWidth(), img.getWidth());
		} else {
			subImg = img.getSubimage(img.getWidth()/2-img.getHeight()/2, 0, img.getHeight(), img.getHeight());
		}
		
		Image i = subImg.getScaledInstance(SCALE_SIZE, SCALE_SIZE, Image.SCALE_SMOOTH);
		imgOut = new BufferedImage(SCALE_SIZE, SCALE_SIZE, BufferedImage.TYPE_INT_RGB);
		imgOut.getGraphics().drawImage(i, 0, 0, null);
		
		return imgOut;
	}
	
	public void setFlag(int flag){
		this.flag=flag;
		flagPic.setIcon(new ImageIcon(getFlag(flag)));
	}
	
	public void setStatus(int status){
		this.status=status;
		repaint();
	}
	
	public User getCurrentUser(){
		return this.currentUser;
	}
	
	public static BufferedImage getFlag(int index){
		int linha = index/FLAGS_PER_LINE;
		int coluna = index%FLAGS_PER_LINE;
		try {
			return FLAG_SPRITE.getSubimage(FLAG_SIZE.width*coluna, FLAG_SIZE.height*linha, FLAG_SIZE.width, FLAG_SIZE.height);
		} catch (RasterFormatException e){
			return null;
		}
	}
	
}
