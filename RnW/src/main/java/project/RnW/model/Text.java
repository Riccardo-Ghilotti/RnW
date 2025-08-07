package project.RnW.model;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.bson.types.ObjectId;


public class Text {

	private ObjectId id;
	private String title;
	private ArrayList<String> intro;
	private ArrayList<String> corpus;
	private ArrayList<String> conclusion;
	private ArrayList<Comment> comments;
	private boolean isPrivate;
	private User author;
	

	public Text(ObjectId id, String title, ArrayList<String> intro, 
			ArrayList<String> corpus, ArrayList<String> conclusion, 
			ArrayList<Comment> comments, boolean isPrivate, User author)
	{
		this.id = id;
		this.title = title;
		this.intro = intro;
		this.corpus = corpus;
		this.conclusion = conclusion;
		this.comments = comments;
		this.isPrivate = isPrivate;
		this.author = author;
	}
	
	//is used to change the macro-section "Introduction" of this Text.
	public void changeIntro(ArrayList<String> intro, User u) 
			throws AccessDeniedException {
		if(this.isAuthor(u))
			this.intro = intro;
		else throw new AccessDeniedException("User isn't the author of this text");
	}
	
	//is used to change the macro-section "Corpus" of this Text.
	public void changeCorpus(ArrayList<String> corpus, User u) 
			throws AccessDeniedException {
		if(this.isAuthor(u))
			this.corpus = corpus;
		else throw new AccessDeniedException("User isn't the author of this text");
	}
	
	//is used to change the macro-section "Conclusion" of this Text.
	public void changeConclusion(ArrayList<String> conclusion, User u) 
			throws AccessDeniedException {
		if(this.isAuthor(u))
			this.conclusion = conclusion;
		else throw new AccessDeniedException("User isn't the author of this text");
	}
	
	//returns true if the inserted User u is the author of this Text.
	public boolean isAuthor(User u) {
		if (u.equals(author))
			return true;
		return false;
	}
	
	//returns the author(User) of this Text.
	public User getAuthor() {
		return author;
	}

	//returns the id of this Text.
	public ObjectId getId() {
		return id;
	}

	//returns the title of this Text.
	public String getTitle() {
		return title;
	}
	
	//returns the Introduction of this Text.
	public ArrayList<String> getIntro() {
		return intro;
	}

	
	//returns the Corpus of this Text.
	public ArrayList<String> getCorpus() {
		return corpus;
	}

	//returns the Conclusion of this Text.
	public ArrayList<String> getConclusion() {
		return conclusion;
	}

	//returns a list that contains all the comments of this Text.
	public ArrayList<Comment> getComments() {
		return comments;
	}

	//returns true if this Text and t are equals.
	public boolean equals(Text t) {
		//since the ids are unique, checking those is enough.
		if(t.getId() == this.getId())
			return true;
		return false;
	}


	//returns true if this Text is private.
	public boolean isPrivate() {
		return isPrivate;
	}


	//is used to change visibility of this Text.
	public void setPrivate(boolean isPrivate, User user) throws AccessDeniedException {
		if(isAuthor(user) || user.isAdmin())
			this.isPrivate = isPrivate;
		else throw new AccessDeniedException("Non sei l'autore di questo testo");
	}

	
}
