import java.net.*;

import java.io.*;

import java.util.HashSet;

import java.time.LocalDateTime;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class NotificationReciver {
	
	private HttpServer clientSocket;
	
	private HashSet<NotificationListener> subscribers = new HashSet<NotificationListener>();
	
	public void startReciving()  throws Exception  {
		clientSocket = HttpServer.create(new InetSocketAddress(0), 0);
		
		clientSocket.createContext("/updateTextChat", (HttpExchange t) -> {
			InputStream is = t.getRequestBody();
			
			long senderID = messageTranslater.translateLong(is);
			
			long chatID = messageTranslater.translateLong(is);
			
			String textContent = messageTranslater.translateString(is);
			
			LocalDateTime sendTime = messageTranslater.translateLocalDateTime(is);
			
			System.out.println("From: " + senderID + " in " + chatID + " got " + textContent + " at " + sendTime);
			
			for (NotificationListener subscriber : subscribers)
				subscriber.reciveChatTextUppdate(senderID, chatID, textContent, sendTime);
			
			t.sendResponseHeaders(200, 0);
			
			OutputStream os = t.getResponseBody();
			os.close();
			
			
		});
		
		clientSocket.start();
	}
	
	public void stopReciving() {
		clientSocket.stop(0);
		
		clientSocket = null;
	}
	
	public InetSocketAddress getSocketAddres() {
		if (!isActive())
			return null;
		
		return clientSocket.getAddress();
	}
	
	public boolean isActive() {
		return clientSocket != null;
	}
	
	public void addSubscriber(NotificationListener subscriber) {
		if (subscriber == null)
			return;
		
		subscribers.add(subscriber);
	}
	
	public void removeSubscriber(NotificationListener subscriber) {
		subscribers.remove(subscriber);
	}
}