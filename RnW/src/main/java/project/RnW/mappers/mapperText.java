package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.model.Comment;
import project.RnW.model.Database;
import project.RnW.model.Text;
import project.RnW.model.User;

public class mapperText {
	
	public static ObjectId insert(String title, 
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion,
			boolean isPrivate,
			User author) 
	throws MongoWriteException{
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
	
	public static boolean update(ObjectId id,
			ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion) {
		UpdateResult ur = Database.texts.updateOne(eq("_id", id), 
				combine(set("intro", intro),
				set("corpus", corpus),
				set("conclusion", conclusion)));		
		return ur.wasAcknowledged();
	}
	
	public static boolean delete(ObjectId id) {
		DeleteResult dr = Database.texts.deleteOne(eq("_id", id));
		boolean deleteReports = mapperReport.deleteReportsOfText(id);
		return dr.getDeletedCount() > 0 && deleteReports;
	}
	
	public static Text getText(ObjectId id2) {
		Document doc = Database.texts.find(eq("_id", id2)).first();
		
		return mapText(doc);
	}

	
	public static boolean changeVisibility(ObjectId id, boolean isPrivate) {
		boolean updateVisibility = Database.texts.updateOne(eq("_id", id), 
				set("isPrivate",isPrivate)).getModifiedCount() > 0;
		return updateVisibility;
	}

	

	public static ArrayList<Text> getAllVisibleTexts() {
		FindIterable<Document> textIt = Database.texts.find(
				eq("isPrivate", false));
		ArrayList<Text> textList = new ArrayList<Text>();
		for(Document doc : textIt) {
			textList.add(mapText(doc));
		}
		return textList;
	}
	
	private static Text mapText(Document doc) {
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
		
		return new Text(
				id,
				title, 
				intro, 
				corpus, 
				conc, 
				comments, 
				isPrivate, 
				mapperUser.getUser(userId));
	}

	public static ArrayList<Text> getAllTextsFromAuthor(ObjectId id) {
		FindIterable<Document> textIt = Database.texts.find(eq("userId", id));
		ArrayList<Text> textList = new ArrayList<Text>();
		for(Document doc : textIt) {
			textList.add(mapText(doc));
		}
		return textList;
	}
}
