import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.awt.Dimension;

class ImgObject {
	private BufferedImage img;
	private String imgExtension;
	
	ImgObject (BufferedImage img, String extension) {
		this.img = img;
		imgExtension = extension;
	}
	
	public BufferedImage getImgContent() {
		
		return img;
	}
	
	public Dimension getImgDimension() {
		return new Dimension(img.getWidth(), img.getHeight());
	}
	
	public String getExtension() {
		return imgExtension;
	}
	
	public byte[] getImgBytes() {
		// Source - https://stackoverflow.com/a/15414490
		// Posted by Nikolay Kuznetsov, modified by community. See post 'Timeline' for change history
		// Retrieved 2026-03-06, License - CC BY-SA 3.0
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, imgExtension, baos);
			byte[] bytes = baos.toByteArray();
			
			return bytes;
		}
		catch (IOException err) {
			err.printStackTrace();
			
			return new byte[0];
		}
	}
}