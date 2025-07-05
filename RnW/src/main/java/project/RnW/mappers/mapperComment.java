package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import project.RnW.model.Comment;
import project.RnW.model.Database;
import project.RnW.model.User;

public class mapperComment {
	
	public static ObjectId insert(ObjectId u_id, ObjectId t_id, String content) {
		Document newComment = new Document("_id", new ObjectId())
				.append("u_id", u_id)
				.append("content", content);
		Database.texts.updateOne(eq("_id", t_id), push("comments", newComment));
		return newComment.getObjectId("_id");
	}
	
	public static ObjectId returnOwnerId(ObjectId textId, ObjectId commentId) {
		Comment comment = getComment(textId, commentId);
	    ObjectId ownerId = comment.getUser().getId();
	    return ownerId;
	}
	
	public static Comment getComment(ObjectId textId, ObjectId commentId) {
		Document resultText = Database.texts.find(eq("_id", textId)).first();
		Document docComment = null;
		ArrayList<Document> resultComments = resultText.get("comments", 
				ArrayList.class);
		for(Document doc : resultComments) {
			if(doc.getObjectId("_id").equals(commentId))
				docComment =  doc;
		}
		if(docComment == null)
			return null;
		
		return mapComment(docComment);
	}
	
	public static Comment mapComment(Document docComment) {
		ObjectId id = docComment.getObjectId("_id");
	    User user = mapperUser.getUser(docComment.getObjectId("u_id"));
	    String comment = docComment.getString("content");
	    
	    return new Comment(id, user, comment);
	}
	
	public static boolean delete(ObjectId commentId, ObjectId textId) {
		return Database.texts.updateOne(eq("_id", textId), pull("comments",
				eq("_id", commentId))).getMatchedCount() > 0;
	}
}
