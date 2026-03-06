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

class MessagePublisher {
	
	public int sendLogOut(UserInterface u) throws Exception {
		URL url = new URL("http://localhost:228/logOut");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
		URL url = new URL("http://localhost:228/sendMsg");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
	
	public int postImg(UserInterface u, ImgObject img, long chatID) throws Exception {
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLong(u.getID());
		
		msgt.addLong(chatID);
		
		byte[]arr = img.getImgBytes();
		
		int len = arr.length;
		
		msgt.addLong(len);
		
		msgt.addImg(img);
		
		URL url = new URL("http://localhost:228/sendImg");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		
		String boundary = "----" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		
		Path imagePath = Paths.get("C:\\Users\\kiril\\Desktop\\kirill\\imgOverlay\\img.png");
        byte[] imageBytes = Files.readAllBytes(imagePath);
		
		try (OutputStream output = new BufferedOutputStream(con.getOutputStream());
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
			 FileInputStream fileInput = new FileInputStream(imagePath.toFile())) {
				 
            // File part header
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + imagePath.getFileName() + "\"").append("\r\n");
            writer.append("Content-Type: image/jpeg").append("\r\n");  // Adjust for your image type, e.g., image/png
            writer.append("Content-Transfer-Encoding: binary").append("\r\n");
            writer.append("\r\n");
            writer.flush();

            // Stream image data in chunks
            byte[] buffer = new byte[1024];  // 8KB buffer
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();

            writer.append("\r\n").flush();
            writer.append("--" + boundary + "--").append("\r\n");
            writer.flush();
        }
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return 0;
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
		URL url = new URL("http://localhost:228/getChats");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
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
		URL url = new URL("http://localhost:228/getChatMembers");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
		URL url = new URL("http://localhost:228/getHistory");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
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
		
		os.write(msgt.getMessage());
		
		os.flush();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		int status = con.getResponseCode();
		
		con.disconnect();
		
		return status;
	}
	
	public User logIn(String username, String password, int portUsed) throws IOException, ProtocolException, MalformedURLException  {
		URL url = new URL("http://localhost:228/logIn");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
	
	public List<Chat> getAvailableChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException {
		URL url = new URL("http://localhost:228/getAvailableChats");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
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
	
	public boolean joinChat(UserInterface u, long chatID) throws IOException, ProtocolException, MalformedURLException  {
		URL url = new URL("http://localhost:228/joinChat");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
		
		con.disconnect();
		
		return status == 200;
	}
	
}
