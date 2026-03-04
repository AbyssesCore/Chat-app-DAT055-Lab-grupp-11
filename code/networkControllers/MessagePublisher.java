import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.util.List;
import java.util.ArrayList;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class MessagePublisher {
	
	public int sendLogOut(UserInterface u) throws Exception {
		URL url = new URL("http://localhost:228/logOut");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("PUT");
		
		con.setDoOutput(true);
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		os.writeBytes(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public int postMessage(UserInterface u, String msgContent, long chatID) throws Exception {
		URL url = new URL("http://localhost:228/sendMsg");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		String userName = u.getName();
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addLong(chatID);
		
		msgt.addString(msgContent);
		
		os.writeBytes(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public long postNewChat(UserInterface u, String chatName) throws Exception {
		URL url = new URL("http://localhost:228/createChat");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addString(chatName);
		
		os.writeBytes(msgt.getMessage());
		
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
	
	public List<Chat> getAllUsersChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException {
		URL url = new URL("http://localhost:228/getChats");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		os.writeBytes(msgt.getMessage());
		
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
	
	public List<Message> getChatHistory(long chatID, LocalDateTime lastMessageSendTime) throws IOException, ProtocolException, MalformedURLException  {
		URL url = new URL("http://localhost:228/getHistory");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(chatID);
		
		msgt.addLocalDateTime(lastMessageSendTime);
		
		os.writeBytes(msgt.getMessage());
		
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
	
	public int checkLogIn(String username, String password) throws IOException, ProtocolException, MalformedURLException {
		URL url = new URL("http://localhost:228/checkLogIn");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		msgt.addString(username);
		
		msgt.addString(password);
		
		os.writeBytes(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public User logIn(String username, String password) throws IOException, ProtocolException, MalformedURLException  {
		URL url = new URL("http://localhost:228/logIn");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		con.setRequestProperty("Content-Type", "application/text");
		
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addString(username);
		
		msgt.addString(password);
		
		os.writeBytes(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		System.out.println(status);
		
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
	
}
