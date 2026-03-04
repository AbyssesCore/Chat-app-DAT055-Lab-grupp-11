import java.time.LocalDateTime;

import java.util.*;

class Model {
	
	ChatLister cl;
	User u;
	
	Chat selectedChat = null;
	
	Model (User u) {
		cl = new ChatLister();
		this.u = u;
		
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public int selectChat(Chat c) {
		System.out.print(selectedChat + " -> ");
		if (c == selectedChat || c == null) {
			return -1;
		}
		selectedChat = c;
		
		System.out.println(selectedChat);
		
		return selectedChat.getChatHistoryLength();
	}
	
	public List<Message> getCurrentChatHistory() throws Exception{
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return selectedChat.getHistory();
	}
	
	public long getCurrentChatID() throws Exception{
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return selectedChat.getID();
	}
	
	public Chat addChat(String chatName, long id) {
		return cl.createChat(u, chatName, id);
	}
	
	public Message sendMessage(String text) throws Exception{
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		Message msg = new TextMessage(u, text);
		selectedChat.sendMessage(msg);
		
		return msg;
	}
	
	public void loadMessagesToChat(List<Message> msgList) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		for (Message msg : msgList) {
			selectedChat.sendMessage(msg);
		}
	}
	
	public LocalDateTime getCurrentChatLastMessageSendTime() throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return selectedChat.getLastMessageSendTime();
	}
}