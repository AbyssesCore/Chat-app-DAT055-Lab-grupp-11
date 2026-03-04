import java.io.*;

class messageTranslater {
	
	String msg;
	
	// Message plan:
	// bytes:
	//		1 byte: 00 - type 0 - is extendet  00000 - length of extenshion / length if not extendet'
	// 		Types: 00 - String 
	//   		   01 - long
	
	// extenshion exists for optinal message with long content as Image content or long chat messages.
	// Not extendet messages may only provide 31 symbols - sufficient for names / passwords / numbers
	// If length of message content greater than 32 symbols, message auto-extends
	// 			   0 - if not extendet, next 5 bytes represent message length
	//			   1 - if it is extendet, next 5 bytes provide how many bytes message length description is. It's length of length description can be at max 4. If it's greater than 4 - error thrown
	
	
	// if message extendet next 1 - 4 bytes may represent length of message, if extendet. If not extendet, this part of message dose not exists
	
	// Message content is pure byte-content. Content can be only of types String and long
	
	public messageTranslater() {
		msg = "";
	}
	
	public String addString(String text) throws Error {
		byte msgBase = 0;
		
		String msgExtenshion = "";
		
		if (text.length() < 32) {
			msgBase |= text.length();
			
			msgExtenshion += new String(new byte[] { msgBase });
		}
		else {
			
			int bytesInLength = calcMinByteLength(text.length());
			
			msgBase |= 0b100000 | bytesInLength;
			
			msgExtenshion += new String(new byte[] { msgBase });
			
			msgExtenshion += longBytesToString(text.length(), bytesInLength);
		}
		
		msgExtenshion += text;
		
		msg += msgExtenshion;
		
		return msgExtenshion;
	}
	
	
	// long cant be extendet as it's maximal size is 8 bytes < 32
	public String addLong(long x) {
		int bytesInLength = calcMinByteLength(x);
		
		byte msgBase = 0b01000000;
		
		msgBase |= bytesInLength;
		
		String msgExtenshion = new String(new byte[] { msgBase } ) + longBytesToString(x, bytesInLength);
		
		msg += msgExtenshion;
		
		return msgExtenshion;
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
			return new String(message);
		}
		else {
			byte[] messageLength = new byte[b & 0x1f];
			
			for (int i = 0; i < (b & 0x1f); i++) {
				messageLength[i] = (byte)is.read();
			}
		}
		
		return "";
	}
	
	static public long translateLong(InputStream is)  throws IOException {
		int b = (int)is.read();
		
		if (b == -1 || ((b & 0b11000000) != 0b01000000)) {
			return -1;
		}
		long out = 0;
		
		for (int toRead = b & 0x1f; toRead > 0; toRead--) {
			out = out << 8;
			
			out += is.read();
		}
		
		return out;
		
	}
	
	public String getMessage() {
		return msg;
	}
	
	public int getMessageLength() {
		return msg.length();
	}
	
	private int calcMinByteLength(long x) {
		int i = 0;
		
		for (; x != 0; i++) {
			
			x &= ~(0xff << i * 8);
			
			
		}
		
		return i;
	}
	
	private String longBytesToString(long content, int endByte) {
		
		String out = "";
		
		while (endByte != 0) {
			
			endByte -= 1;
			
			out += new String( new byte[] { (byte)((content >> (endByte * 8)) & 0xff) } );
		}
		
		return out;
	}
}