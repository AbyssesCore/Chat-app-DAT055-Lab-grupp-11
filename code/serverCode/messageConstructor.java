import java.io.InputStream;

import java.io.IOException;


interface messageConstructor {
	public Message constructMessage(InputStream is)  throws IOException;
}