import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashSet;

import java.awt.Dimension;

import java.awt.image.BufferedImage;

import java.io.File;

class View {
    private Model model;
	
	private final IChatUI ui;

    // Chat list area (era chat-knappar läggs här)
    
    private final HashSet<JComponent> repaintOnChatChange;

    View(Model model, IChatUI ui) {
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
		
		String username = msg.getUser().getName();
		
        int hour = msg.getCreateTime().getHour();
		int minute = msg.getCreateTime().getMinute();
		
		String content = msg.getText();
		
		ui.renderNewText(username, content, hour, minute, isMe);
	}
	
	public void renderImg(ImgMessage msg)  {
		
		boolean isMe = (msg.getUser().equals(model.getUser()));
		
		BufferedImage imgContent;
		
		try {
			ImgObject img = msg.getImg();
			
			imgContent = model.getImgByName(img.getImgNamePart());
		}
		catch (Exception err) {
			err.printStackTrace();
			
			try {
				imgContent = model.getImgByName("noImgFound.jpg");
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		String username = msg.getUser().getName();
		
        int hour = msg.getCreateTime().getHour();
		int minute = msg.getCreateTime().getMinute();
		
		ui.renderImg(username, imgContent, hour, minute, isMe);
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
