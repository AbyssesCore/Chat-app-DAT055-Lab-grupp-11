import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.net.InetSocketAddress;
import java.util.*;

import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.io.*;

import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class Server {
	
	public static final int PORT = 228;
	
	private final String ChatHistoryBasePath = "serverFiles\\Chats\\Chat";
	
	private HttpServer server;
	 
	private ServerModel smodel;
	
	public static void main (String[] args) throws Exception {
		
		Server s = new Server();
		s.server.start();
		System.out.println("server is running on port "+PORT);
	}
	
	Server() throws Exception {
		
		
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
			System.out.println("what");
			
			try {
				InputStream is = t.getRequestBody();
				
				ObjectInputStream ois = new ObjectInputStream(is);
				
				Object obj = ois.readObject();
				
				/*System.out.println(obj.getClass());*/
				
				t.sendResponseHeaders(201, 0);
				OutputStream os = t.getResponseBody();
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
				
				User u = smodel.logIn(messageTranslater.translateString(is), messageTranslater.translateString(is));
				
				if (u == null) {

					System.out.println(u);

					String error = "No user with this log in and/or password";

					t.sendResponseHeaders(400, error.length());

					OutputStream os = t.getResponseBody();
					os.write(error.getBytes());
					os.close();
					return;
				}
				
				messageTranslater msgt = new messageTranslater();
				
				System.out.println( msgt.addString(u.getName()));
				
				System.out.println(msgt.addLong(u.getID()));
				
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
				smodel.logOut( Long.parseLong( decodeMessage(t.getRequestBody()).get(0) ) );
				
				t.sendResponseHeaders(200, 0);
					OutputStream os = t.getResponseBody();
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
		  });
		
		server.createContext("/getChats", (HttpExchange t) -> {
				try {
				
				InputStream is = t.getRequestBody();
				
				User u = smodel.getOnlineUserByID(messageTranslater.translateLong(is));
				
				List<Chat> userChats = smodel.getAllUsersChats(u);
				
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
				throw new RuntimeException(e);
			}
		});
		
		
	}
	
	public static Map<String, String> queryToMap(HttpExchange t){
		 String query = t.getRequestURI().getRawQuery();
		 Map<String, String> result = new HashMap<>();
		 if(query==null)
			return result;
		 for (String param : query.split("&")) {
			  String[] entry = param.split("=", 2);
			  if (entry.length > 1) {
					try {
						result.put(URLDecoder.decode(entry[0], "UTF-8"), 
									  URLDecoder.decode(entry[1], "UTF-8"));
					} catch (Exception e) {
					}
			  }else{
					result.put(entry[0], "");
			  }
		 }
		 return result;
	 }
	
	public List<String> decodeMessage(InputStream is) throws IOException{
		List<String> out = new ArrayList<String>();
		int b;
		
		while ((b = (int)is.read()) != -1) {
			
			byte[] message = new byte[b];
			
			is.read(message, 0, b);
			
			out.add(new String(message));
		}
		
		return out;
	}
	
}