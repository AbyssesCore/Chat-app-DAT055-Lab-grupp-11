import java.util.*;

class ChatLister {
	
	private HashMap<Chat, List<UserInterface>> chatsToUserMap = new HashMap<Chat, List<UserInterface>>();
	
	public List<UserInterface> getChatUsers(Chat c) {
		return chatsToUserMap.get(c);
	}
	
	public List<Chat> userIsPartOf(UserInterface u) {
		
		List<Chat> out = new ArrayList<Chat>();
		
		for ( HashMap.Entry<Chat, List<UserInterface>> e : chatsToUserMap.entrySet() ) {
			if (e.getValue().contains(u) ) {
				out.add(e.getKey());
			}
		}
		
		return out;
	}
	
	public boolean joinChat(Chat ch, UserInterface u) {
		if (!chatsToUserMap.containsKey(ch)) {
			return false;
		}
		
		List<UserInterface> uList = chatsToUserMap.get(ch);
		
		if (uList.contains(u)) {
			return false;
		}
		
		uList.add(u);
		return true;
	}
	
	public Set<Chat> getAllChats() {
		return chatsToUserMap.keySet();
	}
	
	public Chat getChatByID(long chatID, UserInterface u) {
		for (Chat c : userIsPartOf(u)) {
			
			if (c.getID() == chatID) {
				return c;
			}
		}
		
		return null;
	}
	
	private Chat getAnyChatByID(long chatID) {
		for (Chat c : getAllChats()) {
			
			if (c.getID() == chatID) {
				return c;
			}
		}
		
		return null;
	}
	
	public UserInterface getChatMemberByID(long chatID, long userID) {
		
		System.out.println(chatID + " " + userID + " " + getAnyChatByID(chatID));
		
		System.out.println(chatsToUserMap);
		
		List<UserInterface> members = chatsToUserMap.get(getAnyChatByID(chatID));
		
		for (UserInterface u : members){
			if (u.getID() == userID)
				return u;
		}
		return null;
	}
	
	public Chat createChat(UserInterface u, String ChatName, long id) {
		Chat nc = new Chat(ChatName, id);
		List<UserInterface> nui = new ArrayList<UserInterface>();
		
		nui.add(u);
		
		chatsToUserMap.put(nc, nui);
		
		System.out.println(chatsToUserMap);
		
		return nc;
	}
	
	public void loadChat(Chat c, List<UserInterface> users) {
		chatsToUserMap.put(c, users);
	}
}