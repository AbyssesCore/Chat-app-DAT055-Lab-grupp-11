import javax.swing.*;
import java.awt.*;
import java.util.*;

class View {
	Model model;
	
	JPanel text;
	JPanel chatList;
	
	HashSet<JComponent> repaintOnChatChange;
	
	
	View(Model model) {
		this.model = model;
		
		text = new JPanel(new GridLayout(0, 1));
		
		chatList = new JPanel();
		
		chatList.setLayout(new BoxLayout(chatList, BoxLayout.Y_AXIS));
		
		repaintOnChatChange = new HashSet<JComponent>();
	}
	
	public JComponent getTextComponent() {
		return text;
	}
	
	public void addRepaintOnaddChat(JComponent repaintTarget) {
		repaintOnChatChange.add(repaintTarget);
	}
	
	public void removeRepaintOnaddChat(JComponent repaintTarget) {
		if (repaintOnChatChange.contains(repaintTarget)) {
			repaintOnChatChange.remove(repaintTarget);
		}
	}
	
	public void addText(Message msg) {
		JPanel msgBody;
		
		if (msg.getUser() == model.getUser()) {
			msgBody = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 2));
		}
		else
		{
			msgBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
		}
		
		JPanel content = new JPanel(new BorderLayout());
		
		JLabel userDisplay = new JLabel("User: " + msg.getUser().getName());
		
		content.add(userDisplay, BorderLayout.NORTH);
		
		JComponent msgCont = msg.getContent();
		
		content.add(msgCont);
		
		content.setBackground(Color.ORANGE);
		
		msgBody.add(content);
		
		text.add(msgBody);
		
		text.revalidate();
		
	}
	
	public void addChat(JButton chatButton) {
		
		chatList.add( chatButton );
		
		chatList.repaint();
		
		for (JComponent repaintTarget : repaintOnChatChange) {
			repaintTarget.revalidate();
		}
	}
	
	public void loadChat() throws Exception{
		text.removeAll();
		
		for (Message msg : model.getCurrentChatHistory()) {
			addText(msg);
		}
		
		text.revalidate();
		text.repaint();
	}
	
	
	
}