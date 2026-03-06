import java.io.InputStream;

import java.io.IOException;

class ImgConstructor implements messageConstructor {
	
	public Message constructMessage(InputStream is) throws IOException {
		
		System.out.println("Img constructed");
		
		return new ImgMessage(null, null);
	}
}