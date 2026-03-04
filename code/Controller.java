import javax.swing.*;
import java.util.*;

import java.time.LocalDateTime;

class Controller implements NotificationListener{
	Model model;
	View view;
	
	public JTextArea input;
	public JButton send;
	
	public JButton addChat;
	
	public JButton logOutButton;
	
	private MessagePublisher mp;
	
	private NotificationReciver nr;
	
	Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		this.mp = new MessagePublisher();
		
		List<Chat> userChats = null;
		
		try {
			this.nr = new NotificationReciver(this);
			
			userChats = mp.getAllUsersChats(model.getUser());
		}
		catch (Exception err) {
			err.printStackTrace();
			userChats = new ArrayList<Chat>();
		}
		
		for (Chat c : userChats) {
			addChatActionListener(view.addChat(c.getName()), c);
		}
		
		if (userChats.size() > 0)
			chatSwapEvent(userChats.get(0));
		
		
		
		input = new JTextArea("Type Here!");
		send = new JButton("Send");
		
		logOutButton = new JButton("Log out");
		
		logOutButton.addActionListener(e -> {
			logOut();
		});
		
		addChat = new JButton("Create chat");
		
		send.addActionListener(e -> {
			try {
				mp.postMessage(model.getUser(), input.getText(), model.getCurrentChatID());
				
				view.insertMessage(model.sendMessage(input.getText()));
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		});
		
		addChat.addActionListener(e -> {
			
			String chatName = "Test chat";
			
			long chatID;
			
			try {
				chatID = mp.postNewChat(model.getUser(), chatName);
				
			}
			catch (Exception err) {
				err.printStackTrace();
				return;
			}
			
			
			if (chatID < 0) {
				
				System.out.println("Cant create a new chat: " + chatID);
				
				return;
			}
			
			Chat nc = model.addChat(chatName, chatID);
			
			addChatActionListener(view.addChat(chatName), nc);
			
			chatSwapEvent(nc);
		});
		
		
		
	}
	
	public void logOut() {
		try {
			mp.sendLogOut(model.getUser());
			
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void reciveChatUppdate(byte[] NewMessageContent) {
		
	}
	
	public void addChat(Chat nc) {
		
	}
	
	public void addChatActionListener(JButton chatButton, Chat nc) {
		chatButton.addActionListener(e -> { chatSwapEvent(nc); });
	}
	
	private void chatSwapEvent(Chat c) {
		int chatStatus = model.selectChat(c);
		
		if (chatStatus >= 0) {
			try {
				model.loadMessagesToChat(mp.getChatHistory(model.getCurrentChatID(), model.getCurrentChatLastMessageSendTime() ));
				
				view.loadChat();
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		}
	}
}

