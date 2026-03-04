import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import java.awt.event.*;

class Messanger implements LogInObserver, LogOutObserver {
    Model model;
    View view;
	
	LogInView liView;
	
	JFrame jf = new JFrame();
	
	logInUI ui;
	
	ChatUI chatUI;
	
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
		
		mp = new MessagePublisher();
		
		liController =  new LogInController(liView, mp);
		
		liController.addLogInObserver(this);
		
		liController.addLogInEvent(ui.getLogInButton());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Messanger::new);
    }
	
	public void invokeOnLogIn(User u) {
		
		System.out.println("invoked log in");
		
		chatUI = new LoggedInUI(jf);
		
		model = new Model(u);
		view = new View(model, chatUI);
		
		controller = new Controller(model, view);
		
		try {
			nr.startReciving();
			
			nr.addSubscriber(controller);
		}
		catch (Exception err) {
			err.printStackTrace();
			invokeOnLogOut(u);
			return;
		}
		
		
		controller.addLogOutObserver(this);
		
		controller.logInUser(u);
		
		controller.addSendEvent(chatUI.getSendBtn());
		
		controller.addLogOutEvent(chatUI.getLogOutBtn());
		
		controller.addChatCreateEvent(chatUI.getAddChatBtn());
		
		chatUI.repaint();
    }
	
	public void invokeOnLogOut(UserInterface u) {
		chatUI.removeAllChats();
		
		liView.buildLoginUI();
		
		nr.stopReciving();
		
		nr.removeSubscriber(controller);
		
		chatUI = null;
		view = null;
		model = null;
		controller = null;
	}
}
