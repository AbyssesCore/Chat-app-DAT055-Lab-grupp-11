import java.time.LocalDateTime;

import java.io.Serializable;

interface Message extends Serializable {
	public UserInterface getUser();
	public byte[] getContent();
	public LocalDateTime getCreateTime();
	public void render(View v);
	public String getMsgType();
	public void setArrivleTime(LocalDateTime time);
}