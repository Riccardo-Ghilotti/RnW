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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;

public class Text {

	private static ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");
	private ObjectId id;
	private String title;
	private ArrayList<String> intro;
	private ArrayList<String> corpus;
	private ArrayList<String> conclusion;
	private User author;
	
	
	public Text(String title, ArrayList<String> intro, ArrayList<String> corpus,
			ArrayList<String> conclusion, User author) {
		this.title = title;
		this.intro = intro;
		this.corpus = corpus;
		this.conclusion = conclusion;
		this.author = author;
		
		this.id = Database.insert(title, intro, corpus, conclusion, author);
		
	}
	

	
	public Text(ObjectId id, String title, ArrayList<String> intro, 
			ArrayList<String> corpus, ArrayList<String> conclusion, User author)
	{
		this.id = id;
		this.title = title;
		this.intro = intro;
		this.corpus = corpus;
		this.conclusion = conclusion;
		this.author = author;
	}
	
	/* public Text(int id) {
		this.id = id;
		try {
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM texts WHERE id = (?)"
					);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				this.title = rs.getString("title");
				this.author = User.getUser(rs.getInt("userId"));
				this.intro = rs.getString("introduction");
				this.corpus = rs.getString("corpus");
				this.conclusion = rs.getString("conclusion");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	
	public static String compose(ArrayList<String> sections) {
		String macroSecotion = String.join("|", sections);
		return macroSecotion;
	}
	
	
	public static ArrayList<String[]> getAllTextsFromAuthor(User u) {
		// ArrayList<ArrayList<String>> texts = new ArrayList<ArrayList<String>>();
		FindIterable<Document> textIt = 
				Database.getAllTextsFromAuthor(u.getId());
		ArrayList<String[]> textList = new ArrayList<>();
		for (Document doc : textIt) {
			String[] temp = {doc.getObjectId("_id").toString(),
					doc.getString("title")};
			textList.add(temp);
		}
		return textList;
	}
	
	public static Text getText(ObjectId id2) {
		ObjectMapper mp = new ObjectMapper();
		Document doc = Database.getText(id2);
		
		ObjectId id = doc.getObjectId("_id");
		String title = doc.getString("title");
		ArrayList<String> intro = doc.get("intro", ArrayList.class);
		ArrayList<String> corpus = doc.get("corpus", ArrayList.class);
		ArrayList<String> conc = doc.get("conclusion", ArrayList.class);
		ObjectId userId = doc.getObjectId("userId");
		
		return new Text(id, title, intro, corpus, conc, User.getUser(userId));
	}
	public static Text getText(String id) {
		ObjectId idObj = null;
		if(ObjectId.isValid(id))
			idObj = new ObjectId(id);
		return getText(idObj);
	}
	
	public void changeIntro(ArrayList<String> intro, User u) 
			throws AccessDeniedException {
		if(u.equals(author)) {
			Database.update(this.id, intro, this.corpus, this.conclusion);
			this.intro = intro;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public void changeCorpus(ArrayList<String> corpus, User u) 
			throws AccessDeniedException {
		if(u.equals(author)) {	
			Database.update(this.id, this.intro, corpus, this.conclusion);
			this.corpus = corpus;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public void changeConclusion(ArrayList<String> conclusion, User u) 
			throws AccessDeniedException {
		if(u.equals(author)) {
			Database.update(this.id, this.intro, this.corpus, conclusion);
			this.conclusion = conclusion;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public boolean isAuthor(User u) {
		if (u.equals(author))
			return true;
		return false;
	}
	
	public User getAuthor() {
		return author;
	}

	public ObjectId getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public ArrayList<String> getIntro() {
		return intro;
	}

	public ArrayList<String> getCorpus() {
		return corpus;
	}

	public ArrayList<String> getConclusion() {
		return conclusion;
	}

	public boolean Equals(Text t) {
		if(t.getId() == this.getId())
			return true;
		return false;
	}



	public Boolean delete(User u) throws AccessDeniedException {
		// TODO Auto-generated method stub
		if (u.equals(author))
			return Database.deleteText(id);
		else throw new AccessDeniedException("Non sei l'autore di questo testo");
	}
}
