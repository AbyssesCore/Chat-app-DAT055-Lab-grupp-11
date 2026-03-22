import java.io.File;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.awt.Dimension;

class ImgObject {
	private File imgPath;
	private String imgExtension;
	
	ImgObject (File imgPath) {
		this.imgPath = imgPath;
		
		String fileName = imgPath.toString();
		
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			imgExtension = fileName.substring(i+1);
		}
	}
	
	public File getImgPath() {
		return imgPath;
	}
	
	public String getImgNamePart() {
		return imgPath.getName();
	}
	
	public String getExtension() {
		return imgExtension;
	}
}