import java.time.LocalDateTime;
import java.time.Duration;
import java.net.InetAddress;

import java.util.*;

import java.sql.*;

import java.io.Serializable;

import java.io.IOException;

class ServerModel {
	ChatLister cl;
	
	HashSet<OnlineUser> onlineUsers = new HashSet<OnlineUser>();
	
	private databaseConnection dbConn;
	
	private Hashtable<Long, Timer> arrangedLogIns = new Hashtable<Long, Timer>();
	
	ServerModel () throws SQLException, ClassNotFoundException {
		cl = new ChatLister();
		
		try {
			dbConn = new databaseConnection();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OnlineUser getOnlineUserByID(long ID) {
		for (OnlineUser u : onlineUsers) {
			
			if (u.getID() == ID) {
				return u;
			}
		}
		
		return null;
	}
	
	private UserInterface tryLogIn(String name, String password) {
		SQLQueryResult res;
		
		try {
			 res = dbConn.login(name, password);
		}
		catch (Exception err) {
			err.printStackTrace();
			
			return null;
		}
		
		if (!res.getResult()) {
			System.out.println(res.get("error"));
			
			return null;
		}
		
		User u = new User(res.get("display_name"), Long.parseLong(res.get("userId")));
		
		return u;
	}
	
	public UserInterface arrangeLogIn(String name, String password, LocalDateTime sendTime) {
		UserInterface u = tryLogIn(name, password);
		
		if (u == null) {
			return null;
		}
		
		LocalDateTime timeNow = LocalDateTime.now();
		
		long ms = Duration.between(sendTime, timeNow).multipliedBy(4).toMillis();
		
		Timer accesAlowensDeadline = new Timer();
		
		long userID = u.getID();
		
		accesAlowensDeadline.schedule(new TimerTask() {
			@Override
			public void run() {
				removeFromAccesAlowens(userID);
				
			}
		}, ms);
		
		arrangedLogIns.put(userID, accesAlowensDeadline);
		
		return u;
	}
	
	public UserInterface logIn(String name, String password, InetAddress addr, int port)  {
		UserInterface u = tryLogIn(name, password);
		
		if (u == null) {
			return null;
		}
		
		if (!arrangedLogIns.containsKey(u.getID())) {
			return null;
		}
		
		removeFromAccesAlowens(u.getID());
		
		onlineUsers.add(new OnlineUser(u, addr, port));
		
		System.out.println(onlineUsers);
		
		return u;
	}
	
	public void logOut(long userID) {
		
		onlineUsers.remove(getOnlineUserByID(userID));
		System.out.println(onlineUsers);
	}
	
	public Chat createChat(long authorID, String chatName) {
		UserInterface u = getOnlineUserByID(authorID);
		
		if (u == null) {
			return null;
		}
		
		SQLQueryResult res;
		
		try {
			res = dbConn.createChat(chatName, authorID);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (!res.getResult()) {
			
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
			err.printStackTrace();
			return null;
		}
		
		if (!res.getResult()) {
			return null;
		}
		
		int l = Integer.parseInt(res.get("length"));
		
		List<Chat> out = new ArrayList<Chat>();
		
		for (int i = 0; i < l; i++) {
			out.add(new Chat(res.get(i+ "-chat_name"), Long.parseLong(res.get(i+ "-chat_id")) ));
		}
		
		return out;
	}
	
	public void sendTextMessage(long chatID, long userID, String msgContent) throws IOException {
		
		TextMessage msg = new TextMessage(getOnlineUserByID(userID), msgContent);
		
		dbConn.saveMessage(chatID, msg);
	}
	
	public List<byte[]> getChatHistory(long chatID, LocalDateTime time) {
		List<byte[]> res;
		
		try {
			res = dbConn.loadChatHistory(chatID, time == null ? LocalDateTime.MIN : time);
		}
		catch (Exception err){
			err.printStackTrace();
			return null;
		}
		
		if (res == null) {
			return null;
		}
		
		return res;
	}
	
	private void removeFromAccesAlowens(long userID) {
		Timer timerToCancel = arrangedLogIns.get(userID);
		
		if (timerToCancel != null) {
			arrangedLogIns.remove(userID);
			
			timerToCancel.cancel();
			
			return;
		}
		
		
		System.out.println("Error: " + LocalDateTime.now());
		
	}
}


