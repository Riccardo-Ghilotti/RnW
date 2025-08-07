package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.db.Database;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;

public class mapperText {
	
	// this class handles database operations for texts, 
	// including storage, retrieval, and deletion.
	// Also maps database documents to Text objects.
	
	
	//this method stores a text document into the database.
	public static ObjectId insert(String title, 
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion,
			boolean isPrivate,
			User author) 
	throws MongoException{
		Document newText = new Document("title", title)
				.append("intro", intro)
				.append("corpus", corpus)
				.append("conclusion", conclusion)
				.append("userId", author.getId())
				.append("isPrivate", isPrivate)
				.append("comments", new ArrayList<String>());
		InsertOneResult result = Database.texts.insertOne(newText);
		return result.getInsertedId().asObjectId().getValue();
	}
	
	
	//this method updates a text document that's already in the database.
	public static boolean update(ObjectId id,
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion) 
	throws MongoException{
		UpdateResult ur = Database.texts.updateOne(eq("_id", id), 
				combine(set("intro", intro),
				set("corpus", corpus),
				set("conclusion", conclusion)));
		return ur.getMatchedCount() > 0; 
		//in this case the method returns the number of matched documents to
		//avoid errors in case the user just pressed the wrong button.
		//Errors can happen if the user just sends the unchanged version of the
		//text since getModifiedCount returns 0 if the content(s) of the old field(s)
		//is(are) identical to the new content.
	}
	
	
	//this method deletes a text document from the database.
	public static boolean delete(ObjectId id) 
	throws MongoException{
		boolean deleteReports = false;
		deleteReports = mapperReport.deleteReportsOfText(id);
		DeleteResult dr = Database.texts.deleteOne(eq("_id", id));
		return dr.getDeletedCount() > 0 && deleteReports;
	}
	
	
	//recovers and maps the Text from the database
	public static Text getText(ObjectId id2) 
		throws MongoException, AccountNotFoundException{
		Document doc = Database.texts.find(eq("_id", id2)).first();
		
		return mapText(doc);
	}

	//changes visibility field of a text document in the database
	public static boolean changeVisibility(ObjectId id, boolean isPrivate) 
	throws MongoException{
		boolean updateVisibility = Database.texts.updateOne(eq("_id", id), 
				set("isPrivate",isPrivate)).getModifiedCount() > 0;
		return updateVisibility;
	}

	
	//returns all Texts whose isPrivate field is set to false in the database.
	//after retrieving the documents, it maps them to Text objects
	public static ArrayList<Text> getAllVisibleTexts() throws MongoException {
		FindIterable<Document> textIt = Database.texts.find(
				eq("isPrivate", false));
		ArrayList<Text> textList = new ArrayList<Text>();
		for(Document doc : textIt) {
			textList.add(mapText(doc));
		}
		return textList;
	}
	
	
	//maps the document given to a Text object
	private static Text mapText(Document doc) 
		throws MongoException{
		if(doc == null)
			throw new NoSuchElementException("Text not found");
		ObjectId id = doc.getObjectId("_id");
		String title = doc.getString("title");
		ArrayList<String> intro = doc.get("intro", ArrayList.class);
		ArrayList<String> corpus = doc.get("corpus", ArrayList.class);
		ArrayList<String> conc = doc.get("conclusion", ArrayList.class);
		ArrayList<Document> commentsDoc = doc.get("comments", ArrayList.class);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for(Document docTemp : commentsDoc) {
			comments.add(mapperComment.mapComment(docTemp));
		}
		
		boolean isPrivate = doc.getBoolean("isPrivate");	
		ObjectId userId = doc.getObjectId("userId");
		try {
			return new Text(
					id,
					title, 
					intro, 
					corpus, 
					conc, 
					comments, 
					isPrivate, 
					mapperUser.getUser(userId));

		}catch(AccountNotFoundException e) {
			throw new MongoException("Text has non-valid user as author");
		}
		
		}

	//returns every text that has been created by a certain user
	public static ArrayList<Text> getAllTextsFromAuthor(ObjectId id, boolean isOwner) throws  MongoException {
		FindIterable<Document> textIt = null;
		if(isOwner)
			textIt = Database.texts.find(eq("userId", id));
		else
			textIt = Database.texts.find(and(eq("userId", id), eq("isPrivate", false)));
		ArrayList<Text> textList = new ArrayList<Text>();
		for(Document doc : textIt) {
			textList.add(mapText(doc));
		}
		return textList;
	}
}
