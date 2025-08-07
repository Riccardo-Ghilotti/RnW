package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;

import project.RnW.db.Database;
import project.RnW.model.Comment;
import project.RnW.model.User;

public class mapperComment {
	
	// this class handles database operations for text comments, 
	// including storing, retrieving, and deleting.
	// Also maps database documents to Comment objects.

	
	//formats and inserts a comment document into the database.
	public static ObjectId insert(ObjectId u_id, ObjectId t_id, String content) {
		Document newComment = new Document("_id", new ObjectId())
				.append("u_id", u_id)
				.append("content", content);
		Database.texts.updateOne(eq("_id", t_id), push("comments", newComment));
		return newComment.getObjectId("_id");
	}
	
	
	//given text and comment ids, returns the id of the creator of the comment.
	public static ObjectId returnOwnerId(ObjectId textId, ObjectId commentId) 
		throws MongoException, AccountNotFoundException, CommentNotFoundException{
		Comment comment = getComment(textId, commentId);
	    ObjectId ownerId = comment.getUser().getId();
	    return ownerId;
	}
	
	//given text and comment ids, looks for a document and maps it to a Comment object.
	public static Comment getComment(ObjectId textId, ObjectId commentId) 
	throws MongoException, AccountNotFoundException, CommentNotFoundException{
		Document resultText = Database.texts.find(eq("_id", textId)).first();
		if(resultText == null)
			throw new NoSuchElementException("Cannot find text");
		Document docComment = null;
		ArrayList<Document> resultComments = resultText.get("comments", 
				ArrayList.class);
		for(Document doc : resultComments) {
			if(doc.getObjectId("_id").equals(commentId))
				docComment = doc;
		}
		if(docComment == null)
			throw new CommentNotFoundException("Cannot find comment");
			
		return mapComment(docComment);
	}
	
	//maps a document into a Comment object.
	protected static Comment mapComment(Document docComment) 
	throws MongoException{
		ObjectId id = docComment.getObjectId("_id");
		User user = null;
		try {
	    user = mapperUser.getUser(docComment.getObjectId("u_id"));
		}catch(AccountNotFoundException e) {
			throw new MongoException("Comment has non-valid user as commenter");
		}
	    String comment = docComment.getString("content");
	    
	    return new Comment(id, user, comment);
	}
	
	
	//given comment and text id, deletes a comment document from the database.
	public static boolean delete(ObjectId commentId, ObjectId textId) 
	throws MongoException{
		return Database.texts.updateOne(eq("_id", textId), pull("comments",
				eq("_id", commentId))).getModifiedCount() > 0;
	}
	
	public static class CommentNotFoundException extends Exception{
		public CommentNotFoundException(String msg) {
			super(msg);
		}
	}
}
