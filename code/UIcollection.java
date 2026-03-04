import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class UIcollection implements logInUI {
	
	private JTextArea logInInput;
	private JTextArea passwordInput;
	private JButton logInButton;
	
	private JFrame base;
	
	UIcollection(JFrame base) {
		this.base = base;
		
		styleHelper.setNiceLookAndFeel();
		
		logInInput = new JTextArea();
		passwordInput = new JTextArea();
		logInButton = new JButton();
		
	}
	
	// -------------------------
    // Styling helpers
    // -------------------------
	
	public void clearScreen() {
        base.getContentPane().removeAll();
        base.repaint();
    }
	
	
	// -------------------------
    // Log in UI Impelemntation
    // -------------------------
	
	public void buildLoginUI() {
		clearScreen();
		
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 246, 248));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));
        card.setPreferredSize(new Dimension(420, 320));

        JLabel title = new JLabel("Welcome back");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Sign in to continue to the chat");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setForeground(new Color(90, 95, 105));

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));

        // Username
        JLabel userLbl = new JLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));

        logInInput.setText("");
        logInInput.setRows(1);
        logInInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        logInInput.setBackground(new Color(250, 250, 252));
        logInInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        logInInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(logInInput);

        card.add(Box.createVerticalStrut(12));

        // Password (utseende-only: controller har JTextArea just nu)
        JLabel passLbl = new JLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));

        passwordInput.setText("");
        passwordInput.setRows(1);
        passwordInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        passwordInput.setBackground(new Color(250, 250, 252));
        passwordInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(passwordInput);

        card.add(Box.createVerticalStrut(18));

        // Buttons row
        JPanel btnRow = new JPanel(new BorderLayout(10, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        logInButton.setText("Sign in");
        styleHelper.stylePrimaryButtonStatic(logInButton);

        JButton guest = new JButton("Continue as guest");
        guest.setEnabled(false);
        styleHelper.styleGhostButtonStatic(guest);

        btnRow.add(guest, BorderLayout.CENTER);
        btnRow.add(logInButton, BorderLayout.EAST);

        card.add(btnRow);

        card.add(Box.createVerticalStrut(14));

        JLabel hint = new JLabel("Buttons don’t need to work – this is UI only.");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setForeground(new Color(120, 125, 135));
        hint.setFont(hint.getFont().deriveFont(12f));
        card.add(hint);

        root.add(card);
		
		base.add(root);
		
		base.setContentPane((Container) root);
        base.revalidate();
        base.setVisible(true);
    }
	
	// -------------------------
    // Geters
    // -------------------------
	
	public JButton getLogInButton() {
		return logInButton;
	}
	
	public String getLogInText() {
		return logInInput.getText();
	}
	
	public String getPasswordText() {
		return passwordInput.getText();
	}
	
}