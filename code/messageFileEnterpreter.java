import java.time.LocalDateTime;

import java.io.*;

import java.sql.Date;

import java.util.List;
import java.util.ArrayList;

import java.util.Hashtable;

class messageFileEnterpreter {
	
	protected static final Hashtable<String, messageConstructor> msgNameToConstructorMap = new Hashtable<String, messageConstructor>();
	
	static
    {
        msgNameToConstructorMap.put(TextMessage.type, new TextConstructor());
		
		msgNameToConstructorMap.put(ImgMessage.type, new ImgConstructor());
    }
	
	private final String folderPath;
	
	messageFileEnterpreter(String path) {
		folderPath = path;
	}
	
	public boolean saveMessage(long chatID, Message msg) throws IOException {
		File chatHistory = new File(folderPath + chatID);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: "  + folderPath + chatID);
		}
		
		UserInterface u = msg.getUser();
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		msgt.addString(msg.getMsgType());
		
		msgt.addLong(u.getID());
		
		msgt.addString(u.getName());
		
		msgt.addString(new String(msg.getContent()));
		
		String totalMessageLength = msgt.addLong(msgt.getMessageLength(), false);
		
		try(FileOutputStream fos = new FileOutputStream(chatHistory, true)) {
			
			fos.write((totalMessageLength +  msgt.getMessage()).getBytes());
		}
		
		return true;
	}
	
	public void createChatCashe(long chatId) throws IOException {
		File f = new File(folderPath + chatId);
		
		f.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(f);
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		fos.write(msgt.getMessage().getBytes());
		
		fos.close();
	}
	
	public List<byte[]> loadRawMessages(long chatID, LocalDateTime lastModifide) throws IOException {
		File chatHistory = new File(folderPath + chatID);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: " + chatID);
		}
		
		List<byte[]> out = new ArrayList<byte[]>();
		
		try (FileInputStream fis = new FileInputStream(chatHistory)) {
			
			LocalDateTime msgSentTime = messageTranslater.translateLocalDateTime(fis);
			
			long msgLength = messageTranslater.translateLong(fis);
			
			while (msgLength != -1 && lastModifide.compareTo(msgSentTime) >= 0) {
				
				String buffer = messageTranslater.translateString(fis);
				
				msgSentTime = messageTranslater.parseToMessageTimeFormat(buffer);
				
				fis.skip(msgLength - buffer.length() - 1);
				
				msgLength = messageTranslater.translateLong(fis);
			}
			
			while (msgLength != -1) {
				byte[] section = new byte[(int)msgLength];
				
				fis.read(section, 0, (int)msgLength);
				
				out.add(section);
				
				msgLength = messageTranslater.translateLong(fis);
			}
			
		}
		catch (Exception e){
			e.printStackTrace();
			
			return null;
		}
		
		return out;
	}
	
	public List<Message> loadMessages(long chatID, LocalDateTime lastModifide) throws IOException {
		File chatHistory = new File(folderPath + chatID);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: " + chatID);
		}
		
		List<Message> out = new ArrayList<Message>();
		
		try (FileInputStream fis = new FileInputStream(chatHistory)) {
		
			LocalDateTime msgSentTime = messageTranslater.translateLocalDateTime(fis);
			
			long msgLength = messageTranslater.translateLong(fis);
			
			while (msgLength != -1 && lastModifide.compareTo(msgSentTime) >= 0) {
				
				String buffer = messageTranslater.translateString(fis);
				
				msgSentTime = messageTranslater.parseToMessageTimeFormat(buffer);
				
				fis.skip(msgLength - buffer.length() - 1);
				
				msgLength = messageTranslater.translateLong(fis);
			}
			
			while (msgLength != -1) {
				out.add(messageFromInputStream(fis));
			}
			
		}
		catch (Exception e){
			e.printStackTrace();
			
			return null;
		}
		
		return out;
	}
	
	public static Message messageFromInputStream(InputStream is) throws IOException  {
		LocalDateTime msgSentTime = messageTranslater.translateLocalDateTime(is);
		
		String type = messageTranslater.translateString(is);
		
		messageConstructor constructor = msgNameToConstructorMap.get(type);
		
		Message msg = constructor.constructMessage(is);
		
		msg.setArrivleTime(msgSentTime);
		
		return msg;
	}
}