package project.RnW.model;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;

public class Database {
	
	static MongoClient client;
	static MongoDatabase db;
	static MongoCollection<Document> users;
	static MongoCollection<Document> texts;
	static MongoCollection<Document> comments;
	
	static {
        try {
            client = MongoClients.create("mongodb://localhost:27017");
            db = client.getDatabase("RnW");
            users = db.getCollection("Users");
            texts = db.getCollection("Texts");
            comments = db.getCollection("Comments");
        } catch(MongoException me) {
            System.err.println("Connection to the database server error:" + me);
        }
    }
    
    private Database() {}
	
	
	public static ObjectId insert(String mail, 
			String username, String pwd, boolean admin) {
		Document newUser = new Document("name", username);
		newUser.append("mail", mail);
		newUser.append("password", 
				Hashing.sha256()
				.hashString(pwd, StandardCharsets.UTF_8).toString());
		newUser.append("admin", admin);
		
		InsertOneResult result = users.insertOne(newUser);
		if(result.wasAcknowledged())
			return result.getInsertedId().asObjectId().getValue();
		else {
			return null;
		}
	}
	
	public static boolean update(ObjectId id, String name, String pwd) {
		UpdateResult ur = null;
		if(pwd == null) {
			ur = users.updateOne(eq("_id", id), set("name", name));
		}
		else
		{
			ur = users.updateOne(eq("_id", id), set("password", pwd));
		}
		return ur.wasAcknowledged();
	}
	
	public static boolean deleteUser(ObjectId id) {
		DeleteResult dr = users.deleteOne(eq("_id", id));
		System.out.println(dr.toString());
		return dr.wasAcknowledged();
	}
	
	public static Document getUser(String mail, String pwd) {
		Document result = users.find(and(eq("mail", mail), eq("password", pwd)))
				.first();
		return result;
	}
	
	public static Document getUser(ObjectId id) {
		Document result = users.find(eq("_id",id)).first();
		return result;
	}


	public static ObjectId insert(String title, 
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion, 
			User author) {
		Document newText = new Document("title", title)
				.append("intro", intro)
				.append("corpus", corpus)
				.append("conclusion", conclusion)
				.append("userId", author.getId());
		InsertOneResult result = texts.insertOne(newText);
		return result.getInsertedId().asObjectId().getValue();
	}
	
	public static boolean update(ObjectId id,
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion) {
		UpdateResult ur = texts.updateOne(eq("_id", id), 
				combine(set("intro", intro),
				set("corpus", corpus),
				set("conclusion", conclusion)));		
		return ur.wasAcknowledged();
	}
	
	public static boolean deleteText(ObjectId id) {
		DeleteResult dr = texts.deleteOne(eq("_id", id));
		return dr.wasAcknowledged();
	}
	public static FindIterable<Document> getAllTextsFromAuthor(ObjectId objectId){
		FindIterable<Document> textIt = texts.find(eq("userId", objectId));
		return textIt;
	}
	
	public static Document getText(ObjectId id) {
		Document result = texts.find(eq("_id", id)).first();
		return result;
	}
}
