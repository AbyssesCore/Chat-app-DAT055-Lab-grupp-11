import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;


class LoggedInUI implements ChatUI {
	
	private JButton addChat;
	private JTextArea input;
	private JButton send;
	private JButton logoutBtn;
	private JButton joinChat;
	private TextFieldWithPrompt chatName;
	private JButton sendImg;
	
	private JPanel chatList;
	private final JScrollPane messagesScroll;
	private final JPanel messages;
	
	private JPanel sidebar;
	
	private JFrame base;
	
	LoggedInUI (JFrame base) {
		this.base = base;
		
		input = new JTextArea();
		send = new JButton();
		addChat = new JButton();
		logoutBtn = new JButton();
		joinChat = new JButton();
		
		sendImg = new JButton();
		
		messages = new JPanel();
        messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
        messages.setBorder(new EmptyBorder(12, 12, 12, 12));
        messages.setBackground(new Color(245, 246, 248));
		
		chatList = new JPanel();
        chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
        chatList.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatList.setBackground(new Color(250, 250, 252));
		
		messagesScroll = new JScrollPane(messages);
        messagesScroll.setBorder(new EmptyBorder(0,0,0,0));
        messagesScroll.getViewport().setBackground(new Color(245, 246, 248));
        messagesScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        messagesScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(new Color(250, 250, 252));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(220, 223, 228)));
	}
	
	// -------------------------
    // Geters
    // -------------------------
	
	public JPanel getChatList() {
		return chatList;
	}
	
	public JScrollPane getMessagesScroll() {
		return messagesScroll;
	}
	
	public JPanel getMessages() {
		return messages;
	}
	
	public JPanel getSidebar() {
		return sidebar;
	}
	
	public JButton getSendBtn() {
		return send;
	}
	
	public JButton getSendImgBtn() {
		return sendImg;
	}
	
	public JButton getJoinChatBtn() {
		return joinChat;
	}
	
	public JButton getAddChatBtn() {
		return addChat;
	}
	
	public JButton getLogOutBtn() {
		return logoutBtn;
	}
	
	public String getInputText() {
		return input.getText();
	}
	
	public void clearInputText() {
		input.replaceRange("", 0, input.getText().length());
	}
	
	public String getChatNameText() {
		return chatName.getText();
	}
	
	public void clearChatNameText() {
		chatName.replaceRange("", 0, chatName.getText().length());
	}
	
	// -------------------------
    // Styling helpers
    // -------------------------
	
	public void clearScreen() {
        base.getContentPane().removeAll();
        base.repaint();
    }
	
	// -------------------------
    // Bygg Chat-utseendet
    // -------------------------
    
	public void buildChatUI(UserInterface u) {
		clearScreen();
		
		JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 246, 248));

        // ===== Sidebar =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(new Color(250, 250, 252));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(220, 223, 228)));

        // Sidebar header
        JPanel sideHeader = new JPanel();
        sideHeader.setLayout(new BorderLayout());
        sideHeader.setBorder(new EmptyBorder(14, 14, 10, 14));
        sideHeader.setBackground(new Color(250, 250, 252));

        JLabel appTitle = new JLabel("Chats");
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD, 18f));

		JPanel chatButtonList = new JPanel();
		chatButtonList.setLayout(new GridLayout(1, 2, 10, 0));
		chatButtonList.setBorder(new EmptyBorder(7, 5, 7, 5));
        chatButtonList.setBackground(new Color(250, 250, 252));
		
		joinChat.setText("Join chat");
		
		chatButtonList.add(joinChat);
		
		styleHelper.styleGhostButton(joinChat);
		
        addChat.setText("＋ New");
		chatButtonList.add(addChat);
		
        styleHelper.stylePrimaryButton(addChat);

        sideHeader.add(appTitle, BorderLayout.WEST);
        
		sideHeader.add(chatButtonList, BorderLayout.EAST);
		
        // Optional chatName field (utseende-only)
        chatName = new TextFieldWithPrompt("New chat name");
        chatName.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        chatName.setBackground(Color.WHITE);

        JPanel sideTop = new JPanel();
        sideTop.setLayout(new BoxLayout(sideTop, BoxLayout.Y_AXIS));
        sideTop.setBackground(new Color(250, 250, 252));
        sideTop.add(sideHeader);
        sideTop.add(Box.createVerticalStrut(6));

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(new Color(250, 250, 252));
        searchWrap.setBorder(new EmptyBorder(0, 14, 12, 14));
        searchWrap.add(chatName, BorderLayout.CENTER);
        sideTop.add(searchWrap);

        sidebar.add(sideTop, BorderLayout.NORTH);

        // Chat list scroll
		
		JScrollPane chatScroll = new JScrollPane(chatList);
        chatScroll.setBorder(new EmptyBorder(0,0,0,0));
        chatScroll.getViewport().setBackground(new Color(250, 250, 252));
        chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        sidebar.add(chatScroll, BorderLayout.CENTER);

        // Sidebar bottom user info (utseende)
        JPanel sideBottom = new JPanel(new BorderLayout());
        sideBottom.setBackground(new Color(250, 250, 252));
        sideBottom.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel userLabel = new JLabel("Signed in: " + u.getName());
        userLabel.setForeground(new Color(90, 95, 105));

        JButton dummySettings = new JButton("⚙");
        dummySettings.setEnabled(false);
        dummySettings.setBorderPainted(false);
        dummySettings.setContentAreaFilled(false);
        dummySettings.setFocusPainted(false);

        sideBottom.add(userLabel, BorderLayout.WEST);
        sideBottom.add(dummySettings, BorderLayout.EAST);

        sidebar.add(sideBottom, BorderLayout.SOUTH);

        // ===== Main area =====
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 246, 248));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 223, 228)),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel chatTitle = new JLabel("Select a chat");
        chatTitle.setFont(chatTitle.getFont().deriveFont(Font.BOLD, 16f));

        // Log out knapp sitter i lic (om null i början, skapa dummy)
        
		logoutBtn.setText("Log out");
		
        styleHelper.styleGhostButton(logoutBtn);

        topBar.add(chatTitle, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // Center messages
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBackground(new Color(245, 246, 248));
        centerWrap.add(messagesScroll, BorderLayout.CENTER);

        // Bottom input bar
        JPanel inputBar = new JPanel(new BorderLayout(10, 0));
        inputBar.setBackground(Color.WHITE);
        inputBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(220, 223, 228)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(new EmptyBorder(10, 10, 10, 10));
        input.setBackground(new Color(250, 250, 252));

        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setBorder(new LineBorder(new Color(220, 223, 228), 1, true));
        inputScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setPreferredSize(new Dimension(0, 60));

        styleHelper.stylePrimaryButton(send);
		
		send.setText("Send");
        inputBar.add(inputScroll, BorderLayout.CENTER);
        inputBar.add(send, BorderLayout.EAST);
		
		styleHelper.stylePrimaryButton(sendImg);
		
		sendImg.setText("Send img");
        inputBar.add(sendImg, BorderLayout.WEST);
		
		styleHelper.stylePrimaryButton(sendImg);
		
        main.add(topBar, BorderLayout.NORTH);
        main.add(centerWrap, BorderLayout.CENTER);
        main.add(inputBar, BorderLayout.SOUTH);

        // Layout: split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, main);
        split.setDividerSize(6);
        split.setDividerLocation(270);
        split.setBorder(new EmptyBorder(0,0,0,0));
        split.setContinuousLayout(true);

        root.add(split, BorderLayout.CENTER);

        // När chatList ändras (Create chat etc)
        //addRepaintOnaddChat(chatList);
        //addRepaintOnaddChat(sidebar);
		base.setContentPane((Container) root);
		
		base.repaint();
		base.setVisible(true);
		base.revalidate();
    }
	
	public JButton addChat(String chatName) {
		JButton chatButton = new JButton(chatName);
        // Style chat buttons as list items
        chatButton.setHorizontalAlignment(SwingConstants.LEFT);
        chatButton.setFocusPainted(false);
        chatButton.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        chatButton.setBackground(Color.WHITE);
        chatButton.setOpaque(true);
        chatButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        chatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		
        chatList.add(chatButton);
        chatList.add(Box.createVerticalStrut(8));
        chatList.revalidate();
        chatList.repaint();
		
		return chatButton;
	}
	
	
	public void removeAllMessages() {
		messages.removeAll();
	}
	
	public void removeAllChats() {
		chatList.removeAll();
	}
	
	public void revalidatedMessages() {
		messages.revalidate();
        messages.repaint();
	}
	
	public void repaint() {
		base.revalidate();
		base.repaint();
	}
}