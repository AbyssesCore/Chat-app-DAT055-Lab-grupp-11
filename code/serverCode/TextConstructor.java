import java.io.IOException;

import java.io.InputStream;

class TextConstructor implements messageConstructor {
	
	TextConstructor() {
		
	}
	
	
	public Message constructMessage(InputStream is) throws IOException{
		
		long userID = messageTranslater.translateLong(is);
		
		String userName = messageTranslater.translateString(is);
		
		String messageContent = messageTranslater.translateString(is);
		
		return new TextMessage(new User(userName, userID), messageContent);
	}
}