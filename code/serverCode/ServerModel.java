import java.time.LocalDateTime;
import java.time.Duration;
import java.net.InetAddress;

import java.util.*;

import java.sql.*;

import java.io.Serializable;

import java.io.IOException;
import java.net.MalformedURLException;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.File;

class ServerModel {
	ChatLister cl;
	
	Hashtable<Long, OnlineUser> onlineUsers = new Hashtable<Long, OnlineUser>();
	
	private databaseConnection dbConn;
	
	private Hashtable<Long, Timer> arrangedLogIns = new Hashtable<Long, Timer>();
	
	private messageDistributor md;
	
	private final long minimalQueueTime = 200;
	
	private long max(long a, long b) {
		return a > b ? a : b;
	}
	
	ServerModel () throws SQLException, ClassNotFoundException {
		cl = new ChatLister();
		
		md = new messageDistributor();
		
		try {
			dbConn = new databaseConnection();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public OnlineUser getOnlineUserByID(long ID) {
		return onlineUsers.get(ID);
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
	
	public boolean createUser(String username, String password, String displayName) {
		return dbConn.createUser(username, displayName, password).getResult();
	}
	
	public UserInterface arrangeLogIn(String name, String password, LocalDateTime sendTime) {
		UserInterface u = tryLogIn(name, password);
		
		if (u == null) {
			return null;
		}
		
		LocalDateTime timeNow = LocalDateTime.now();
		
		long ms = max(Duration.between(sendTime, timeNow).multipliedBy(4).toMillis(), minimalQueueTime);
		
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
	
	public UserInterface logIn(String name, String password,  InetAddress addr, int port)  {
		UserInterface u = tryLogIn(name, password);
		
		System.out.println("On log in: " + LocalDateTime.now());
		
		if (u == null) {
			return null;
		}
		
		System.out.println(arrangedLogIns);
		
		if (!arrangedLogIns.containsKey(u.getID())) {
			return null;
		}
		
		removeFromAccesAlowens(u.getID());
		
		try {
			onlineUsers.put(u.getID(), new OnlineUser(u, addr, port));
		}
		catch (MalformedURLException err) {
			err.printStackTrace();
			return null;
		}
		
		System.out.println(onlineUsers);
		
		return u;
	}
	
	public void logOut(long userID) {
		
		onlineUsers.remove(userID);
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
	
	public List<UserInterface> getChatMembers(long chatID) {
		return dbConn.listChatsMembers(chatID);
	}
	
	public void sendTextMessage(long chatID, long userID, String msgContent) throws IOException {
		
		UserInterface u = getOnlineUserByID(userID);
		
		if (u == null)
			return;
		
		TextMessage msg = new TextMessage(u, msgContent);
		
		msg.setArrivleTime(LocalDateTime.now());
		
		if (msg == null)
			return;
		
		SQLQueryResult res = dbConn.saveMessage(chatID, msg);
		
		if (!res.getResult())
			return;
		
		
		for (UserInterface member : dbConn.listChatsMembers(chatID)) {
			
			if (member.getID() == userID)
				continue;
			
			OnlineUser resendTo = getOnlineUserByID(member.getID());
			
			if (resendTo == null)
				continue;
			
			System.out.println("From " + member.getID() + " got " + resendTo + " is online");
			
			md.sendTextTo(resendTo, chatID, msg);
		}
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
	
	public List<Chat> getAvailableChats(long userID) {
		return dbConn.getAvailableChats(userID);
	}
	
	public List<UserInterface> addMember(long chatID, long userID) {
		UserInterface joined = dbConn.addMember(chatID, userID, "reader");
		
		if (joined == null)
			return null;
		
		List<UserInterface> chatMembers = dbConn.listChatsMembers(chatID);
		
		for (UserInterface member : chatMembers) {
			
			if (member.getID() == userID)
				continue;
			
			OnlineUser resendTo = getOnlineUserByID(member.getID());
			
			if (resendTo == null)
				continue;
			
			System.out.println("From " + member.getID() + " got " + resendTo + " is online");
			
			md.userJoinedChat(resendTo, chatID, joined);
		}
		
		return chatMembers;
	}
	
	public String sendImg(long chatID, long senderID, BufferedImage imgCont, String type) throws IOException {
		
		UserInterface u = getOnlineUserByID(senderID);
		
		if (u == null) {	
			System.out.println("User is not online: " + senderID);
			
			return null;
		}
		
		LocalDateTime creationTime = LocalDateTime.now();
		
		String fileName = Integer.toHexString(creationTime.getDayOfYear() * 24 * 3600 + creationTime.getHour() * 3600 + creationTime.getMinute() * 60 + creationTime.getSecond()) + Integer.toHexString(creationTime.getNano()) + "." + type;
		
		File imgPath = dbConn.saveImg(imgCont, fileName);
		
		ImgMessage msg = new ImgMessage(u, new ImgObject(imgPath));
		
		msg.setArrivleTime(creationTime);
		
		dbConn.saveMessage(chatID, msg);
		
		for (UserInterface member : dbConn.listChatsMembers(chatID)) {
			
			if (member.getID() == senderID)
				continue;
			
			OnlineUser resendTo = getOnlineUserByID(member.getID());
			
			if (resendTo == null)
				continue;
			
			md.sendImgTo(resendTo, chatID, msg);
		}
		
		
		return fileName;
	}
	
	public ImgObject getImgObject(String imgName) {
		try {
			return dbConn.getFileRefference(imgName);
		}
		catch ( IOException err) {
			return null;
		}
	}
}


