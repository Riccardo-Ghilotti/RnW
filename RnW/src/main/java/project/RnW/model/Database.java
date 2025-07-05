package project.RnW.model;


import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



//this class collects a reference for every collection in the database.
//these references will be used in the mapper classes to interact with the db.
public class Database {
	
	public static MongoClient client;
	public static MongoDatabase db;
	public static MongoCollection<Document> users;
	public static MongoCollection<Document> texts;
	public static MongoCollection<Document> reports;
	
	static {
        try {
            client = MongoClients.create("mongodb://localhost:27017");
            db = client.getDatabase("RnW");
            users = db.getCollection("Users");
            texts = db.getCollection("Texts");
            reports = db.getCollection("Reports");
        } catch(MongoException me) {
            System.err.println("Connection to the database server error:" + me);
        }
    }

}
