import java.net.URL;
import java.net.InetAddress;

import java.net.MalformedURLException;

class OnlineUser implements UserInterface {
	private URL contactAddress;
	
	final private long persId;
	
	public String name;
	
	OnlineUser(String username, long userID, InetAddress addr, int port) throws MalformedURLException{
		persId = userID;
		
		this.name = name;
		
		contactAddress = new URL("http:/" + addr + ":" + port);
		
	}
	
	OnlineUser(UserInterface u, InetAddress addr, int port) throws MalformedURLException{
		persId = u.getID();
		
		this.name = u.getName();
		
		contactAddress = new URL("http:/" + addr + ":" + port);
	}
	
	public URL getURL(String addition) throws MalformedURLException {
		return  new URL(contactAddress, "/" + addition);
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
	
	public String toString() {
		return "[UserID= " + persId + ", name= " + name + ", address= " + contactAddress + "]";
	}
	
}