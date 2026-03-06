import java.time.LocalDateTime;
import java.awt.image.BufferedImage;

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
	
	ImgMessage(UserInterface u, BufferedImage img, String fileExtension) {
		this.u = u;
		this.img = new ImgObject(img, type);
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
		return img.getImgBytes();

	}
	
	public ImgObject getImg() {
		return img;
	}
	
	public BufferedImage getBufferedImage() {
		return img.getImgContent();
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