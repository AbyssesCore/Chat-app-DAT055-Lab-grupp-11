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
	
	public Chat createChat(UserInterface u, String ChatName, long id) {
		Chat nc = new Chat(ChatName, id);
		List<UserInterface> nui = new ArrayList<UserInterface>();
		
		nui.add(u);
		
		chatsToUserMap.put(nc, nui);
		
		return nc;
	}
}