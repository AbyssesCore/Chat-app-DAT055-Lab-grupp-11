import java.time.LocalDateTime;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

// replace String -> Image class later when defined
class ImgMessage implements Message {
	private UserInterface u;
	private LocalDateTime createTime = null;
	private ImgObject img;
	
	public static final String type = "Image";
	
	ImgMessage(UserInterface u, ImgObject img) {
		this.u = u;
		this.img = img;
	}
	
	ImgMessage(UserInterface u, File imgPath, String fileExtension) {
		this.u = u;
		this.img = new ImgObject(imgPath);
	}
	
	ImgMessage(ImgMessage msg) {
		this.img = msg.img;
		this.u = msg.u;
		this.createTime = msg.createTime;
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public byte[] getContent() {
		return img.getImgNamePart().getBytes();

	}
	
	public ImgObject getImg() {
		return img;
	}
	
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	
	public void render(View v) {
		v.renderImg(this);
	}
	
	public String getMsgType() {
		return type;
	}
	
	public void setArrivleTime(LocalDateTime time) {
		createTime = time;
	}
}