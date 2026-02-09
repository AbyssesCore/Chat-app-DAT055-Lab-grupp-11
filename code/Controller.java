import javax.swing.*;
import java.util.*;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class Controller {
	Model model;
	View view;
	
	public JTextArea input;
	public JButton send;
	
	public List<JButton> chats;
	
	public JButton addChat;
	
	private HttpServer clientSocket;
	
	Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		
		chats = new ArrayList<JButton>();
		
		input = new JTextArea("Type Here!");
		send = new JButton("Send");
		
		addChat = new JButton("Create chat");
		
		
		send.addActionListener(e -> {
			
			try {
				view.addText(model.sendMessage(input.getText()));
				URL url = new URL("http://localhost:228/sendMsg");
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				con.setDoOutput(true);
				
				con.setRequestProperty("Content-Type", "application/text");
				
				DataOutputStream os = new DataOutputStream(con.getOutputStream());
				
				String userName = model.getUser().getName();
				
				String msgContent = input.getText();
				
				String chatName = model.getCurrentChatName();
				
				os.writeBytes(((char)(userName.length() & 0xFF) + "" + (char)(chatName.length() & 0xFF) + "" + (char)(msgContent.length() & 0xFF)) + userName + chatName + msgContent);
				
				os.flush();
				
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				
				int status = con.getResponseCode();
				
				con.disconnect();
				
				
			}
			catch (Exception err) {
				System.out.println(err);
			}
			
		});
		
		addChat.addActionListener(e -> {
			
			String chatName = "Test chat";
			
			try {
				URL url = new URL("http://localhost:228/createChat");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				con.setDoOutput(true);
				
				con.setRequestProperty("Content-Type", "application/text");
				
				DataOutputStream os = new DataOutputStream(con.getOutputStream());
				
				String chatAuthor = model.getUser().getName();
				
				os.writeBytes(((char)(chatAuthor.length() & 0xFF) + "" + (char)(chatName.length() & 0xFF)) + chatAuthor + chatName);
				
				os.flush();
				
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				
				int status = con.getResponseCode();
				
				if (status != 201) {
					return;
				}
				
				con.disconnect();
			}
			catch (Exception err) {
				System.out.println(err);
			}
			
			
			Chat nc = model.addChat(chatName);
			
			JButton ncButton = new JButton(chatName);
			
			chats.add(ncButton);
			view.addChat(ncButton);
			
			if (model.selectChat(nc)) {
				try {
					view.loadChat();
				}
				catch (Exception err) {
					System.out.println(err);
				}
				
			}
			
			ncButton.addActionListener(ncE -> {
				if (model.selectChat(nc)) {
					try {
						view.loadChat();
						
						
						URL url = new URL("http://localhost:228/");
						HttpURLConnection con = (HttpURLConnection) url.openConnection();
						con.setRequestMethod("GET");
						
						con.setConnectTimeout(5000);
						con.setReadTimeout(5000);
						
						int status = con.getResponseCode();
						
						BufferedReader in = new BufferedReader(
						  new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer content = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							content.append(inputLine);
						}
						in.close();
						System.out.println(content);
						con.disconnect();
					}
					catch (Exception err) {
						System.out.println(err);
					}
					
				}
			});
		});
		
		
		try {
			clientSocket = HttpServer.create(new InetSocketAddress(0), 0);
			
			System.out.println("Port used: " + clientSocket.getAddress());
			
			clientSocket.createContext("/updateChat", (HttpExchange t) -> {
				InputStream is = t.getRequestBody();
				byte[] bytes = new byte[is.available()];
				
				is.read(bytes, 0, is.available());
			});
		}
		catch (Exception err) {
			System.out.println(err);
		}
		
	}
}