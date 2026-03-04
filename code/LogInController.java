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
	
	public JTextArea logInInput;
	public JTextArea passwordInput;
	public JButton logIn;
	
	public Messanger msngr;
	
	private HttpServer clientSocket;
	
	LogInController(Messanger msngr) {
		
		this.msngr = msngr;
		
		logInInput = new JTextArea();
		
		passwordInput = new JTextArea();
		
		logIn = new JButton();
		
		logIn.addActionListener(e -> {
			try {
				URL url = new URL("http://localhost:228/checkLogIn");
				
				HttpURLConnection con2 = (HttpURLConnection) url.openConnection();
				con2.setRequestMethod("POST");
				
				con2.setDoOutput(true);
				
				con2.setRequestProperty("Content-Type", "application/text");
				
				DataOutputStream os = new DataOutputStream(con2.getOutputStream());
				
				messageTranslater msgt = new messageTranslater();
				
				msgt.addLocalDateTime(LocalDateTime.now());
				
				msgt.addString("asd");
				
				msgt.addString("asd"); // no password text input yet
				
				os.writeBytes(msgt.getMessage());
				
				os.flush();
				
				con2.setConnectTimeout(5000);
				con2.setReadTimeout(5000);
				
				int status = con2.getResponseCode();
				
				if (status != 200)
					return;
				
				con2.disconnect();
			}
			catch (Exception err) {
				System.out.println(err);
			}
			
			try {
				URL url = new URL("http://localhost:228/logIn");
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				
				con.setDoOutput(true);
				
				con.setRequestProperty("Content-Type", "application/text");
				
				DataOutputStream os = new DataOutputStream(con.getOutputStream());
				
				messageTranslater msgt = new messageTranslater();
				
				msgt.addString(logInInput.getText());
				
				msgt.addString("asd"); // no password text input yet
				
				os.writeBytes(msgt.getMessage());
				
				os.flush();
				
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				
				int status = con.getResponseCode();
				
				System.out.println(status);
				
				if (status == 200) {
					InputStream is = con.getInputStream();
					try {
						String name = msgt.translateString(is);
						
						long id = msgt.translateLong(is);
						
						System.out.println("Name: " + name  + " ID: " + id);
						
						
						msngr.loggedIn(new User(name, id));
					}
					catch (Exception err) {
						err.printStackTrace();
						return;
					}
					
				}
				
				con.disconnect();
			}
			catch (Exception err) {
				System.out.println(err);
			}
			
		});
		
	}
}