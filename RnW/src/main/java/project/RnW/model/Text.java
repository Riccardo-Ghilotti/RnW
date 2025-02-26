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

public class Text {

	private static ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");
	private int id;
	private String title;
	private String intro;
	private String corpus;
	private String conclusion;
	private User author;
	
	
	public Text(String title, String intro, String corpus, String conclusion, User author) {
		this.title = title;
		this.intro = intro;
		this.corpus = corpus;
		this.conclusion = conclusion;
		this.author = author;
		
		this.id = this.insert();
	}
	
	public Text(String title, ArrayList<String> intro, ArrayList<String> corpus, ArrayList<String> conclusion, User author) {
		this.title = title;
		this.intro = compose(intro);
		this.corpus = compose(corpus);
		this.conclusion = compose(conclusion);
		this.author = author;
		
		this.id = this.insert();
		
	}
	
	private static Connection loadDB() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbInfo.getString("db.url"), dbInfo.getString("db.user"), dbInfo.getString("db.pwd"));
		return conn;
	}


	private int insert() {
		int id = -1;
		try{
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("INSERT INTO texts (title, userId, introduction, corpus, conclusion) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, title);
			pst.setInt(2, author.getId());
			pst.setString(3, intro);
			pst.setString(4, corpus);
			pst.setString(5, conclusion);
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			if(rs.next()) {
				id = rs.getInt(1);
			}
		}catch(Exception ex) {
			System.out.println("ERROR " + ex.toString()); //TODO: fix exception
		}
		return id;
		
	}
	
	
	public static String compose(ArrayList<String> sections) {
		String macroSecotion = String.join("|", sections);
		return macroSecotion;
	}
	
	
	public void changeIntro(String intro, User u) throws AccessDeniedException {
		if(u.equals(author)) {
			update(this.id, intro, this.corpus, this.conclusion);
			this.intro = intro;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
			
	}
	
	public void changeIntro(ArrayList<String> intro, User u) throws AccessDeniedException {
		if(u.equals(author)) {
			String introTmp = String.join("|", intro);
			update(this.id, introTmp, this.corpus, this.conclusion);
			this.intro = introTmp;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public void changeCorpus(String corpus, User u) throws AccessDeniedException {
		if(u.equals(author)) {	
			update(this.id, this.intro, corpus, this.conclusion);
			this.corpus = corpus;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public void changeCorpus(ArrayList<String> corpus, User u) throws AccessDeniedException {
		if(u.equals(author)) {	
			String corpusTmp = String.join("|", corpus);
			update(this.id, this.intro, corpusTmp, this.conclusion);
			this.corpus = corpusTmp;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}

	public void changeConclusion(String conclusion, User u) throws AccessDeniedException {
		if(u.equals(author)) {	
			update(this.id, this.intro, this.corpus, conclusion);
			this.conclusion = conclusion;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public void changeConclusion(ArrayList<String> conclusion, User u) throws AccessDeniedException {
		if(u.equals(author)) {
			String concTmp = String.join("|", conclusion);
			update(this.id, this.intro, this.corpus, concTmp);
			this.conclusion = concTmp;
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}


	private void update(int id, String intro, String corpus, String conclusion) {
		try {
			Connection conn = loadDB();
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement("UPDATE texts SET introduction=(?), corpus = (?), conclusion = (?) WHERE id = (?);");
			stmt.setString(1, intro);
			stmt.setString(2, corpus);
			stmt.setString(3, conclusion);
			stmt.setInt(4, this.id);
			stmt.executeUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("ERROR " + e.toString());
		}
	}
	
	protected void delete(User u) throws AccessDeniedException {
		if(u.equals(author) || u.isAdmin())	
			try {
				Connection conn = loadDB();
				PreparedStatement pst = conn.prepareStatement("DELETE FROM texts WHERE id=(?);");
				pst.setInt(1, this.id);
				pst.execute();
			} catch (ClassNotFoundException | SQLException e) {
				System.out.println("ERROR " + e.toString());
			}
		else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}
	
	public ArrayList<String> reconstructText(String text) {
		String[] textTmp = text.split("|");
		ArrayList<String> txt = new ArrayList<String>();
		for(String s : textTmp){
			txt.add(s);
		}
		return txt;
	}

	public User getAuthor() {
		return author;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getIntro() {
		return intro;
	}

	public String getCorpus() {
		return corpus;
	}

	public String getConclusion() {
		return conclusion;
	}

	public boolean Equals(Text t) {
		if(t.getId() == this.getId())
			return true;
		return false;
	}
}
