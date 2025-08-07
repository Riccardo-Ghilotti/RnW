package project.RnW.model;


import org.bson.types.ObjectId;


public class User {
	
	private ObjectId id;
	private String name;
	private boolean admin;
	
	
	public User(ObjectId id, String name, boolean admin) {
		this.id = id;
		this.name = name;
		this.admin = admin;
	}

	//is used to change the name of the account
	public void changeName(String name) {
		this.name=name;
	}


	//returns the id of the account
	public ObjectId getId() {
		return id;
	}

	//returns the name of the account
	public String getName() {
		return name;
	}
	
	//returns true if the User is an administrator
	public boolean isAdmin() {
		return admin;
	}
	
	//returns true if this User is equal to u
	public boolean equals(User u) {
		//since the ids are unique, checking those is enough
		if(u.getId().equals(this.getId())) 
			return true;
		return false;
	}

	//returns true if u is the owner of this account
	public boolean isOwner(User u) {
		return this.equals(u);
		
	}
	
	
	
}

