package project.RnW.model;

import org.bson.types.ObjectId;

public class Comment {
	
	private ObjectId id;
	private User  user;
	private String content;

	
	
	public Comment(ObjectId id, User user, String content) {
		this.id = id;
		this.user = user;
		this.content = content;
	}
	
	//returns id of this comment
	public ObjectId getId() {
		return id;
	}
	
	//returns the User that made the comment
	public User getUser() {
		return user;
	}

	//returns the content of the comment
	public String getContent() {
		return content;
	}
	
}
