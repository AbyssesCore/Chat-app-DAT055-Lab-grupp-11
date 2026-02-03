import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.*;

import java.io.*;


class Messanger extends JFrame{
	JFrame jf;
	
	Model model;
	
	View view;
	
	Controller controller;
	LogInController lic;
	
	
	Messanger() {
		jf = new JFrame("Drawing game");
		
		jf.setSize(650, 300);
		
    	jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		
		loggingScreen();
		
	}
	
	public static void main (String[] args){
		new Messanger();
	}
	
	private void clearScreen() {
		jf.getContentPane().removeAll();
		jf.repaint();
	}
	
	public void loggedIn(User u) {
		clearScreen();
		
		model = new Model(u);
		
		view = new View(model);
		
		controller = new Controller(model, view);
		
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
		
		jf.add(lic.logOut, BorderLayout.EAST);
		
		jf.setVisible(true);
	}
	
	public void loggingScreen() {
		clearScreen();
		
		jf.setLayout(new BorderLayout());
		
		lic = new LogInController(this);
		
		jf.add(lic.logInInput, BorderLayout.NORTH);
		
		jf.add(lic.logIn, BorderLayout.EAST);
		
		jf.setVisible(true);
	}
}