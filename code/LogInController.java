import javax.swing.*;
import java.awt.*;
import java.util.*;

class LogInController {
	
	public JTextArea logInInput;
	public JTextArea passwordInput;
	public JButton logIn;
	
	public JButton logOut;
	
	public Messanger msngr;
	
	
	LogInController(Messanger msngr) {
		
		this.msngr = msngr;
		
		logInInput = new JTextArea();
		
		passwordInput = new JTextArea();
		
		logIn = new JButton();
		
		logIn.addActionListener(e -> {
			if (logInInput.getText() != "") {
				msngr.loggedIn(new User(logInInput.getText()));
			}
			
		});
		
		logOut = new JButton("Log out");
		
		logOut.addActionListener(e -> {
			msngr.loggingScreen();
		});
		
	}
}