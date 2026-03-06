import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;


class messageTranslater {
	
	byte[] msg;
	
	private static final DateTimeFormatter dateTimeSaveFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
	
	// Message plan:
	// bytes:
	//		1 byte: 00 - type 0 - is extendet  00000 - length of extenshion / length if not extendet'
	// 		Types:
	//  		   00 - String 
	//   		   01 - long
	//			   10 - URL (not implemented yet)
	//			   11 - Image
	
	// extenshion exists for optinal message with long content as Image content or long chat messages.
	// Not extendet messages may only provide 31 symbols - sufficient for names / passwords / numbers
	// If length of message content greater than 32 symbols, message auto-extends
	// 			   0 - if not extendet, next 5 bytes represent message length
	//			   1 - if it is extendet, next 5 bytes provide how many bytes message length description is. It's length of length description can be at max 4. If it's greater than 4 - error thrown
	
	
	// if message extendet next 1 - 4 bytes may represent length of message, if extendet. If not extendet, this part of message dose not exists
	
	// Message content is pure byte-content. Content can be only of types String and long
	
	public messageTranslater() {
		msg = new byte[0];
	}
	
	public byte[] addString(String text) throws Error {
		return addString(text, true);
	}
	
	public byte[] addString(String text, boolean addToBody) throws Error {
		byte msgBase = 0;
		
		byte[] msgExtenshion;
		
		int offset = 1;
		
		if (text.length() < 32) {
			msgBase |= text.length();
			
			msgExtenshion = new byte[1 + text.length()];
			
			msgExtenshion[0] = msgBase;
		}
		else {
			
			int bytesInLength = calcMinByteLength(text.length());
			
			msgBase |= 0b100000 | bytesInLength;
			
			msgExtenshion = new byte[1 + bytesInLength + text.length()];
			
			byte[] longBytes = longBytesToByteArray(text.length(), bytesInLength);
			
			msgExtenshion[0] = msgBase;
			
			System.arraycopy(longBytes, 0, msgExtenshion, 1, bytesInLength);
			
			offset += bytesInLength;
		}
		
		System.arraycopy(text.getBytes(), 0, msgExtenshion, offset, text.length());
		
		if (addToBody) {
			byte[] newMsg = Arrays.copyOf(msg, msg.length + msgExtenshion.length);
			
			System.arraycopy(msgExtenshion, 0, newMsg, msg.length, msgExtenshion.length);
			
			msg = newMsg;
		}
		
		return msgExtenshion;
	}
	
	public byte[] addImg (ImgObject img) {
		return addImg(img, true);
	}
	
	public byte[] addImg(ImgObject img, boolean addToBody) {
		byte msgBase = (byte)(0b11000000);
		
		byte[] msgExtenshion;
		
		byte[] imgContent = img.getImgBytes();
		
		int offset = 1;
		
		if (imgContent.length < 32) {
			msgBase |= imgContent.length;
			
			msgExtenshion = new byte[1 + imgContent.length];
			
			msgExtenshion[0] = msgBase;
		}
		else {
			
			int bytesInLength = calcMinByteLength(imgContent.length);
			
			msgBase |= 0b100000 | bytesInLength;
			
			msgExtenshion = new byte[1 + bytesInLength + imgContent.length];
			
			msgExtenshion[0] = msgBase;
			
			byte[] longBytes = longBytesToByteArray(imgContent.length, bytesInLength);
			
			System.arraycopy(longBytes, 0, msgExtenshion, 1, bytesInLength);
			
			offset += bytesInLength;
		}
		
		System.arraycopy(imgContent, 0, msgExtenshion, offset, imgContent.length);
		
		if (addToBody) {
			byte[] newMsg = Arrays.copyOf(msg, msg.length + msgExtenshion.length);
			
			System.arraycopy(msgExtenshion, 0, newMsg, msg.length, msgExtenshion.length);
			
			msg = newMsg;
		}
		
		return msgExtenshion;
	}
	
	
	public byte[] addLong(long x) {
		return addLong(x, true);
	}
	
	
	// long cant be extendet as it's maximal size is 8 bytes < 32
	public byte[] addLong(long x, boolean addToBody) {
		int bytesInLength = calcMinByteLength(x);
		
		byte msgBase = (byte)(0b01000000 | bytesInLength);
		
		byte[] msgExtenshion = new byte[1 + bytesInLength];
		
		msgExtenshion[0] = msgBase;
		
		System.arraycopy(longBytesToByteArray(x, bytesInLength), 0, msgExtenshion, 1, bytesInLength);
		
		if (addToBody) {
			byte[] newMsg = Arrays.copyOf(msg, msg.length + msgExtenshion.length);
			
			System.arraycopy(msgExtenshion, 0, newMsg, msg.length, msgExtenshion.length);
			
			msg = newMsg;
		}
		
		return msgExtenshion;
	}
	
	
	
	public byte[] addLocalDateTime(LocalDateTime time) {
		return addString(time.format(dateTimeSaveFormat));
	}
	
	public byte[] addLocalDateTime(LocalDateTime time, boolean addToBody) {
		return addString(parseTimeToMessageFormat(time), addToBody);
	}
	
	public static LocalDateTime parseToMessageTimeFormat(String asTimeText) {
		return LocalDateTime.parse(asTimeText, dateTimeSaveFormat);
	}
	
	public static String parseTimeToMessageFormat(LocalDateTime asTimeText) {
		return asTimeText.format(dateTimeSaveFormat);
	}
	
	
	static public String translateString(InputStream is) throws IOException{
		int b = (int)is.read();
		
		if (b == -1 || ((b & 0b11000000) != 0)) {
			return "";
		}
		
		byte[] message;
		
		if ((b & 0b00100000) == 0) {
			message = new byte[b & 0x1f];
			
			for (int i = 0; i < (b & 0x1f); i++) {
				message[i] = (byte)is.read();
			}
			
		}
		else {
			long messageLength = 0;
			
			for (int i = 0; i < (b & 0x1f); i++) {
				messageLength = (messageLength << 8) + is.read();
			}
			
			message = new byte[(int)messageLength];
			
			for (int i = 0; i < messageLength; i++) {
				message[i] = (byte)is.read();
			}
		}
		
		
		return new String(message);
	}
	
	static public LocalDateTime translateLocalDateTime(InputStream is) throws IOException {
		return parseToMessageTimeFormat(translateString(is));
	}
	
	static public long translateLong(InputStream is)  throws IOException {
		int b = (int)is.read();
		
		if (b == -1 || ((b & 0b11000000) != 0b01000000)) {
			return -1;
		}
		long out = 0;
		
		for (int toRead = b & 0x1f; toRead > 0; toRead--) {
			out = (out << 8) + is.read();
			
		}
		
		return out;
	}
	
	public byte[] getMessage() {
		return msg;
	}
	
	public int getMessageLength() {
		return msg.length;
	}
	
	private int calcMinByteLength(long x) {
		int i = 0;
		
		for (; x != 0; i++) {
			
			x &= ~(0xff << i * 8);
		}
		
		return i;
	}
	
	
	private byte[] longBytesToByteArray(long content, int endByte) {
		byte[] out = new byte[endByte];
		
		int i = 0;
		while (endByte != 0) {
			
			endByte -= 1;
			
			out[i++] = (byte)((content >> (endByte * 8)) & 0xff);	
		}
		
		return out;
	}
}