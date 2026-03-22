import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import java.awt.event.*;

class Messanger implements LogInObserver, LogOutObserver {
    Model model;
    View view;
	
	LogInView liView;
	
	JFrame jf = new JFrame();
	
	IlogInUI ui;
	
	createUserForm userFormView;
	
	IChatUI chatUI;
	
	Controller controller;
	
	LogInController liController;
	
	MessagePublisher mp;
	
	NotificationReciver nr = new NotificationReciver();
	
    Messanger() {
        jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				
				if (controller != null) {
					controller.logOut(false);
				}
				
				System.exit(0);
			}
		});
		
		jf.setName("Messanger");
		
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.setMinimumSize(new Dimension(700, 650));
        jf.setLocationRelativeTo(null);
		
		ui = new UIcollection(jf);
		
		liView = new LogInView(ui);
		
		try {
			mp = new MessagePublisher();
		}
		catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		userFormView = new createUserForm(new CreateUserUI(jf));
		
		
		liController = new LogInController(liView, userFormView, mp);
		
		liController.addLogInObserver(this);
		
		liController.addLogInEvent(ui.getLogInButton());
		
		liController.addShowCreateUserForm(liView.getCreateUserBtn());
		
		liController.addCreateUserEvent(userFormView.getCreateUserBtn());
		
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Messanger::new);
    }
	
	public void invokeLogIn(String username, String password) {
		User u = null;
		
		try {
			nr.startReciving();
			
			u = mp.logIn(username, password, nr.getSocketAddres().getPort());
		}
		catch (Exception err){
			err.printStackTrace();
			return;
		}
		
		if (u == null) {
			nr.stopReciving();
			return;
		}
		
		chatUI = new LoggedInUI(jf);
		
		model = new Model(u, mp);
		view = new View(model, chatUI);
		
		controller = new Controller(model, view, mp);
		
		nr.addSubscriber(controller);
		
		controller.addLogOutObserver(this);
		
		controller.logInUser(u);
		
		controller.addSendEvent(chatUI.getSendBtn());
		
		controller.addSendImgEvent(chatUI.getSendImgBtn(), jf);
		
		controller.addLogOutEvent(chatUI.getLogOutBtn());
		
		controller.addChatCreateActionListener(chatUI.getAddChatBtn());
		
		controller.addSelectJoinChatEvent(chatUI.getJoinChatBtn(), new JoinChatView(jf));
		
		chatUI.repaint();
    }
	
	public void invokeOnLogOut(UserInterface u) {
		chatUI.removeAllChats();
		
		liView.buildLoginUI();
		
		liController.addShowCreateUserForm(liView.getCreateUserBtn());
		
		
		liController.addCreateUserEvent(userFormView.getCreateUserBtn());
		
		nr.stopReciving();
		
		nr.removeSubscriber(controller);
		
		chatUI = null;
		view = null;
		model = null;
		controller = null;
	}
}
