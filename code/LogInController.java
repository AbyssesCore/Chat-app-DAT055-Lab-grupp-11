import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class LogInController {
	
	public JTextArea logInInput;
	public JTextArea passwordInput;
	public JButton logIn;
	
	public JButton logOut;
	
	public Messanger msngr;
	
	private HttpServer clientSocket;
	
	LogInController(Messanger msngr) {
	
		
		this.msngr = msngr;
		
		logInInput = new JTextArea();
		
		passwordInput = new JTextArea();
		
		logIn = new JButton();
		
		logIn.addActionListener(e -> {
			try {
				URL url = new URL("http://localhost:228/logIn");
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				con.setDoOutput(true);
				
				con.setRequestProperty("Content-Type", "application/text");
				
				DataOutputStream os = new DataOutputStream(con.getOutputStream());
				
				String logInName = logInInput.getText();
				
				String password = "asd"; // no password text input yet
				
				os.writeBytes(((char)(logInName.length() & 0xFF) + "" + (char)(password.length() & 0xFF) ) + logInName + password);
				
				os.flush();
				
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				
				BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
				
				String inputLine;
				
				StringBuffer content = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				
				in.close();
				
				int status = con.getResponseCode();
				
				con.disconnect();
				
				if (status == 200) {
					msngr.loggedIn(new User(content.toString()));
				}
			}
			catch (Exception err) {
				System.out.println(err);
			}
		});
		
		logOut = new JButton("Log out");
		
		logOut.addActionListener(e -> {
			msngr.loggingScreen();
		});
		
	}
}