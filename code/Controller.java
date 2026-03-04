import javax.swing.*;
import java.util.*;

import java.time.LocalDateTime;

class Controller implements NotificationListener {
	Model model;
	View view;
	
	List<LogOutObserver> logOutObservers = new ArrayList<LogOutObserver>();
	
	public JButton logOutButton;
	private MessagePublisher mp;
	
	
	
	Controller(Model model, View view) {
		this.view = view;
		this.model = model;
		this.mp = new MessagePublisher();
	}
	
	public void addLogOutEvent(JButton logOutButton) {
		logOutButton.addActionListener(e -> {
			logOut(true);
		});
	}
	
	public void logOut(boolean keepAlive) {
		try {
			mp.sendLogOut(model.getUser());
			
			if (keepAlive) {
				notifyLogOutObservers();
			}
			
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void reciveChatUppdate(byte[] NewMessageContent) {
		
	}
	
	public void addChatCreateEvent(JButton createChat) {
		
		createChat.addActionListener(e -> {
			
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
	
	public void addSendEvent(JButton send) {
		
		send.addActionListener(e -> {
			try {
				mp.postMessage(model.getUser(), view.getInputText(), model.getCurrentChatID());
				
				view.addText(model.sendMessage(view.getInputText()));
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		});
	}
	
	public void logInUser(User u) {
		List<Chat> userChats = null;
		
		try {
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
		
		view.buildChatUI(u);
		
	}
	
	private void notifyLogOutObservers() {
		
		System.out.println("quit");
		
		UserInterface u = model.getUser();
		
		for (LogOutObserver observer : logOutObservers)
			observer.invokeOnLogOut(u);
	}
	
	public void addLogOutObserver(LogOutObserver observer) {
		logOutObservers.add(observer);
	}
}

