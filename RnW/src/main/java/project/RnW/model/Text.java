package project.RnW.model;

import java.nio.file.AccessDeniedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;

import project.RnW.controller.ControllerUtils;
import project.RnW.mappers.mapperText;

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
	
	//returns a list that contains all the comments of this Text
	public ArrayList<Comment> getComments() {
		return comments;
	}

	//is used to change the macro-section "Introduction" of this Text
	public void changeIntro(ArrayList<String> intro, User u) {
		this.intro = intro;
	}
	
	//is used to change the macro-section "Corpus" of this Text
	public void changeCorpus(ArrayList<String> corpus, User u) {
		this.corpus = corpus;
	}
	
	//is used to change the macro-section "Conclusion" of this Text
	public void changeConclusion(ArrayList<String> conclusion, User u) {
		this.conclusion = conclusion;
	}
	
	//returns true if the inserted User u is the author of this text
	public boolean isAuthor(User u) {
		if (u.equals(author))
			return true;
		return false;
	}
	
	//returns the author(User) of this Text
	public User getAuthor() {
		return author;
	}

	//returns the id of this Text
	public ObjectId getId() {
		return id;
	}

	//returns the title of this Text
	public String getTitle() {
		return title;
	}
	
	//returns the Introduction of this Text
	public ArrayList<String> getIntro() {
		return intro;
	}

	
	//returns the Corpus of this Text
	public ArrayList<String> getCorpus() {
		return corpus;
	}

	//returns the Conclusion of this Text
	public ArrayList<String> getConclusion() {
		return conclusion;
	}

	//returns true if this Text and t are equals
	public boolean Equals(Text t) {
		//since the ids are unique, checking those is enough
		if(t.getId() == this.getId())
			return true;
		return false;
	}


	//returns true if this Text is private
	public boolean isPrivate() {
		return isPrivate;
	}


	//is used to change visibility of this Text
	public void setPrivate(boolean isPrivate, User user) {
		this.isPrivate = isPrivate;
	}



	
	
	
}
