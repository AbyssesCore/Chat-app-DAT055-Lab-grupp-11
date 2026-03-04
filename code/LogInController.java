import javax.swing.*;
import java.util.*;

import java.time.LocalDateTime;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class LogInController {
	
	private MessagePublisher mp;
	
	private final LogInView view;
	
	private List<LogInObserver> updateOnLogIn;
	
	LogInController(LogInView view, MessagePublisher mp) {
		this.mp = mp;
		this.view = view;
		
		updateOnLogIn = new ArrayList<LogInObserver>();
		
		view.buildLoginUI();
	}
	
	public void addLogInEvent(JButton logIn) {
		logIn.addActionListener(e -> {
			int status = 0;
			
			System.out.println(view.getLogInText() + " " + view.getPasswordText());
			
			try {
				status = mp.checkLogIn(view.getLogInText(), view.getPasswordText());
			}catch (Exception err) {
				err.printStackTrace();
				
				status = 500;
			}
			
			if (status != 200)
				return;
			
			try {
				User u = mp.logIn(view.getLogInText(), view.getPasswordText());
				
				for (LogInObserver observer : updateOnLogIn)
					observer.invokeOnLogIn(u);
			}
			catch (Exception err) {
				err.printStackTrace();
			}
			
		});
	}
	
	public void addLogInObserver(LogInObserver observer) {
		updateOnLogIn.add(observer);
	}
	
	
}