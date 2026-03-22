import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.util.List;
import java.util.ArrayList;

import java.io.*;

import java.awt.image.BufferedImage;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Arrays;

import java.io.PrintWriter;

class MessagePublisher implements IfileRequester, Ipublisher {
	
	URL url;
	
	MessagePublisher()  throws Exception{
		url = new URL("http://localhost:228/");
	}
	
	public int sendLogOut(UserInterface u) throws Exception {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "logOut").openConnection();
		con.setRequestMethod("PUT");
		
		con.setDoOutput(true);
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public int postMessage(UserInterface u, String msgContent, long chatID) throws Exception {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "sendMsg").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addLong(chatID);
		
		msgt.addString(msgContent);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public String postImg(UserInterface u, ImgObject img, long chatID) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(url, "sendImg").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addLong(chatID);
		
		msgt.addString(img.getExtension());
		
		os.write(msgt.getMessage());
		
		msgt.addImgToStream(img, os);
		
		os.close();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		if (status == 201) {
			InputStream is = con.getInputStream();
			
			return messageTranslater.translateString(is);
		}
		
		return null;
	}
	
	public BufferedImage requestImg(String imgName) throws Exception  {
		HttpURLConnection con = (HttpURLConnection) new URL(url, "getImg").openConnection();
		con.setRequestMethod("GET");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addString(imgName);
		
		os.write(msgt.getMessage());
		
		os.close();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			
			return messageTranslater.translateImg(is);
		}
		
		return null;
	}
	
	public long postNewChat(UserInterface u, String chatName) throws Exception {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "createChat").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addString(chatName);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		long id = -1;
		
		if (status == 201) {
			InputStream is = con.getInputStream();
			
			try {
				
				id = msgt.translateLong(is);
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				return -2;
			}
		}
		
		con.disconnect();
		
		return id;
	}
	
	public List<Chat> getAllUserChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "getChats").openConnection();
		con.setRequestMethod("GET");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		List<Chat> out = null;
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			
			out = new ArrayList<Chat>();
			
			try {
				
				long l = msgt.translateLong(is);
				
				for (int i = 0; i < l; i++) {
					
					long id = msgt.translateLong(is);
					
					String name = msgt.translateString(is);
					
					out.add(new Chat(name, id));
				}
				
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				con.disconnect();
				
				return null;
			}
		}
		
		con.disconnect();
		
		return out;
	}
	
	public List<UserInterface> getChatMembers(long chatID) throws IOException, ProtocolException, MalformedURLException  {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "getChatMembers").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(chatID);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		List<UserInterface> out = null;
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			
			out = new ArrayList<UserInterface>();
			
			try {
				
				long l = msgt.translateLong(is);
				
				for (int i = 0; i < l; i++) {
					
					long id = msgt.translateLong(is);
					
					String name = msgt.translateString(is);
					
					out.add(new User(name, id));
				}
				
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				con.disconnect();
				
				return null;
			}
		}
		
		con.disconnect();
		
		return out;
	}
	
	public List<Message> getChatHistory(long chatID, LocalDateTime lastMessageSendTime) throws IOException, ProtocolException, MalformedURLException  {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "getHistory").openConnection();
		con.setRequestMethod("GET");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(chatID);
		
		msgt.addLocalDateTime(lastMessageSendTime);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		List<Message> out = null;
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			
			out = new ArrayList<Message>();
			
			try {
				long historySize = messageTranslater.translateLong(is);
				
				for (long i = 0; i < historySize; i++) {
					out.add(messageFileEnterpreter.messageFromInputStream(is));
				}
				
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				return null;
			}
		}
		
		con.disconnect();
		
		return out;
	}
	
	public int createUser(String username, String password, String displayName) throws IOException, ProtocolException, MalformedURLException {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "createUser").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		msgt.addString(username);
		
		msgt.addString(password);
		
		msgt.addString(displayName);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public int checkLogIn(String username, String password) throws IOException, ProtocolException, MalformedURLException {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "checkLogIn").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		msgt.addString(username);
		
		msgt.addString(password);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public User logIn(String username, String password, int portUsed) throws IOException, ProtocolException, MalformedURLException  {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "logIn").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addString(username);
		
		msgt.addString(password);
		
		msgt.addLong(portUsed);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		User u = null;
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			try {
				String name = msgt.translateString(is);
				
				long id = msgt.translateLong(is);
				
				u =  new User(name, id);
			}
			catch (Exception err) {
				err.printStackTrace();
				con.disconnect();
				return null;
			}
			
		}
		
		con.disconnect();
		
		return u;
	}
	
	public List<Chat> getAvailableChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "getAvailableChats").openConnection();
		con.setRequestMethod("GET");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		List<Chat> out = null;
		
		if (status == 200) {
			InputStream is = con.getInputStream();
			
			out = new ArrayList<Chat>();
			
			try {
				
				long l = msgt.translateLong(is);
				
				for (int i = 0; i < l; i++) {
					
					long id = msgt.translateLong(is);
					
					String name = msgt.translateString(is);
					
					out.add(new Chat(name, id));
				}
				
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				con.disconnect();
				
				return null;
			}
		}
		
		con.disconnect();
		
		return out;
	}
	
	public List<UserInterface> joinChat(UserInterface u, long chatID) throws IOException, ProtocolException, MalformedURLException  {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url, "joinChat").openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addLong(chatID);
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		List<UserInterface> chatMembers = null;
		
		if (status == 201) {
			InputStream is = con.getInputStream();
			
			chatMembers = new ArrayList<UserInterface>();
			
			
			try {
				
				long userMemberCount = messageTranslater.translateLong(is);
				
				for (int i = 0; i < userMemberCount; i++) {
					
					long userID = messageTranslater.translateLong(is);
					
					String userName = messageTranslater.translateString(is);
					
					chatMembers.add(new User(userName, userID));
				}
				
			}
			catch (Exception err) {
				System.out.println("Failed to read responseBody: ");
				
				err.printStackTrace();
				
				con.disconnect();
				
				return null;
			}
			
		}
		
		con.disconnect();
		
		return chatMembers;
	}
	
}
