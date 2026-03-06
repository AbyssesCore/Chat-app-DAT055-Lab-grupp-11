import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

interface ChatUI {
	public JPanel getChatList();
	public JScrollPane getMessagesScroll();
	public JPanel getMessages();
	public JPanel getSidebar();
	public JButton getSendBtn();
	public JButton getSendImgBtn();
	public JButton getJoinChatBtn();
	public JButton getAddChatBtn();
	public JButton getLogOutBtn();
	public String getInputText();
	public void clearInputText();
	public String getChatNameText();
	public void clearChatNameText();
	
	// -------------------------
    // Styling helpers
    // -------------------------
	
	public void clearScreen();
	
	// -------------------------
    // Bygg Chat-utseendet
    // -------------------------
    
	public void buildChatUI(UserInterface u);
	
	public JButton addChat(String chatName);
	
	
	public void removeAllMessages();
	
	public void removeAllChats();
	
	public void revalidatedMessages();
	
	public void repaint();
}