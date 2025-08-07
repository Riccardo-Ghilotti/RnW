package project.RnW.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import project.RnW.db.Database;
import project.RnW.mappers.mapperComment.CommentNotFoundException;
import project.RnW.model.Comment;
import project.RnW.model.User;

public class mapperCommentTest {

	private ObjectId commId;
	private ObjectId userId;
	private ObjectId textId;
	private String content;
	
	@BeforeEach
	public void setup() {
		commId = new ObjectId();
		userId = new ObjectId();
		textId = new ObjectId();
		content = "Test";
	}
	
	
	@Test
	public  void testInsertCommentTest() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        
        Database.texts = mockCollection;

        ObjectId result = mapperComment.insert(userId, textId, content);
        assertNotNull(result);
        
	}
	
	
	//this method tests both getComment and mapComment
	@Test
	public void testGetComment() throws AccountNotFoundException, MongoException, CommentNotFoundException {
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {

		ArrayList<Document> commList = new ArrayList<Document>();
		
		Document docComment = new Document("_id", commId)
						.append("u_id", userId)
						.append("content", content);
		
		commList.add(docComment);
		
		Document docText = new Document("_id", textId)
                .append("title", "titolo")
                .append("intro", new ArrayList<String>())
                .append("corpus", new ArrayList<String>())
                .append("conclusion", new ArrayList<String>())
                .append("userId", new ObjectId())
                .append("isPrivate", false)
                .append("comments", commList);
		
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockResult = mock(FindIterable.class);
        
        when(mockResult.first()).thenReturn(docText);
        when(mockCollection.find(eq("_id", textId))).thenReturn(mockResult);
        utilities.when(() -> mapperUser.getUser(userId))
        .thenReturn(new User(userId, "Test", false));
        
        Database.texts = mockCollection;
        Comment comm = mapperComment.getComment(textId, commId);
        
        assertEquals(commId, comm.getId());
        assertEquals(userId, comm.getUser().getId());
        assertEquals(content, comm.getContent());

        }
	}
	
	@Test
	public void testGetCommentExceptionNoText() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockResult = mock(FindIterable.class);
        
        when(mockResult.first()).thenReturn(null);
        when(mockCollection.find(eq("_id", textId))).thenReturn(mockResult);
        
        Database.texts = mockCollection;
        
        assertThrows(NoSuchElementException.class, 
        		() -> mapperComment.getComment(textId, commId));		
	}
	
	@Test
	public void testGetCommentExceptionNoComment() {
		Document docText = new Document("_id", textId)
                .append("title", "titolo")
                .append("intro", new ArrayList<String>())
                .append("corpus", new ArrayList<String>())
                .append("conclusion", new ArrayList<String>())
                .append("userId", new ObjectId())
                .append("isPrivate", false)
                .append("comments", new ArrayList<Document>());
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockResult = mock(FindIterable.class);
        
        when(mockResult.first()).thenReturn(docText);
        when(mockCollection.find(eq("_id", textId))).thenReturn(mockResult);
        
        Database.texts = mockCollection;
        
        assertThrows(CommentNotFoundException.class, 
        		() -> mapperComment.getComment(textId, commId));		
	}
	
	@Test
	public void testDeleteComment() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
		UpdateResult mockResultDeleteC = mock(UpdateResult.class);
        
    	when(mockResultDeleteC.getModifiedCount()).thenReturn(1L);
    	when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).
    	thenReturn(mockResultDeleteC);
            
        Database.texts = mockCollection;
        
       
        
        boolean deleteComment = mapperComment.delete(commId, textId);
        verify(mockCollection).updateOne(any(Bson.class), any(Bson.class));
        assertEquals(deleteComment, true);

	}
}