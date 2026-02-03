import javax.swing.*;
import java.util.*;

class Controller {
	Model model;
	View view;
	
	public JTextArea input;
	public JButton send;
	
	public List<JButton> chats;
	
	public JButton addChat;
	
	Controller(Model model, View view) {
		this.model = model;
		this.view = view;
		
		chats = new ArrayList<JButton>();
		
		input = new JTextArea("Type Here!");
		send = new JButton("Send");
		
		addChat = new JButton("Create chat");
		
		
		send.addActionListener(e -> {
			
			try {
				view.addText(model.sendMessage(input.getText()));
				
			}
			catch (Exception err) {
				System.out.println(err);
			}
			
		});
		
		addChat.addActionListener(e -> {
			
			Chat nc = model.addChat("Test");
			
			JButton ncButton = new JButton("Test of minw");
			
			chats.add(ncButton);
			view.addChat(ncButton);
			
			if (model.selectChat(nc)) {
				try {
					view.loadChat();
				}
				catch (Exception err) {
					System.out.println(err);
				}
				
			}
			
			ncButton.addActionListener(ncE -> {
				if (model.selectChat(nc)) {
					try {
						view.loadChat();
					}
					catch (Exception err) {
						System.out.println(err);
					}
					
				}
			});
		});
	}
}