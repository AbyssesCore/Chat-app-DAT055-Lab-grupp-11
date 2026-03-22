import java.time.LocalDateTime;

import java.util.*;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.FileNotFoundException;

class Model {
	
	ChatLister cl;
	User u;
	
	Chat selectedChat = null;
	
	messageFileEnterpreter mfe;
	
	IfileRequester ifr;
	
	Model (User u, IfileRequester ifr) {
		cl = new ChatLister();
		this.u = u;
		
		mfe = new messageFileEnterpreter("clientFiles\\" + u.getID() + "\\", "Chats\\", "Images\\");
		
		this.ifr = ifr;
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public int selectChat(Chat c) throws Exception  {
		if (c == selectedChat || c == null) {
			return -1;
		}
		selectedChat = c;
		
		loadMessagesToChat(c.getID());
		
		return selectedChat.getChatHistoryLength();
	}
	
	public int selectChat(long chatID) {
		
		Chat c = cl.getChatByID(chatID, u);
		
		if (c == selectedChat || c == null) {
			return -1;
		}
		selectedChat = c;
		
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
	
	public Chat createChat(String chatName, long id) throws IOException{
		
		mfe.createChatCashe("Chat" + id);
		return cl.createChat(u, chatName, id);
	}
	
	public void loadChat(Chat c) throws Exception {
		loadChat(c, ifr.getChatMembers(c.getID()));
	}
	
	public void loadChat(Chat c, List<UserInterface> users) throws Exception {
		
		cl.loadChat(c, users);
		
		loadMessagesToChat(c.getID());
	}
	
	public TextMessage sendMessage(String text) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		TextMessage msg = new TextMessage(u, text);
		
		msg.setArrivleTime(LocalDateTime.now());
		
		loadMessageToChat(msg);
		
		return msg;
	}
	
	public ImgMessage sendImage(ImgObject img) throws Exception {
		
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		ImgMessage msg = new ImgMessage(u, img);
		
		msg.setArrivleTime(LocalDateTime.now());
		
		loadMessageToChat(msg);
		
		return msg;
	}
	
	public void loadMessagesToChat() throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		loadMessagesToChat(selectedChat.getID());
	}
	
	public void loadMessagesToChat(long chatID) throws Exception {
		Chat c = cl.getChatByID(chatID, u);
		
		if (c == null) {
			throw new Exception("no chat with this ID");
		}
		
		List<Message> msgList;
		
		if (!isChatCached(c)) {
			msgList = ifr.getChatHistory(chatID, c.getLastMessageSendTime() );
			
			
			mfe.createChatCashe("Chat" + chatID);
			mfe.saveMessage("Chat" + chatID, msgList);
		}
		else {
			msgList = mfe.loadMessages("Chat" + chatID, c.getLastMessageSendTime());
			
			if (msgList.size() > 0) {
				List<Message> newMessages = ifr.getChatHistory(chatID, msgList.get(msgList.size() - 1).getCreateTime());
				
				mfe.saveMessage("Chat" + chatID, newMessages);
			}
			else {
				
				List<Message> newMessages = ifr.getChatHistory(chatID, c.getLastMessageSendTime());
				
				msgList.addAll(newMessages);
				
				mfe.saveMessage("Chat" + chatID, newMessages);
			}
		}
		
		for (Message msg : msgList) {
			c.sendMessage(msg);
		}
	}
	
	public void loadMessageToChat(Message msg) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		loadMessageToChat(msg, selectedChat.getID());
	}
	
	public void loadMessageToChat(Message msg, long chatID) throws Exception {
		Chat c = cl.getChatByID(chatID, u);
		
		if (c == null) {
			throw new Exception("no chat with this ID");
		}
		
		c.sendMessage(msg);
		
		if (isChatCached(c)) {
			mfe.saveMessage("Chat" + chatID, msg);
		}
	}
	
	public LocalDateTime getCurrentChatLastMessageSendTime() throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return selectedChat.getLastMessageSendTime();
	}
	
	public LocalDateTime getChatLastMessageSendTime(long chatID) throws Exception {
		
		return cl.getChatByID(chatID, u).getLastMessageSendTime();
	}
	
	public UserInterface getChatMemberByID(long chatID, long userID) throws Exception{
		
		UserInterface u =  cl.getChatMemberByID(chatID, userID);
		
		if (u == null)
			throw new Exception("No such user in this chat");
		
		return u;
	}
	
	public UserInterface getCurrentChatMemberByID(long userID) throws Exception {
		if (selectedChat == null) {
			throw new Exception("no chat selected");
		}
		
		return cl.getChatMemberByID(selectedChat.getID(), userID);
	}
	
	public boolean isImgCached(String imgName) {
		return mfe.isImgCached(imgName);
	}
	
	public boolean isChatCached(Chat c) {
		return mfe.isChatCached("Chat" + c.getID());
	}
	
	public void saveImg(String imgName) throws Exception{
		if (!isImgCached(imgName))
			mfe.saveImg( ifr.requestImg(imgName), imgName);
	}
	
	public ImgObject getImgObjectByName(String imgName) throws Exception  {
		
		saveImg(imgName);
		
		return mfe.getFileRefference(imgName);
	}
	
	public void addMemberToChat(long chatID, UserInterface newUser) {
		addMemberToChat(cl.getChatByID(chatID, u), newUser);
	}
	
	public void addMemberToChat(Chat c, UserInterface u) {
		cl.joinChat(c, u);
	}
	
	public BufferedImage getImgByName(String imgName) throws IOException, Exception{
		
		saveImg(imgName);
		
		return mfe.loadImg(imgName);
	}
	
	public ImgObject saveLocalFileToCashe(ImgObject img, String imgName) throws FileNotFoundException, IOException {
		
		return mfe.copyFileToImgCashe(img.getImgPath(), imgName);
		
	}
	
	public List<Chat> getUsersJointChats() throws IOException {
		return ifr.getAllUserChats(getUser());
	}
	
	public List<Chat> getAvailableUsersChats() throws IOException {
		return ifr.getAvailableChats(getUser());
	}
	
}