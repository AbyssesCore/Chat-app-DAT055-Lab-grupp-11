import javax.swing.JButton;

class createUserForm {
	CreateUserUI ui;
	
	createUserForm (CreateUserUI ui) {
		this.ui = ui;
	}
	
	public void showUI() {
		ui.show();
	}
	
	public void hideUI() {
		ui.hide();
	}
	
	public String getUsernameText() {
		return ui.getUsernameText();
	}
	
	public String getPasswordText() {
		return ui.getPasswordText();
	}
	
	public String getDisplayNameText() {
		return ui.getDisplayNameText();
	}
	
	public JButton getCreateUserBtn() {
		return ui.getCreateUserBtn();
	}
}