import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class CreateUserUI {
	private JTextArea userName;
	private JTextArea password;
	private JTextArea displayName;
	private JButton createUser;
	
	private JFrame base;
	
	private JDialog popup;
	
	CreateUserUI(JFrame base) {
		userName = new JTextArea();
		password = new JTextArea();
		displayName = new JTextArea();
		createUser = new JButton();
		
		this.base = base;
		
		popup = new JDialog(base, "Scrollable Popup", true); // true for modal
		popup.setSize(400, 300);
		popup.setLocationRelativeTo(base); // Center it relative to main frame

		// Create the content panel with vertical layout for rows
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
	
		JLabel userLbl = new JLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(userLbl);
        contentPanel.add(Box.createVerticalStrut(6));

        userName.setText("");
        userName.setRows(1);
        userName.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        userName.setBackground(new Color(250, 250, 252));
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);
        userName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        contentPanel.add(userName);

        contentPanel.add(Box.createVerticalStrut(12));
		
		// DisplayName
		JLabel dispLbl = new JLabel("Display name");
        dispLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(dispLbl);
        contentPanel.add(Box.createVerticalStrut(6));

        displayName.setText("");
        displayName.setRows(1);
        displayName.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        displayName.setBackground(new Color(250, 250, 252));
        displayName.setAlignmentX(Component.LEFT_ALIGNMENT);
        displayName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        contentPanel.add(displayName);

        // Password (utseende-only: controller har JTextArea just nu)
        JLabel passLbl = new JLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(passLbl);
        contentPanel.add(Box.createVerticalStrut(6));

        password.setText("");
        password.setRows(1);
        password.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        password.setBackground(new Color(250, 250, 252));
        password.setAlignmentX(Component.LEFT_ALIGNMENT);
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        contentPanel.add(password);
		
		
		createUser.setText("Sign in");
        styleHelper.stylePrimaryButtonStatic(createUser);
		
		contentPanel.add(createUser);
		
		popup.add(contentPanel);
	}
	
	public void show() {
		popup.setVisible(true);
	}
	
	public void hide() {
		popup.setVisible(false);
	}
	
	public String getUsernameText() {
		return userName.getText();
	}
	
	public String getPasswordText() {
		return password.getText();
	}
	
	public String getDisplayNameText() {
		return displayName.getText();
	}
	
	public JButton getCreateUserBtn() {
		return createUser;
	}
	
	
}