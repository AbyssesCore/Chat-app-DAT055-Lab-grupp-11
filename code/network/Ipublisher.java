import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.util.List;
import java.time.LocalDateTime;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.MalformedURLException;

interface Ipublisher {
	public int sendLogOut(UserInterface u) throws Exception;
	public int postMessage(UserInterface u, String msgContent, long chatID) throws Exception;
	public String postImg(UserInterface u, ImgObject img, long chatID) throws Exception;
	public long postNewChat(UserInterface u, String chatName) throws Exception;
	public int createUser(String username, String password, String displayName) throws IOException, ProtocolException, MalformedURLException;
	public int checkLogIn(String username, String password) throws IOException, ProtocolException, MalformedURLException;
	public User logIn(String username, String password, int portUsed) throws IOException, ProtocolException, MalformedURLException;
	public List<UserInterface> joinChat(UserInterface u, long chatID) throws IOException, ProtocolException, MalformedURLException;
	
}