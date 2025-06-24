package project.RnW.model;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import java.sql.Statement;
import project.RnW.model.Database;

public class User {
	
	private static ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");
	private String mail;
	private ObjectId id;
	private String name;
	private boolean admin;
	
	public User(String mail, String name, String pwd, boolean admin) {
		ObjectId id_temp = Database.insert(mail, name, pwd, admin);
		if(id_temp != null) {
			this.id= id_temp;
			this.mail = mail;
			this.name = name;
			this.admin = admin;
		}
		else {
			this.id = null;
			System.err.println("Errore nella creazione dell'utente");
		}
	}
	
	public User(ObjectId id, String mail, String name, boolean admin) {
		this.id = id;
		this.mail = mail;
		this.name = name;
		this.admin = admin;
	}

	public void changeName(String name) {
		try {
			Database.update(id, name, null);
			this.name=name;
		}catch(Exception e) {
			System.out.println("ERROR: " + e);//TODO: fix exception
		}
	}

	public void changePassword(String pwd) {
		pwd = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
		try {
			Database.update(id, null, pwd);
		}catch(Exception e) {
			System.out.println("ERROR: e");//TODO: fix exception
		}
	}

	public ObjectId getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	

	public boolean isAdmin() {
		return admin;
	}
	
	public static User getUser(ObjectId id2) {
		Document docUser = Database.getUser(id2);
		if(docUser == null)
			return null;
		ObjectId id = docUser.getObjectId("_id");
	    String mail = docUser.getString("mail");
	    String name = docUser.getString("name");
	    boolean admin = docUser.getBoolean("admin", false);
	    
	    return new User(id, mail, name, admin);
	}
	
	public static User getUser(String id) {
		ObjectId idObj = null;
		if(ObjectId.isValid(id))
			idObj = new ObjectId(id);
		return getUser(idObj);
	}
	
	public boolean equals(User u) {
		if(u.getId().equals(this.getId())) 
			return true;
		return false;
	}
	
	public static User login(String mail, String password) {
		Document docUser = Database.getUser(mail, password);
		if(docUser == null)
			return null;
		ObjectId id = docUser.getObjectId("_id");
	    String name = docUser.getString("name");
	    boolean admin = docUser.getBoolean("admin", false);
	    
	    return new User(id, mail, name, admin);
	}
	
	public boolean delete() {
		return Database.deleteUser(id);
	}
	
}

