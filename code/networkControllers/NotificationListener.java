import java.time.LocalDateTime;

interface NotificationListener {
	public void reciveChatTextUppdate(long senderID, long chatID, String textContent, LocalDateTime sendTime);
}