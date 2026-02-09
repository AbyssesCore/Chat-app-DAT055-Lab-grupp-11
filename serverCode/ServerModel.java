import java.time.LocalDateTime;

import javax.swing.*;
import java.util.*;

import java.sql.*;

class ServerModel {
	ChatLister cl;
	
	HashSet<User> onlineUsers = new HashSet<User>();
	
	private Connection conn;
	
	ServerModel () throws SQLException, ClassNotFoundException {
		cl = new ChatLister();
		
		Class.forName("org.postgresql.Driver");
		
		Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
		
		conn = DriverManager.getConnection("jdbc:postgresql://localhost/", props);
	}
	
	public User getOnlineUserByName(String name) {
		for (User u : onlineUsers) {
			System.out.println(u.getName() + " " + name);
			
			if (u.getName().equals(name)) {
				return u;
			}
		}
		
		return null;
	}
	
	public User logIn(String name, String password) throws SQLException, ClassNotFoundException {
		try(PreparedStatement st = conn.prepareStatement(
            // replace this with something more useful
            "SELECT display_name FROM app_user WHERE username = ? AND password_hash = ?"
            );){
            
            st.setString(1, name);
			
			st.setString(2, password);
			
            ResultSet rs = st.executeQuery();
            
            if(rs.next()) {
				User u = new User(rs.getString("display_name"));
				
				onlineUsers.add(u);
				
				return u;
            }
			else {
              return null; 
            }
        }
	}
	
	public Chat createChat(String author, String chatName) {
		User u = getOnlineUserByName(author);
		
		System.out.println(u);
		
		if (u == null) {
			
			return null;
		}
		
		cl.createChat(u, chatName);
	}
	
}

interface UserInterface {
	public int getID();
	public String getName();
	public void sendNotification(Message msg);
}

class User implements UserInterface {
	private static int userCount;
	final private int persId;
	
	public String name;
	
	User(String name) {
		persId = userCount++;
		
		this.name = name;
	}
	
	public int getID() {
		return persId;
	}
	
	public String getName() {
		return name;
	}
	
	public void sendNotification(Message msg) {
		System.out.println("NOTIFIDE BY "+ msg.toString());
	}
}

interface Message {
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
	
	Chat(ChatLister cl, String chatName) {
		this.cl = cl;
		this.chatName = chatName;
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
	
	public Chat createChat(UserInterface u, String ChatName) {
		Chat nc = new Chat(this, ChatName);
		List<UserInterface> nui = new ArrayList<UserInterface>();
		
		nui.add(u);
		
		chatsToUserMap.put(nc, nui);
		
		return nc;
	}
}


