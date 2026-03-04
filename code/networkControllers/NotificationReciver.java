import java.net.*;

import java.io.*;

import java.util.HashSet;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class NotificationReciver {
	
	private HttpServer clientSocket;
	
	private HashSet<NotificationListener> subscribers = new HashSet<NotificationListener>();
	
	public void startReciving()  throws Exception  {
		clientSocket = HttpServer.create(new InetSocketAddress(0), 0);
		
		System.out.println("Port used: " + clientSocket.getAddress());
		
		clientSocket.createContext("/updateChat", (HttpExchange t) -> {
			InputStream is = t.getRequestBody();
			byte[] bytes = new byte[is.available()];
			
			is.read(bytes, 0, is.available());
			
			
			for (NotificationListener subscriber : subscribers)
				subscriber.reciveChatUppdate(bytes);
		});
	}
	
	public void stopReciving() {
		clientSocket.stop(0);
	}
	
	public void addSubscriber(NotificationListener subscriber) {
		if (subscriber == null)
			return;
		
		System.out.println(subscriber);
		
		subscribers.add(subscriber);
	}
	
	public void removeSubscriber(NotificationListener subscriber) {
		subscribers.remove(subscriber);
	}
}