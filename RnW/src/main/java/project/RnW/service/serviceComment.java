package project.RnW.service;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;

import project.RnW.service.serviceUser.InvalidIdException;
import project.RnW.mappers.mapperComment;
import project.RnW.mappers.mapperComment.CommentNotFoundException;
import project.RnW.model.Comment;

public class serviceComment {
	
	//This class handles business logic for Comment objects.
	
	//given a text id and a comment id, this method returns the commenter's id
	public static ObjectId returnOwnerId(ObjectId textId, ObjectId commentId) throws AccountNotFoundException, MongoException, CommentNotFoundException {
		return mapperComment.returnOwnerId(textId, commentId);
	}
	public static String returnOwnerId(String textId, String commentId) throws AccountNotFoundException, MongoException, CommentNotFoundException, InvalidIdException {
		try {
			return mapperComment.returnOwnerId(new ObjectId(textId),
					new ObjectId(commentId)).toString();
		} catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}
	
	public static Comment getComment(ObjectId textId, ObjectId commentId) throws AccountNotFoundException, MongoException, CommentNotFoundException {
			return mapperComment.getComment(textId, commentId);
	}
	
	public static Comment getComment(String textId, String commentId) throws AccountNotFoundException, MongoException, CommentNotFoundException, InvalidIdException {
		try {
			return mapperComment.getComment(new ObjectId(textId), new ObjectId(commentId));
		} catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
		
	}
	
	public static ObjectId saveComment(ObjectId userId, ObjectId textId, String content) throws CommentUnsavedException {
		ObjectId commId = mapperComment.insert(userId, textId, content);
		if(commId == null)
			throw new CommentUnsavedException("Cannot save comment");
		return commId;
	}
	
	public static String saveComment(String userId, String textId, String  content) 
	throws MongoException, CommentUnsavedException, InvalidIdException{
		try {
			return saveComment(new ObjectId(userId), new ObjectId(textId), content).toString();
		} catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}
	
	public static void deleteComment(ObjectId textId, Comment c) 
			throws MongoException, CommentDeletionException{
		if(!mapperComment.delete(c.getId(), textId)) {
			throw new CommentDeletionException("Comment was not deleted");
		}
	}

	public static void deleteComment(String textId, Comment c) 
			throws MongoException, CommentDeletionException, InvalidIdException{
		try {
			deleteComment(new ObjectId(textId), c);
		} catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}
	
	
	public static class CommentUnsavedException extends Exception{
		public CommentUnsavedException(String msg) {
			super(msg);
		}
	}
	
	public static class CommentDeletionException extends Exception{
		public CommentDeletionException(String msg) {
			super(msg);
		}
	}
	
}
