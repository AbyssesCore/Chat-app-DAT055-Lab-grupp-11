import java.time.LocalDateTime;

class TextMessage implements Message {
	String text;
	UserInterface u;
	LocalDateTime createTime;
	
	public static final String type = "Text";
	
	TextMessage(UserInterface u, String text) {
		this.u = u;
		this.text = text;
	}
	
	TextMessage(TextMessage msg) {
		this.text = msg.text;
		this.u = msg.u;
		this.createTime = msg.createTime;
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public TextMessage clone() {
		return new TextMessage(this);
	}
	
	public String getText() {
		return text;
	}
	
	public byte[] getContent() {
		return text.getBytes();
	}
	
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	
	public void render(View v) {
		v.renderText(this);
	}
	
	public String getMsgType() {
		return type;
	}
	
	public void setArrivleTime(LocalDateTime time) {
		createTime = time;
	}
}