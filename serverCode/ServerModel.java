import java.time.LocalDateTime;

import javax.swing.*;
import java.util.*;

import java.sql.*;

import java.io.Serializable;

class ServerModel {
	ChatLister cl;
	
	HashSet<User> onlineUsers = new HashSet<User>();
	
	private databaseConnection dbConn;
	
	ServerModel () throws SQLException, ClassNotFoundException {
		cl = new ChatLister();
		
		try {
			dbConn = new databaseConnection();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public User getOnlineUserByID(long ID) {
		for (User u : onlineUsers) {
			
			if (u.getID() == ID) {
				return u;
			}
		}
		
		return null;
	}
	
	public User logIn(String name, String password)  {
		
		SQLQueryResult res;
		
		try {
			 res = dbConn.login(name, password);
		}
		catch (Exception err) {
			System.out.println(err);
			
			return null;
		}
		
		System.out.println(res.get("error"));
		
		if (!res.getResult()) {
			return null;
		}
		
		User u = new User(res.get("display_name"), Long.parseLong(res.get("userId")));
		
		onlineUsers.add(u);
		
		System.out.println(onlineUsers);
		
		return u;
	}
	
	public void logOut(long userID) {
		
		System.out.println(userID);
		
		onlineUsers.remove(getOnlineUserByID(userID));
		System.out.println(onlineUsers);
	}
	
	public Chat createChat(long authorID, String chatName) {
		User u = getOnlineUserByID(authorID);
		
		SQLQueryResult res = dbConn.createChat(chatName, authorID);
		
		System.out.println(u);
		
		if (u == null || !res.getResult()) {
			
			return null;
		}
		
		return cl.createChat(u, chatName, Long.parseLong(res.get("chatId")));
	}
	
	public List<UserInterface> getOnlineChatUsers(Chat c) {
		return cl.getChatUsers(c);
	}
	
	public List<Chat> getAllUsersChats(UserInterface u) {
		SQLQueryResult res;
		
		try {
			res = dbConn.listChats(u.getID());
		}
		catch (Exception err){
			System.out.println(err);
			return null;
		}
		
		if (!res.getResult()) {
			return null;
		}
		
		int l = Integer.parseInt(res.get("length"));
		
		List<Chat> out = new ArrayList<Chat>();
		
		for (int i = 0; i < l; i++) {
			out.add(new Chat(cl, res.get(i+ "-chat_name"), Long.parseLong(res.get(i+ "-chat_id")) ));
		}
		
		return out;
	}
}

interface UserInterface {
	public long getID();
	public String getName();
	public void sendNotification(Message msg);
}

class User implements UserInterface, Serializable {
	private static int userCount;
	final private long persId;
	
	public String name;
	
	User(String name, long id) {
		persId = id;
		
		this.name = name;
	}
	
	public long getID() {
		return persId;
	}
	
	public String getName() {
		return name;
	}
	
	public void sendNotification(Message msg) {
		System.out.println("NOTIFIDE BY "+ msg.toString());
	}
}

interface Message extends Serializable {
	public UserInterface getUser();
	public Message clone();
	public int getMessageType();
	public JComponent getContent();
	public LocalDateTime getCreateTime();
}

class TextMessage implements Message {
	String text;
	UserInterface u;
	LocalDateTime createTime;
	
	TextMessage(UserInterface u, String text) {
		this.u = u;
		this.text = text;
		createTime = LocalDateTime.now();
	}
	
	TextMessage(TextMessage msg) {
		this.text = msg.text;
		this.u = msg.u;
		this.createTime = msg.createTime;
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public int getMessageType() {
		return 0;
	}
	
	public TextMessage clone() {
		return new TextMessage(this);
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return "User: " + u.getName() + "\n Content: " + text + "\n at: " + createTime + "\n";
	}
	
	public JComponent getContent() {
		return new JLabel("<html>"+ text +"</html>");
	}
	
	public LocalDateTime getCreateTime() {
		return createTime;
	}
}


// replace String -> Image class later when defined
class ImgMessage implements Message {
	UserInterface u;
	LocalDateTime createTime;
	String img;
	
	ImgMessage(UserInterface u, String img) {
		this.u = u;
		this.img = img;
		createTime = LocalDateTime.now();
	}
	
	ImgMessage(ImgMessage msg) {
		this.img = msg.img;
		this.u = msg.u;
		this.createTime = msg.createTime;
	}
	
	public UserInterface getUser() {
		return u;
	}
	
	public int getMessageType() {
		return 1;
	}
	
	@Override
	public ImgMessage clone() {
		return new ImgMessage(this);
	}
	
	public JComponent getContent() {
		return new JPanel();
	}
	
	public LocalDateTime getCreateTime() {
		return createTime;
	}
}


class Chat {
	private List<Message> listOfMessages = new ArrayList<>();
	private ChatLister cl;
	
	private String chatName;
	
	private long id;
	
	Chat(ChatLister cl, String chatName, long id) {
		this.cl = cl;
		this.chatName = chatName;
		
		this.id = id;
	}
	
	public long getID() {
		return id;
	}
	
	public boolean sendMessage(Message msg) {
		
		List<UserInterface> chatUsers = cl.getChatUsers(this);
		
		UserInterface sender = msg.getUser();
		
		if (!chatUsers.contains(sender)) {
			return false;
		}		
		
		listOfMessages.add(msg);
		
		for (UserInterface u : chatUsers) {
			if (u != sender) {
				u.sendNotification(msg);
			}
		}
		
		return true;
	}
	
	public List<Message> getHistory() {
		return new ArrayList<Message>(listOfMessages);
	}
	
	public String getName() {
		return chatName;
	}
}

class ChatLister {
	
	private HashMap<Chat, List<UserInterface>> chatsToUserMap = new HashMap<Chat, List<UserInterface>>();
	
	public List<UserInterface> getChatUsers(Chat c) {
		return chatsToUserMap.get(c);
	}
	
	public Chat getChatByID(long id) {
		for (Chat c : chatsToUserMap.keySet()) {
			if (c.getID() == id) {
				return c;
			}
		}
		
		return null;
	}
	
	public List<Chat> userIsPartOf(UserInterface u) {
		
		List<Chat> out = new ArrayList<Chat>();
		
		for ( HashMap.Entry<Chat, List<UserInterface>> e : chatsToUserMap.entrySet() ) {
			if (e.getValue().contains(u) ) {
				out.add(e.getKey());
			}
		}
		
		return out;
	}
	
	public boolean joinChat(Chat ch, UserInterface u) {
		if (!chatsToUserMap.containsKey(ch)) {
			return false;
		}
		
		List<UserInterface> uList = chatsToUserMap.get(ch);
		
		if (uList.contains(u)) {
			return false;
		}
		
		uList.add(u);
		return true;
	}
	
	public Set<Chat> getAllChats() {
		return chatsToUserMap.keySet();
	}
	
	public Chat createChat(UserInterface u, String ChatName, long id) {
		Chat nc = new Chat(this, ChatName, id);
		List<UserInterface> nui = new ArrayList<UserInterface>();
		
		nui.add(u);
		
		chatsToUserMap.put(nc, nui);
		
		return nc;
	}
}


