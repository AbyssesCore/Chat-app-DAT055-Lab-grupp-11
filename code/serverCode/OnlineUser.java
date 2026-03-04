import java.net.InetSocketAddress;
import java.net.InetAddress;

class OnlineUser implements UserInterface {
	private InetSocketAddress contactAddress;
	
	final private long persId;
	
	public String name;
	
	OnlineUser(String username, long userID, InetAddress addr, int port) {
		persId = userID;
		
		this.name = name;
		
		contactAddress = new InetSocketAddress(addr, port);
	}
	
	OnlineUser(UserInterface u, InetAddress addr, int port) {
		persId = u.getID();
		
		this.name = u.getName();
		
		contactAddress = new InetSocketAddress(addr, port);
	}
	
	public long getID() {
		return persId;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (object == null)
			return false;
		
		if (object.getClass() != this.getClass())
			return false;
		
		OnlineUser otherUser = (OnlineUser) object;
		
		return otherUser.name.equals(this.name) && (otherUser.persId == this.persId) && this.contactAddress.equals(otherUser.contactAddress);
	}
	
}