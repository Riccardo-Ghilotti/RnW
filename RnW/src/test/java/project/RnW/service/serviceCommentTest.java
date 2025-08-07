package project.RnW.service;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import project.RnW.mappers.mapperComment;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceComment.CommentDeletionException;
import project.RnW.service.serviceComment.CommentUnsavedException;

public class serviceCommentTest {
	
	private Text text;
	private User commenter;
	private String content;
	private Comment comment;
	
	@BeforeEach
	public void setup() {
		text = new Text(new ObjectId(),
						"Test",
						new ArrayList<String>(),
						new ArrayList<String>(),
						new ArrayList<String>(),
						new ArrayList<Comment>(),
						false,
						new User(new ObjectId(),
								"test",
								false));
		content = "Test comment";
		comment = new Comment(new ObjectId(),
							commenter,
							content);
		commenter = new User(new ObjectId(),
				"test",
				false);
	}
	
	//this class tests only for exceptions since 
	//that is the only real logic present in the serviceComment Class
	
	
	@Test
	public void testSaveCommentEsception() {
		try (MockedStatic<mapperComment> utilities = Mockito.mockStatic(mapperComment.class)) {
			utilities.when(() -> mapperComment.insert(
					commenter.getId(), text.getId(), content))
			.thenReturn(null);
			
			assertThrows(CommentUnsavedException.class, () -> 
			serviceComment.saveComment(text.getId(), commenter.getId(), content));
		}
	}
	
	@Test
	public void testDeleteCommentException() {
		try (MockedStatic<mapperComment> utilities = Mockito.mockStatic(mapperComment.class)) {
			utilities.when(() -> mapperComment.delete(
					commenter.getId(), text.getId()))
			.thenReturn(false);
			
			assertThrows(CommentDeletionException.class, () -> 
			serviceComment.deleteComment(text.getId(), comment));
		}
	}
	
	
}
