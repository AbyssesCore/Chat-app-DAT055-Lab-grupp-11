import java.io.InputStream;

import java.io.IOException;

import java.io.File;

class ImgConstructor implements messageConstructor {
	
	public Message constructMessage(InputStream is) throws IOException {
		
		long userID = messageTranslater.translateLong(is);
		
		String userName = messageTranslater.translateString(is);
		
		String imgName = messageTranslater.translateString(is);
		
		return new ImgMessage(new User(userName, userID), new ImgObject(new File(imgName)));
	}
}