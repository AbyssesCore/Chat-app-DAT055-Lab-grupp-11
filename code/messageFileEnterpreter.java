import java.time.LocalDateTime;

import java.io.*;

import java.sql.Date;

import java.util.List;
import java.util.ArrayList;

import java.util.Hashtable;

import javax.swing.JFileChooser;

import javax.imageio.ImageIO;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.NoSuchFileException;

import java.awt.image.BufferedImage;

import java.nio.channels.FileChannel;

class messageFileEnterpreter {
	
	protected static final Hashtable<String, messageConstructor> msgNameToConstructorMap = new Hashtable<String, messageConstructor>();
	
	static
    {
        msgNameToConstructorMap.put(TextMessage.type, new TextConstructor());
		
		msgNameToConstructorMap.put(ImgMessage.type, new ImgConstructor());
    }
	
	private final String folderPath;
	
	private String chatCasheExtension = "";
	
	private String imgCasheExtension = "";
	
	messageFileEnterpreter(String path) {
		folderPath = path;
	}
	
	messageFileEnterpreter(String path, String chatCasheExtension, String imgCasheExtension) {
		folderPath = path;
		
		this.chatCasheExtension = chatCasheExtension;
		this.imgCasheExtension = imgCasheExtension;
		
		File chatCashe = new File(folderPath + chatCasheExtension);
		
		chatCashe.mkdirs();
		
		File imgCashe = new File(folderPath + imgCasheExtension);
		
		imgCashe.mkdirs();
	}
	
	public File appendChatCashePath(String extension) {
		chatCasheExtension = extension;
		
		File chatCashe = new File(folderPath + chatCasheExtension);
		
		chatCashe.mkdirs();
		
		return chatCashe;
	}
	
	public File appendImgCashePath(String extension) {
		imgCasheExtension = extension;
		
		File imgCashe = new File(folderPath + imgCasheExtension);
		
		imgCashe.mkdirs();
		
		return imgCashe;
	}
	
	public boolean saveMessage(String chatFileName, Message msg) throws IOException {
		File chatHistory = new File(folderPath + chatCasheExtension + chatFileName);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: "  + folderPath + chatCasheExtension + chatFileName);
		}
		
		UserInterface u = msg.getUser();
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(msg.getCreateTime());
		
		msgt.addString(msg.getMsgType());
		
		msgt.addLong(u.getID());
		
		msgt.addString(u.getName());
		
		msgt.addString(new String(msg.getContent()));
		
		byte[] totalMessageLength = msgt.addLong(msgt.getMessageLength(), false);
		
		try(FileOutputStream fos = new FileOutputStream(chatHistory, true)) {
			
			fos.write(totalMessageLength);
			
			fos.write(msgt.getMessage());
		}
		
		return true;
	}
	
	public boolean saveMessage(String chatFileName, List<Message> msgList) throws IOException  {
		File chatHistory = new File(folderPath + chatCasheExtension + chatFileName);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: "  + folderPath + chatCasheExtension + chatFileName);
		}
		
		try(FileOutputStream fos = new FileOutputStream(chatHistory, true)) {
			for (Message msg: msgList) {
				UserInterface u = msg.getUser();
				
				messageTranslater msgt = new messageTranslater();
				
				msgt.addLocalDateTime(msg.getCreateTime());
				
				msgt.addString(msg.getMsgType());
				
				msgt.addLong(u.getID());
				
				msgt.addString(u.getName());
				
				msgt.addString(new String(msg.getContent()));
				
				byte[] totalMessageLength = msgt.addLong(msgt.getMessageLength(), false);
				
				fos.write(totalMessageLength);
				
				fos.write(msgt.getMessage());
			}
		}
		
		return true;
	}
	
	public void createChatCashe(String chatFileName) throws IOException {
		File f = new File(folderPath + chatCasheExtension + chatFileName);
		
		f.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(f);
		
		messageTranslater msgt = new messageTranslater();
		
		msgt.addLocalDateTime(LocalDateTime.now());
		
		fos.write(msgt.getMessage());
		
		fos.close();
	}
	
	public List<byte[]> loadRawMessages(String chatFileName, LocalDateTime lastModifide) throws IOException {
		File chatHistory = new File(folderPath + chatCasheExtension + chatFileName);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: " + folderPath + chatCasheExtension + chatFileName);
		}
		
		List<byte[]> out = new ArrayList<byte[]>();
		
		try (FileInputStream fis = new FileInputStream(chatHistory)) {
			
			LocalDateTime msgSentTime = messageTranslater.translateLocalDateTime(fis);
			
			long msgLength = messageTranslater.translateLong(fis);
			
			while (msgLength != -1 && lastModifide.compareTo(msgSentTime) > 0) {
				
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
	
	public List<Message> loadMessages(String chatFileName, LocalDateTime lastModifide) throws IOException {
		File chatHistory = new File(folderPath + chatCasheExtension + chatFileName);
		
		if (!chatHistory.exists()) {
			throw new IOException("Chat wasn't created or chat history file was deleted on server-side: " + folderPath + chatCasheExtension + chatFileName);
		}
		
		List<Message> out = new ArrayList<Message>();
		
		try (FileInputStream fis = new FileInputStream(chatHistory)) {
		
			LocalDateTime msgSentTime = messageTranslater.translateLocalDateTime(fis);
			
			long msgLength = messageTranslater.translateLong(fis);
			
			while (msgLength != -1 && lastModifide.compareTo(msgSentTime) > 0) {
				
				String buffer = messageTranslater.translateString(fis);
				
				msgSentTime = messageTranslater.parseToMessageTimeFormat(buffer);
				
				fis.skip(msgLength - buffer.length() - 1);
				
				msgLength = messageTranslater.translateLong(fis);
			}
			
			while (msgLength != -1) {
				out.add(messageFromInputStream(fis));
				
				msgLength = messageTranslater.translateLong(fis);
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
	
	public static File pickFile(FileNameExtensionFilter ... filters) throws NoSuchFileException {
		// Source - https://stackoverflow.com/a/40255184
		// Posted by matt, modified by community. See post 'Timeline' for change history
		// Retrieved 2026-03-06, License - CC BY-SA 4.0
		
		JFileChooser chooser = new JFileChooser();
		
		for (FileNameExtensionFilter filter : filters)
			chooser.setFileFilter(filter);
		
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		
		throw new NoSuchFileException("No file were selected");
	}
	
	
	public static ImgObject choseImgFile() throws NoSuchFileException, IOException, Exception {
		File f = pickFile(new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif", "png"));
		
		
		return new ImgObject( f );
	}
	
	private static String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}
	
	private static String getFileNamePart(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(0, i);
		}
		return fileName;
	}
	
	public File saveImg(BufferedImage img, String name) throws IOException {
		return saveImg(img, getFileNamePart(name), getFileExtension(name));
	}
	
	public File saveImg(BufferedImage img, String name, String extension) throws IOException {
		
		File imgPath = new File(folderPath + imgCasheExtension + name + "." + extension);
		
		ImageIO.write(img, extension, imgPath);
		
		return imgPath;
	}
	
	public void saveImgFromStream(InputStream imgStream, long imgLength, String name) throws IOException {
		File f = new File(name);
		
		FileOutputStream fos = new FileOutputStream(f);
		
		int bytes = 0;
		
		byte[] buffer = new byte[4 * 1024];
		while (imgLength > 0
			   && (bytes = imgStream.read(
					   buffer, 0,
					   (int)Math.min(buffer.length, imgLength)))
					  != -1) {
			// Here we write the file using write method
			fos.write(buffer, 0, bytes);
			imgLength -= bytes; // read upto file imgLength
		}
		System.out.println("File is Received");
		fos.close();
	}
	
	public BufferedImage loadImg(String name) throws IOException{
		 File f = new File(folderPath + imgCasheExtension + name);
		 
		 if (!f.exists())
			 throw new IOException("Image dose not exist: " + f);
		 
		 return ImageIO.read(f);
	}
	
	public boolean isImgCached(String imgName) {
		return new File(folderPath + imgCasheExtension + imgName).exists();
	}
	
	public boolean isChatCached(String chatFileName) {
		return new File(folderPath + chatCasheExtension + chatFileName).exists();
	}
	
	public ImgObject getFileRefference(String imgName) throws IOException {
		File f = new File(folderPath + imgCasheExtension + imgName);
		
		if (!f.exists())
			throw new IOException("Image dose not exist: " + f);
		
		return new ImgObject(f);
	}
	
	public ImgObject copyFileToImgCashe(File sourcePath, String resultName) throws FileNotFoundException, IOException{
		File newFile = new File(folderPath + imgCasheExtension + resultName);
		
		// Source - https://stackoverflow.com/a/115086
		// Posted by Josh, modified by community. See post 'Timeline' for change history
		// Retrieved 2026-03-10, License - CC BY-SA 3.0
		
		try (FileChannel source = new FileInputStream(sourcePath).getChannel();
			 FileChannel destination = new FileOutputStream( newFile ).getChannel())
         {
			 destination.transferFrom(source, 0, source.size());
		 }
		 
		 return new ImgObject(newFile);
	}
}