import java.net.*;

import java.io.*;

import java.util.List;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class NotificationReciver {
	
	private HttpServer clientSocket;
	
	private NotificationListener subscriber;
	
	NotificationReciver(NotificationListener subscriber) throws Exception {
		this.subscriber = subscriber;
		
		clientSocket = HttpServer.create(new InetSocketAddress(0), 0);
		
		System.out.println("Port used: " + clientSocket.getAddress());
		
		clientSocket.createContext("/updateChat", (HttpExchange t) -> {
			InputStream is = t.getRequestBody();
			byte[] bytes = new byte[is.available()];
			
			is.read(bytes, 0, is.available());
			
			subscriber.reciveChatUppdate(bytes);
		});
	}
	
	public void setSubscriber(NotificationListener subscriber) {
		this.subscriber = subscriber;
	}
}