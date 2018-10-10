//////////////////////////////////////////////////////////////////////////////
//
//      ImageManager.java - Kite Messenger - Threaded chat
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


package imgs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageManager {

	private static ImageManager instance;
	
	public static BufferedImage getImage(String filename){
		if(instance == null){
			instance = new ImageManager();
			return ImageManager.getImage(filename);
		} else {
			try {
			return ImageIO.read(instance.getClass().getResourceAsStream(filename));
			} catch (IOException e){
				return null;
			}
		}
	}
	
	public static BufferedImage openImage(File imageFile){
		try {
			return ImageIO.read(imageFile);
		} catch (IOException ex){
			return null;
		}
	}
	
	public static String img2String(BufferedImage img){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "PNG", out);
			return Base64.encodeBase64String(out.toByteArray());
		} catch (IOException e){
			return null;			
		}
	}
	
	public static BufferedImage string2Image(String str){
		ByteArrayInputStream in = new ByteArrayInputStream(Base64.decodeBase64(str));
		try {
			return ImageIO.read(in);
		} catch (IOException e){
			return null;			
		}
	}
	
}
