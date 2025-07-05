package project.RnW.service;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;

import project.RnW.mappers.mapperComment;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;

public class serviceComment {
	
	
	public static Comment createNewComment(User user, Text text, String content) 
		throws MongoException{
		ObjectId id_temp = mapperComment.insert(user.getId(), text.getId(), content);
		
		Comment c = null;
		c = new Comment(id_temp, user, content);
	
		if(c.getId() == null) {
			throw new MongoException("Impossibile salvare il commento");
		}
		return c;
	}
	
	public static ObjectId returnOwnerId(ObjectId textId, ObjectId commentId) {
		return mapperComment.returnOwnerId(textId, commentId);
	}
	public static String returnOwnerId(String textId, String commentId) {
		return mapperComment.returnOwnerId(new ObjectId(textId),
				new ObjectId(commentId)).toString();
	}
	
	public static Comment getComment(ObjectId textId, ObjectId commentId) {
		return mapperComment.getComment(textId, commentId);
	}
	
	public static Comment getComment(String textId, String commentId) {
		return mapperComment.getComment(new ObjectId(textId), new ObjectId(commentId));
	}
	
	public static boolean saveComment(ObjectId userId, ObjectId textId, String content) {
		return mapperComment.insert(userId, textId, content) != null;
	}
	
	public static boolean saveComment(String userId, String textId, String  content) {
		return saveComment(new ObjectId(userId), new ObjectId(textId), content);
	}
	
	public static void deleteComment(ObjectId textId, Comment c) 
			throws MongoException{
		
		if(!mapperComment.delete(c.getId(), textId)) {
			throw new MongoException("Impossibile cancellare il commento");
		};
	}

	public static void deleteComment(String textId, Comment c) 
			throws MongoException{
		deleteComment(new ObjectId(textId), c);
	}
}
