import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.net.InetSocketAddress;
import java.util.*;

import java.time.LocalDateTime;

import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.io.*;

import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class ServerDrive {
	
	public static final int PORT = 228;
	
	private HttpServer server;
	 
	private ServerModel smodel;
	
	public static void main (String[] args) throws Exception {
		
		ServerDrive s = new ServerDrive();
		s.server.start();
		System.out.println("server is running on port "+PORT);
	}
	
	ServerDrive() throws Exception {
		
		
		smodel = new ServerModel();
		
		this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
		
			server.createContext("/createChat", (HttpExchange t) -> {
				try {
					
					InputStream is = t.getRequestBody();
					
					System.out.println("new chat created: ");
					
					Chat nc = smodel.createChat(messageTranslater.translateLong(is), messageTranslater.translateString(is));
					
					if (nc == null) {
						
						String error = "Couldn't create chat";
						
						t.sendResponseHeaders(418, error.length());
						
						OutputStream os = t.getResponseBody();
						os.write(error.getBytes());
						os.close();
						return;
					}
					
					messageTranslater msgt = new messageTranslater();
					
					msgt.addLong(nc.getID());
					
					
					t.sendResponseHeaders(201, msgt.getMessageLength());
						OutputStream os = t.getResponseBody();
					
					os.write(msgt.getMessage().getBytes());
					
						os.close();
				} catch (Exception e) {
					e.printStackTrace();
				
				t.sendResponseHeaders(500, 0);
				
				throw new RuntimeException(e);
				}
			});
			
			server.createContext("/sendMsg", (HttpExchange t) -> {
				try {
					InputStream is = t.getRequestBody();
					
					long userID = messageTranslater.translateLong(is);
					
					long chatID = messageTranslater.translateLong(is);
					
					String msgContent = messageTranslater.translateString(is);
					
					smodel.sendTextMessage(chatID, userID, msgContent);
					
					t.sendResponseHeaders(201, 0);
					OutputStream os = t.getResponseBody();
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
					
					t.sendResponseHeaders(500, 0);
					
					throw new RuntimeException(e);
				}
			});
			
			server.createContext("/checkLogIn", (HttpExchange t) -> {
				try {
					InputStream is = t.getRequestBody();
					
					LocalDateTime sendTime = messageTranslater.translateLocalDateTime(is);
					
					String userName = messageTranslater.translateString(is);
					
					String userPassword = messageTranslater.translateString(is);
					
					UserInterface u = smodel.arrangeLogIn(userName, userPassword, sendTime);
					
					if (u == null) {
						String error = "No user with this log in and/or password";

						t.sendResponseHeaders(400, error.length());

						OutputStream os = t.getResponseBody();
						os.write(error.getBytes());
						os.close();
						return;
					}
					
					messageTranslater msgt = new messageTranslater();
					
					String responseMessage = msgt.getMessage();
					
					t.sendResponseHeaders(200, responseMessage.length());
					DataOutputStream os = new DataOutputStream(t.getResponseBody());
					os.writeBytes(responseMessage);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
					
					t.sendResponseHeaders(500, 0);
					
					throw new RuntimeException(e);
				}

			});
			
			server.createContext("/logIn", (HttpExchange t) -> {
				try {
					
					InputStream is = t.getRequestBody();
					
					UserInterface u = smodel.logIn(messageTranslater.translateString(is), messageTranslater.translateString(is), t.getRemoteAddress().getAddress(), t.getRemoteAddress().getPort());
					
					if (u == null) {
						
						String error = "No user with this log in and/or password";

						t.sendResponseHeaders(400, error.length());

						OutputStream os = t.getResponseBody();
						os.write(error.getBytes());
						os.close();
						return;
					}
					
					messageTranslater msgt = new messageTranslater();
					
					msgt.addString(u.getName());
					
					msgt.addLong(u.getID());
					
					String responseMessage = msgt.getMessage();
					
					t.sendResponseHeaders(200, responseMessage.length());
					DataOutputStream os = new DataOutputStream(t.getResponseBody());
					os.writeBytes(responseMessage);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				
				t.sendResponseHeaders(500, 0);
				
				OutputStream os = t.getResponseBody();
				os.close();
				
				throw new RuntimeException(e);
			}
		});
		
		
		server.createContext("/logOut", (HttpExchange t) -> {
			try {
				smodel.logOut( messageTranslater.translateLong(t.getRequestBody()) );
				
				t.sendResponseHeaders(200, 0);
				OutputStream os = t.getResponseBody();
				os.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
		  });
		
		server.createContext("/getHistory", (HttpExchange t) -> {
			try {
				
				InputStream is = t.getRequestBody();
				
				long chatID = messageTranslater.translateLong(is);
				
				LocalDateTime lastMessage = messageTranslater.translateLocalDateTime(is);
				
				List<byte[]> chats = smodel.getChatHistory(chatID, lastMessage);
				
				if (chats == null) {
					String errorReport = "Couldn't load users chats history";
					
					t.sendResponseHeaders(500, errorReport.length());
					
					OutputStream os = t.getResponseBody();
					
					os.write(errorReport.getBytes());
					
					os.close();
					return;
				}
				
				
				messageTranslater msgt = new messageTranslater();
				
				String arrayLength = msgt.addLong(chats.size(), false);
				
				int endSize = arrayLength.length();
				
				for (byte[] msgBytes : chats) {
					endSize += msgBytes.length;
				}
				
				t.sendResponseHeaders(200, endSize);
				
				OutputStream os = t.getResponseBody();
				
				os.write(arrayLength.getBytes());
				
				for (byte[] msgBytes : chats) {
					os.write(msgBytes);
				}
				
				os.close();
				
				} catch (Exception e) {
					e.printStackTrace();
					
					t.sendResponseHeaders(500, 0);
					
					OutputStream os = t.getResponseBody();
					os.close();
					
					throw new RuntimeException(e);
				}
		  });
		
		server.createContext("/getChats", (HttpExchange t) -> {
			try {
				
				InputStream is = t.getRequestBody();
				
				UserInterface u = smodel.getOnlineUserByID(messageTranslater.translateLong(is));
				
				List<Chat> userChats = smodel.getAllUsersChats(u);
				
				if (userChats == null) {
					String errorReport = "Couldn't load users chats";
					
					t.sendResponseHeaders(500, errorReport.length());
					
					OutputStream os = t.getResponseBody();
					
					os.write(errorReport.getBytes());
					
					os.close();
					return;
				}
				
				messageTranslater msgt = new messageTranslater();
				
				msgt.addLong(userChats.size());
				
				for (Chat c : userChats) {
					msgt.addLong(c.getID());
					
					msgt.addString(c.getName());
				}
				
				t.sendResponseHeaders(200, msgt.getMessageLength());
				OutputStream os = t.getResponseBody();
				
				os.write(msgt.getMessage().getBytes());
				
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				t.sendResponseHeaders(500, 0);
				
				throw new RuntimeException(e);
			}
		});
	}
	
}