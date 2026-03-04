import java.time.LocalDateTime;
import java.util.*;

class Chat {
	private List<Message> listOfMessages = new ArrayList<>();
	
	private String chatName;
	
	private long id;
	
	Chat(String chatName, long ID) {
		this.chatName = chatName;
		
		this.id = ID;
	}
	
	Chat(String chatName, long ID, List<Message> loadetMsges) {
		this.chatName = chatName;
		
		this.id = ID;
		
		listOfMessages = new ArrayList<Message>(loadetMsges);
	}
	
	public boolean sendMessage(Message msg) {
		
		listOfMessages.add(msg);
		
		return true;
	}
	
	public List<Message> getHistory() {
		return new ArrayList<Message>(listOfMessages);
	}
	
	public String getName() {
		return chatName;
	}
	
	public long getID() {
		return id;
	}
	
	public int getChatHistoryLength() {
		return listOfMessages.size();
	}
	
	public LocalDateTime getLastMessageSendTime() {
		if (listOfMessages.size() == 0)
			return LocalDateTime.of(1, 1, 1, 1 ,1);
		
		return listOfMessages.get(listOfMessages.size() - 1).getCreateTime();
	}
}