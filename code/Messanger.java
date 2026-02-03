import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.io.*;


class Messanger {
	public static void main (String[] args){
		JFrame jf = new JFrame("Drawing game");
		
		jf.setSize(650, 300);
    	jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		User u = new User();
		
		Model model = new Model(u);
		
		View view = new View(model);
		
		Controller controller = new Controller(model, view);
		
		controller.input.setLayout(new BorderLayout());
		
		controller.input.add(controller.send, BorderLayout.EAST);
		
		jf.setLayout(new BorderLayout());
		
		jf.add(controller.input, BorderLayout.SOUTH);
		
		JScrollPane chatScroll = new JScrollPane(view.chatList);
		
		chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		chatScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel chatsDisplay = new JPanel();
		
		chatsDisplay.setLayout(new BorderLayout());
		
		chatsDisplay.add(chatScroll);
		
		chatsDisplay.add(controller.addChat, BorderLayout.NORTH);
		
		view.addRepaintOnaddChat(chatsDisplay);
		
		JPanel textSizeScaler = new JPanel( new BorderLayout() );
		
		textSizeScaler.add(view.getTextComponent(), BorderLayout.PAGE_START);
		
		JScrollPane textScroller = new JScrollPane(textSizeScaler);
		
		textScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		textScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		jf.add(textScroller);
		
		jf.add(chatsDisplay, BorderLayout.WEST);
		
		jf.setVisible(true);
	}
}