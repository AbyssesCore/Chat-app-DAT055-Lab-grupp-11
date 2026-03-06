import java.time.LocalDateTime;

import java.util.*;

import java.awt.image.BufferedImage;

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
	
	public int selectChat(long chatID) {
		
		Chat c = cl.getChatByID(chatID, u);
		
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
	
	public Chat createChat(String chatName, long id) {
		return cl.createChat(u, chatName, id);
	}
	
	public void loadChat(Chat c, List<UserInterface> users) {
		cl.loadChat(c, users);
	}
	
	public TextMessage sendMessage(String text) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		TextMessage msg = new TextMessage(u, text);
		
		msg.setArrivleTime(LocalDateTime.now());
		
		selectedChat.sendMessage(msg);
		
		return msg;
	}
	
	public ImgMessage sendImage(ImgObject img) throws Exception {
		
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		ImgMessage msg = new ImgMessage(u, img);
		
		msg.setArrivleTime(LocalDateTime.now());
		
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
	
	public void loadMessagesToChat(List<Message> msgList, long chatID) throws Exception {
		Chat c = cl.getChatByID(chatID, u);
		
		if (c == null) {
			throw new Exception("no chat with this ID");
		}
		
		for (Message msg : msgList) {
			c.sendMessage(msg);
		}
	}
	
	public void loadMessageToChat(Message msg) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
	}
	
	public void loadMessageToChat(Message msg, long chatID) throws Exception {
		Chat c = cl.getChatByID(chatID, u);
		
		if (c == null) {
			throw new Exception("no chat with this ID");
		}
		
		c.sendMessage(msg);
	}
	
	public LocalDateTime getCurrentChatLastMessageSendTime() throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return selectedChat.getLastMessageSendTime();
	}
	
	public UserInterface getChatMemberByID(long chatID, long userID) {
		return cl.getChatMemberByID(chatID, userID);
	}
	
	public UserInterface getCurrentChatMemberByID(long userID) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return cl.getChatMemberByID(selectedChat.getID(), userID);
	}
}