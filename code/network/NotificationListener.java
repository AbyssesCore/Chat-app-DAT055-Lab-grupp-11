import java.time.LocalDateTime;

interface NotificationListener {
	public void reciveChatTextUppdate(long senderID, long chatID, String textContent, LocalDateTime sendTime);
	public void reciveChatImgUppdate(long senderID, long chatID, String imgName, LocalDateTime sendTime);
	public void newChatMember(UserInterface newUser, long chatID );
}