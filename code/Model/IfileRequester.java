import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.util.List;
import java.time.LocalDateTime;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.MalformedURLException;

interface IfileRequester {
	public BufferedImage requestImg(String imgName) throws Exception;
	public List<Chat> getAllUserChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException;
	public List<UserInterface> getChatMembers(long chatID) throws IOException, ProtocolException, MalformedURLException;
	public List<Message> getChatHistory(long chatID, LocalDateTime lastMessageSendTime) throws IOException, ProtocolException, MalformedURLException;
	public List<Chat> getAvailableChats(UserInterface u) throws IOException, ProtocolException, MalformedURLException;
}