import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.net.InetSocketAddress;
import java.util.*;


import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class Server {
	
	public static final int PORT = 228;
	
    private HttpServer server;
    
	private ServerModel smodel;
	
	public static void main (String[] args) throws Exception {
		
		Server s = new Server();
		s.server.start();
        System.out.println("server is running on port "+PORT);
	}
	
	Server() throws Exception {
		smodel = new ServerModel();
		
		this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		server.createContext("/", (HttpExchange t) -> {
            String response = 
               "<!doctype html>"+
               "<html lang=\"en\">"+
               "<head>"+
               "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">"+
               "</head><body class=\"bg-light\">"+
               "<div class=\"container\">"+ 
               "<form action=\"run\">"+      
               "<div class=\"mb-3\">"+
               "<div class=\"input-group\">"+
               "  <input type=\"text\" name=\"student\" placeholder=\"Student ID\">"+
               "  <div class=\"input-group-append\">"+
               "    <input type=\"submit\" value=\"Run\">"+
               "  </div>"+
               "</div>"+
               "</div></form>"+
               "</div></body></html>";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
		
		server.createContext("/createChat", (HttpExchange t) -> {
            try {
               InputStream is = t.getRequestBody();
			   
			   byte[] bytes = new byte[is.available()];
			   
			   is.read(bytes, 0, is.available());
			   
			   String author = new String(Arrays.copyOfRange(bytes, 2, 2 + (int)bytes[0]) );
			   
			   String chatName = new String(Arrays.copyOfRange(bytes, 2 + (int)bytes[0], 2 + (int)bytes[0] + (int)bytes[1]) );
			   
			   System.out.println("new chat created: ");
			   
			   System.out.println("Author name: " + author);
			   
			   System.out.println("Chat name: " + chatName);
			   
			   Chat nc = smodel.createChat(author, chatName);
			   
			   if (nc == null) {
				   
				   String error = "Couldn't create chat";
				   
				   t.sendResponseHeaders(418, error.length());
				   
				   OutputStream os = t.getResponseBody();
				   os.write(error.getBytes());
				   os.close();
				   return;
			   }
			   
			   t.sendResponseHeaders(201, 0);
               OutputStream os = t.getResponseBody();
               os.close();
            } catch (Exception e) {
               e.printStackTrace();
			   
			   t.sendResponseHeaders(500, 0);
			   
			   throw new RuntimeException(e);
            }
        });
		
		server.createContext("/sendMsg", (HttpExchange t) -> {
            try {
               InputStream is = t.getRequestBody();
			   
			   byte[] bytes = new byte[is.available()];
			   
			   is.read(bytes, 0, is.available());
			   
			   System.out.println("new chat recived: ");
			   
			   System.out.println("From: " + new String(Arrays.copyOfRange(bytes, 3, 3 + (int)bytes[0]) ));
			   
			   System.out.println("At chat: " + new String(Arrays.copyOfRange(bytes, 3 + (int)bytes[0], 3 + (int)bytes[0] + (int)bytes[1]) ));
			   
			   System.out.println("msg: " + new String(Arrays.copyOfRange(bytes, 3 + (int)bytes[0] + (int)bytes[1], 3 + (int)bytes[0] + (int)bytes[1] + (int)bytes[2])) );
			   
			   
			   t.sendResponseHeaders(200, 0);
               OutputStream os = t.getResponseBody();
               os.close();
            } catch (Exception e) {
               e.printStackTrace();
			   
			   t.sendResponseHeaders(500, 0);
			   
			   throw new RuntimeException(e);
            }
        });
		
		server.createContext("/logIn", (HttpExchange t) -> {
            Map<String,String> input = queryToMap(t);
            try {
               InputStream is = t.getRequestBody();
			   
			   byte[] bytes = new byte[is.available()];
			   
			   is.read(bytes, 0, is.available());
			   
			   String logInName = new String(Arrays.copyOfRange(bytes, 2, 2 + (int)bytes[0]));
			   
			   String logInPassword = new String(Arrays.copyOfRange(bytes, 2 + (int)bytes[0], 2 + (int)bytes[0] + (int)bytes[1]));
			   
               System.out.println("Name: " + logInName );
			   
			   System.out.println("Password: " + logInPassword );
			   
			   User u = smodel.logIn(logInName, logInPassword);
			   
			   if (u == null) {
				   
				   System.out.println(u);
				   
				   String error = "No user with this log in and/or password";
				   
				   t.sendResponseHeaders(500, error.length());
				   
				   OutputStream os = t.getResponseBody();
				   os.write(error.getBytes());
				   os.close();
				   return;
			   }
			   
			   t.sendResponseHeaders(200, u.getName().length());
               OutputStream os = t.getResponseBody();
               os.write(u.getName().getBytes());
               os.close();
            } catch (Exception e) {
               e.printStackTrace();
			   
			   t.sendResponseHeaders(500, 0);
			   
			   OutputStream os = t.getResponseBody();
               os.close();
			   
               throw new RuntimeException(e);
            }
        });
		
		
		server.createContext("/logOut", (HttpExchange t) -> {
            Map<String,String> input = queryToMap(t);
            try {
               InputStream is = t.getRequestBody();
			   
			   byte[] bytes = new byte[is.available()];
			   
			   is.read(bytes, 0, is.available());
			   
               System.out.println("User: " + Arrays.copyOfRange(bytes, 5, (int)bytes[0]) );
			   
			   
			   t.sendResponseHeaders(200, bytes.length);
               OutputStream os = t.getResponseBody();
               os.write(bytes);
               os.close();
            } catch (Exception e) {
               e.printStackTrace();
               throw new RuntimeException(e);
            }
        });
	}
	
	public static Map<String, String> queryToMap(HttpExchange t){
       String query = t.getRequestURI().getRawQuery();
       Map<String, String> result = new HashMap<>();
       if(query==null)
         return result;
       for (String param : query.split("&")) {
           String[] entry = param.split("=", 2);
           if (entry.length > 1) {
               try {
                  result.put(URLDecoder.decode(entry[0], "UTF-8"), 
                             URLDecoder.decode(entry[1], "UTF-8"));
               } catch (Exception e) {
               }
           }else{
               result.put(entry[0], "");
           }
       }
       return result;
    }

}
