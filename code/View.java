import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashSet;

import java.awt.Dimension;

class View {
    private Model model;
	
	private final ChatUI ui;

    // Chat list area (era chat-knappar läggs här)
    
    private final HashSet<JComponent> repaintOnChatChange;

    View(Model model, ChatUI ui) {
        this.ui = ui;
		this.model = model;
		repaintOnChatChange = new HashSet<>();
		
		addRepaintOnaddChat(ui.getChatList());
        addRepaintOnaddChat(ui.getSidebar());
    }
	
    // -------------------------
    // Login-utseende (statisk)
    // -------------------------
	
	public void renderText (TextMessage msg) {
		
		boolean isMe = (msg.getUser().equals(model.getUser()));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel bubble = new BubblePanel(isMe ? new Color(220, 238, 255) : Color.WHITE);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 12, 10, 12));
        bubble.setOpaque(false);

        JLabel name = new JLabel(msg.getUser().getName());
        name.setFont(name.getFont().deriveFont(Font.BOLD, 12f));
        name.setForeground(new Color(70, 75, 85));
		
		JComponent content = new JLabel(msg.getText());
		content.setOpaque(false);
		
        bubble.add(name);
        bubble.add(Box.createVerticalStrut(4));
        bubble.add(content);

        if (isMe) {
            row.add(bubble, BorderLayout.EAST);
        } else {
            row.add(bubble, BorderLayout.WEST);
        }
		JPanel messages = ui.getMessages();
		
        messages.add(row);
        messages.add(Box.createVerticalStrut(2));

        messages.revalidate();

        // Auto-scroll till botten
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ui.getMessagesScroll().getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
	}
	
	public void renderImg(ImgMessage msg)  {
		
		boolean isMe = (msg.getUser().equals(model.getUser()));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel bubble = new BubblePanel(isMe ? new Color(220, 238, 255) : Color.WHITE);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 12, 10, 12));
        bubble.setOpaque(false);

        JLabel name = new JLabel(msg.getUser().getName());
        name.setFont(name.getFont().deriveFont(Font.BOLD, 12f));
        name.setForeground(new Color(70, 75, 85));
		
		
		ImgObject imgObj = msg.getImg();
		
		Dimension d = imgObj.getImgDimension();
		
		int widthLim = 300 > d.getWidth() ? (int)d.getWidth() : 300;
		
		double scale = widthLim / d.getWidth();
		
		int heightLim = (int)(d.getHeight() * scale);
		
		JComponent content = new JLabel( new ImageIcon(new ImageIcon(msg.getBufferedImage()).getImage().getScaledInstance(widthLim, heightLim, java.awt.Image.SCALE_SMOOTH)) );
		content.setOpaque(false);
		
        bubble.add(name);
        bubble.add(Box.createVerticalStrut(4));
        bubble.add(content);

        if (isMe) {
            row.add(bubble, BorderLayout.EAST);
        } else {
            row.add(bubble, BorderLayout.WEST);
        }

		JPanel messages = ui.getMessages();
		
        messages.add(row);
        messages.add(Box.createVerticalStrut(2));

        messages.revalidate();

        // Auto-scroll till botten
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ui.getMessagesScroll().getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
	}

    // -------------------------
    // Message rendering
    // -------------------------
    public void renderMessage(Message msg) {
		if (model == null)
			return;
		
		msg.render(this);
    }

    public JButton addChat(String chatName) {
		
		JButton chatButton = ui.addChat(chatName);
		
        for (JComponent repaintTarget : repaintOnChatChange) {
            repaintTarget.revalidate();
            repaintTarget.repaint();
        }
		
		return chatButton;
    }

    public void addRepaintOnaddChat(JComponent repaintTarget) {
		if (repaintTarget == null)
			return;
        repaintOnChatChange.add(repaintTarget);
    }

    public void removeRepaintOnaddChat(JComponent repaintTarget) {
        repaintOnChatChange.remove(repaintTarget);
    }

    public void loadChat() throws Exception {
		
        ui.removeAllMessages();
		
        for (Message msg : model.getCurrentChatHistory()) {
            renderMessage(msg);
        }

        ui.revalidatedMessages();
    }
	
    // -------------------------
    // Rounded bubble panel
    // -------------------------
    private static class BubblePanel extends JPanel {
        private final Color fill;

        BubblePanel(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 18;
            int w = getWidth();
            int h = getHeight();

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            g2.setColor(new Color(220, 223, 228));
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
	
	public void buildChatUI(User u) {
		ui.buildChatUI(u);
	}
	
	public String getInputText() {
		String out = ui.getInputText();
		
		ui.clearInputText();
		
		return out;
	}
	
	public String popChatNameText() {
		String out = ui.getChatNameText();
		
		ui.clearChatNameText();
		
		return out;
	}
	
	
}
