import java.time.LocalDateTime;

// replace String -> Image class later when defined
class ImgMessage implements Message {
	UserInterface u;
	LocalDateTime createTime = null;
	String img;
	
	public static final String type = "Image";
	
	ImgMessage(UserInterface u, String img) {
		this.u = u;
		this.img = img;
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
		return "Not implemented".getBytes();
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