package project.RnW.mappers;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.common.hash.Hashing;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.model.Database;
import project.RnW.model.User;

public class mapperUser {
	
	public static ObjectId insert(String mail, 
			String username, String pwd, boolean admin) 
					throws MongoWriteException{
		Document newUser = new Document("name", username);
		newUser.append("mail", mail);
		newUser.append("password", 
				Hashing.sha256()
				.hashString(pwd, StandardCharsets.UTF_8).toString());
		newUser.append("admin", admin);
		InsertOneResult result = Database.users.insertOne(newUser);
		return result.getInsertedId().asObjectId().getValue();
	}

	public static boolean update(ObjectId id, String name, String pwd) {
		UpdateResult ur = null;
		if(pwd == null) {
			ur = Database.users.updateOne(eq("_id", id), set("name", name));
		}
		else
		{
			pwd = Hashing.sha256().
					hashString(pwd, StandardCharsets.UTF_8).toString();
			ur = Database.users.updateOne(eq("_id", id), set("password", pwd));
		}
		return ur.getModifiedCount() > 0;
	}

	public static boolean delete(ObjectId id) {
		DeleteResult dr = Database.users.deleteOne(eq("_id", id));
		System.out.println(dr.toString());
		return dr.wasAcknowledged();
	}

	public static User getUser(ObjectId id2) {
				Document docUser = Database.users.find(eq("_id",id2)).first();
				return mapUser(docUser);
			}
	
	
	public static Document getUser(String mail, String pwd) 
			 throws IllegalArgumentException {
		Document checkUser = Database.users.find(eq("mail", mail)).first();
		if(checkUser == null)
			throw new IllegalArgumentException("Utente non trovato");
		Document result = Database.users.find(and(eq("mail", mail), eq("password", pwd)))
				.first();
		if(result.isEmpty())
			throw new IllegalArgumentException("Credenziali sbagliate");
		return result;
	}
	
	
	public static boolean checkPassword(ObjectId id, String pwd) {
		Document result = Database.users.find(eq("_id", id)).first();
		pwd = Hashing.sha256()
				.hashString(pwd, StandardCharsets.UTF_8).toString();
		if(result.getString("password").equals(pwd))
			return true;
		return false;
	}

	public static ArrayList<User> getAllUsers() {
		FindIterable<Document> result = Database.users.find();
		ArrayList<User> userList = new ArrayList<User>();
		for(Document doc : result) {
			userList.add(mapUser(doc));
		}
		return userList;
	}
	
	public static User login(String mail, String password)
			throws IllegalArgumentException{
		Document docUser = mapperUser.getUser(mail, password);
		return mapUser(docUser);
	}

	
	public static User mapUser(Document docUser) {
		if(docUser == null)
			return null;
		ObjectId id = docUser.getObjectId("_id");
	    String name = docUser.getString("name");
	    boolean admin = docUser.getBoolean("admin", false);
	    
	    return new User(id, name, admin);
	}
}

