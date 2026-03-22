import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.*;
import java.net.URLDecoder;

import java.net.*;

import java.util.List;
import java.util.ArrayList;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class messageDistributor {
	
	public void sendTextTo(OnlineUser u, long chatID, TextMessage msg) {
		try {
			URL url = u.getURL("updateTextChat");
			
			System.out.println(u + " got " + msg + " Send to " + url);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			
			con.setDoOutput(true);
			
			con.setRequestProperty("Content-Type", "application/text");
			
			DataOutputStream os = new DataOutputStream(con.getOutputStream());
			
			messageTranslater msgt = new messageTranslater();
			
			msgt.addLong(msg.getUser().getID());
			
			msgt.addLong(chatID);
			
			msgt.addString(msg.getText());
			
			msgt.addLocalDateTime(msg.getCreateTime());
			
			os.write(msgt.getMessage());
			
			os.flush();
			
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void sendImgTo(OnlineUser toNotify, long chatID, ImgMessage msg) {
		try {
			URL url = toNotify.getURL("updateImgChat");
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			
			con.setDoOutput(true);
			
			con.setRequestProperty("Content-Type", "application/text");
			
			DataOutputStream os = new DataOutputStream(con.getOutputStream());
			
			messageTranslater msgt = new messageTranslater();
			
			msgt.addLong(msg.getUser().getID());
			
			msgt.addLong(chatID);
			
			msgt.addString(msg.getImg().getImgNamePart());
			
			msgt.addLocalDateTime(msg.getCreateTime());
			
			os.write(msgt.getMessage());
			
			os.flush();
			
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void userJoinedChat(OnlineUser toNotify, long chatID, UserInterface whoJoined) {
		try {
			URL url = toNotify.getURL("newChatMember");
			
			System.out.println(toNotify + " got " + whoJoined + " joined " + chatID);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			
			con.setDoOutput(true);
			
			con.setRequestProperty("Content-Type", "application/text");
			
			DataOutputStream os = new DataOutputStream(con.getOutputStream());
			
			messageTranslater msgt = new messageTranslater();
			
			msgt.addLong(whoJoined.getID());
			
			msgt.addString(whoJoined.getName());
			
			msgt.addLong(chatID);
			
			os.write(msgt.getMessage());
			
			os.flush();
			
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
}