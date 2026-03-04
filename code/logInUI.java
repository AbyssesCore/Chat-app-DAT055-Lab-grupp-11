import javax.swing.*;


interface logInUI {
	public JButton getLogInButton();
	public String getLogInText();
	public String getPasswordText();
	public void buildLoginUI();
	public void clearScreen();
}