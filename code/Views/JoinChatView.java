import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import java.util.Hashtable;

class JoinChatView {
	
	private JFrame base;
	
	private JDialog popup;
	
	private JPanel contentPanel;
	
	private Hashtable<Long, JPanel> rows;
	
	JoinChatView(JFrame base) {
		
		this.base = base;
		
		rows = new Hashtable<Long, JPanel>();
		
		popup = new JDialog(base, "Scrollable Popup", true); // true for modal
		popup.setSize(400, 300);
		popup.setLocationRelativeTo(base); // Center it relative to main frame

		// Create the content panel with vertical layout for rows
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		// Add the content panel to a scroll pane (this provides the slider/scrollbar)
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar

		// Add the scroll pane to the dialog
		popup.add(scrollPane);

		
	}
	
	public JButton addChatToList(Chat chatToDisplay) {
		// Stylen för pop-up fönster har varit designat med hjälp av AI
		// Create the popup dialog
		// Add sample rows (you can replace this with your actual content)
		
		JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Left-aligned flow layout
		rowPanel.add(new JLabel(chatToDisplay.getName() + " (" + chatToDisplay.getID() + ")")); // Content label
		JButton rowButton = new JButton("Join"); // Button on the same row
		
		rowPanel.add(rowButton);
		contentPanel.add(rowPanel);
		
		rows.put(chatToDisplay.getID(), rowPanel);
		
		return rowButton;
	}
	
	public void deleteChatRowByID(long chatID) {
		JPanel row = rows.get(chatID);
		
		if (row == null)
			return;
		
		contentPanel.remove(row);
		
		rows.remove(chatID);
		
		contentPanel.revalidate();
		contentPanel.repaint();
	}
	
	public void show() {
		// Show the popup
		popup.setVisible(true);
	}
	
	public void clean() {
		contentPanel.removeAll();
		rows.clear();
	}
}