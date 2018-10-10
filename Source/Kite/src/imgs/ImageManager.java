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
