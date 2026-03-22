import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class LogInView extends JFrame {
	private JTextArea logInInput;
	private JTextArea passwordInput;
	private JButton logInButton;
	
	IlogInUI ui;
	
	LogInView(IlogInUI ui) {
		logInInput = new JTextArea();
		passwordInput = new JTextArea();
		logInButton = new JButton();
		
		this.ui = ui;
	}
	
	public void buildLoginUI() {
		ui.buildLoginUI();
	}
	
	public String getLogInText() {
		return ui.getLogInText();
	}
	
	public String getPasswordText() {
		return ui.getPasswordText();
	}
	
	public JButton getCreateUserBtn() {
		return ui.getCreateUserBtn();
	}
}