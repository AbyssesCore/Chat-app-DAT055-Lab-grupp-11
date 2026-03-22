import java.io.Serializable;

class User implements UserInterface, Serializable {
	final private long persId;
	
	public String name;
	
	User(String name, long id) {
		persId = id;
		
		this.name = name;
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
		
		User otherUser = (User) object;
		
		return otherUser.name.equals(this.name) && (otherUser.persId == this.persId);
	}
	
	public String toString() {
		return "[ UserID= " + persId + ", UserName= " + name + " ]";
	}
}