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
	
	public void reciveChatTextUppdate(long senderID, long chatID, String textContent, LocalDateTime sendTime) {
		try {
			
			TextMessage msg = new TextMessage(model.getChatMemberByID(chatID, senderID), textContent);
			
			msg.setArrivleTime(sendTime);
			
			model.loadMessageToChat(msg, chatID);
			
			if (model.getCurrentChatID() == chatID)
				view.renderText(msg);
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void addChatCreateActionListener(JButton createChat) {
		
		createChat.addActionListener(e -> {
			
			String chatName = view.popChatNameText();
			
			if (chatName == null || chatName.isEmpty())
				return;
			
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
			
			Chat nc = model.createChat(chatName, chatID);
			
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
				
				String msgContent = view.getInputText();
				
				mp.postMessage(model.getUser(), msgContent, model.getCurrentChatID());
				
				view.renderText(model.sendMessage(msgContent));
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		});
	}
	
	public void addSendImgEvent(JButton send, JFrame base) {
		send.addActionListener(e -> {
			try {
				
				ImgObject imgObj = messageFileEnterpreter.choseImgFile();
				
				mp.postImg(model.getUser(), imgObj, model.getCurrentChatID());
				
				view.renderImg(model.sendImage(imgObj));
				
			} catch (Exception err){
				err.printStackTrace();
			}
		});
	}
	
	public void addSelectJoinChatEvent(JButton join, JoinChatView jcv) {
		join.addActionListener(e -> {
			try {
				
				List<Chat> l = mp.getAvailableChats(model.getUser());
				
				System.out.println(l);
				
				jcv.clean();
				
				for (Chat c : l) {
					JButton joinChatBtn = jcv.addChatToList(c);
					
					addJoinChatEvent(joinChatBtn, c);
					
					joinChatBtn.addActionListener(event -> {
						
						System.out.println("delete the row");
						
						jcv.deleteChatRowByID(c.getID());
					
					});
				}
				jcv.show();
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		});
	}
	
	public void addJoinChatEvent(JButton join, Chat c) {
		join.addActionListener(e -> {
			try {
				if (mp.joinChat(model.getUser(), c.getID())) {
					addChatActionListener(view.addChat(c.getName()), c);
					
					chatSwapEvent(c);
				}
				
			}
			catch (Exception err) {
				err.printStackTrace();
			}
		});
	}
	
	public void logInUser(User u) {
		List<Chat> userChats = null;
		
		try {
			userChats = mp.getAllUserChats(model.getUser());
		}
		catch (Exception err) {
			err.printStackTrace();
			userChats = new ArrayList<Chat>();
		}
		
		int successfulRenders = 0;
		
		for (Chat c : userChats) {
			try {
				model.loadChat(c, mp.getChatMembers(c.getID()));
				
			}
			catch (Exception err) {
				err.printStackTrace();
				continue;
			}
			
			addChatActionListener(view.addChat(c.getName()), c);
			
			successfulRenders++;
		}
		
		if (successfulRenders > 0)
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