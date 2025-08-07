package project.RnW.mappers;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.db.Database;
import project.RnW.model.User;

public class mapperUser {
	
	public static ObjectId insert(String mail, 
			String username, String pwd, boolean admin) 
					throws MongoException{
		Document newUser = new Document("name", username);
		newUser.append("mail", mail);
		newUser.append("password", pwd);
		newUser.append("admin", admin);
		InsertOneResult result = Database.users.insertOne(newUser);
		return result.getInsertedId().asObjectId().getValue();
	}

	//updates a document, changing the "name" or "password" field
	public static boolean update(ObjectId id, String name, String pwd) {
		UpdateResult ur = null;
		if(pwd == null) {
			ur = Database.users.updateOne(eq("_id", id), set("name", name));
		}
		else
		{
			ur = Database.users.updateOne(eq("_id", id), set("password", pwd));
		}
		return ur.getModifiedCount() > 0;
	}

	public static boolean delete(ObjectId id) {
		DeleteResult dr = Database.users.deleteOne(eq("_id", id));
		return dr.getDeletedCount() > 0;
	}

	//recovers and maps the User from the database
	public static User getUser(ObjectId id2) 
			throws MongoException, AccountNotFoundException{
				Document docUser = Database.users.find(eq("_id",id2)).first();
				return mapUser(docUser);
			}
	
	
	//recovers the document using the user's credentials
	public static Document getUser(String mail, String pwd) 
			 throws IllegalArgumentException{
		Document checkUser = Database.users.find(eq("mail", mail)).first();
		if(checkUser == null)
			return checkUser; //mapUser will throw an exception if the user document is null
		Document result = Database.users.find(and(eq("mail", mail),	eq("password", pwd)))
				.first();
		if(result == null || result.isEmpty())
			throw new IllegalArgumentException("There's no correspondance in the database");
		return result;
	}
	
	//checks if the password given is equal to the one that's present in the db
	public static String returnPassword(ObjectId id) {
		Document result = Database.users.find(eq("_id", id)).first();
		return result.getString("password");
	}

	public static ArrayList<User> getAllUsers() 
		throws MongoException {
		FindIterable<Document> result = Database.users.find();
		ArrayList<User> userList = new ArrayList<User>();
		for(Document doc : result) {
			try {
			userList.add(mapUser(doc));
			}catch(AccountNotFoundException e) {
				//Since the code scans for all the users in the db, if one 
				//User can't be mapped, than it has been saved incorrectly or
				//the database has encountered an error.
				throw new MongoException("Couldn't map a user");
			}
		}
		return userList;
	}
	
	//searches for the document that has matching criteria,
	//then maps it to a User object
	public static User getAndMapUser(String mail, String password)
			throws IllegalArgumentException, MongoException, AccountNotFoundException{
		Document docUser = mapperUser.getUser(mail, password);
		return mapUser(docUser);
	}

	//maps the document given to a User object
	private static User mapUser(Document docUser) 
	throws MongoException, AccountNotFoundException{
		if(docUser == null)
			throw new AccountNotFoundException("No user was found");
		ObjectId id = docUser.getObjectId("_id");
	    String name = docUser.getString("name");
	    boolean admin = docUser.getBoolean("admin", false);
	    
	    return new User(id, name, admin);
	}
}

