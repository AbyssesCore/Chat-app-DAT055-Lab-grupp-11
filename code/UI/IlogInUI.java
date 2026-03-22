import javax.swing.*;


interface IlogInUI {
	public JButton getLogInButton();
	public JButton getCreateUserBtn();
	public String getLogInText();
	public String getPasswordText();
	public void buildLoginUI();
	public void clearScreen();
}