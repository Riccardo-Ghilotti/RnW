package project.RnW.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.db.Database;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;

public class mapperTextTest {
	
	private ObjectId textId;
    private String title;
	private ArrayList<String> intro;
	private ArrayList<String> corpus;
	private ArrayList<String> conc;
    private ArrayList<Comment> comments;
    private boolean isPrivate;
    private User author;

    @BeforeEach
    public void setup() {
        textId = new ObjectId();
        title = "Example";
        intro = new ArrayList<String>();
        intro.add("Introduction");
        corpus = new ArrayList<String>();
        corpus.add("Body");
        conc = new ArrayList<String>();
        conc.add("Conclusion");
        isPrivate = false;
        author = new User(new ObjectId(),"nome",false);
        comments = new ArrayList<Comment>();
        comments.add(new Comment(new ObjectId(), author, "Test"));
    }
    
    
    
    @Test
    public void testTextInsert() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        InsertOneResult mockResult = mock(InsertOneResult.class);
        
        when(mockResult.getInsertedId()).thenReturn(new BsonObjectId(textId));
        when(mockCollection.insertOne(any(Document.class)))
        .thenReturn(mockResult);

        Database.texts = mockCollection;

        ObjectId result = mapperText.insert(
        		title, intro, corpus, conc, isPrivate, author);
        assertEquals(textId, result);
    }
    
    @Test
    public void testTextUpdate() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        UpdateResult mockResult = mock(UpdateResult.class);
        
        when(mockResult.getMatchedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq("_id", textId), 
        		combine(set("intro", intro),
				set("corpus", corpus),
				set("conclusion", conc)))).thenReturn(mockResult);
        
        Database.texts = mockCollection;
        
        boolean result = mapperText.update(textId, intro, corpus, conc);
        assertEquals(true, result);
    }
    
    @Test
    public void testTextDelete() {
    	
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        DeleteResult mockResultDeleteT = mock(DeleteResult.class);
        
        try (MockedStatic<mapperReport> utilities = Mockito.mockStatic(mapperReport.class)) {
        	when(mockResultDeleteT.getDeletedCount()).thenReturn(1L);
        	utilities.when(() -> mapperReport.deleteReportsOfText(textId))
            .thenReturn(true);
        	when(mockCollection.deleteOne(eq("_id", textId)))
        	.thenReturn(mockResultDeleteT);
            
            Database.texts = mockCollection;
            
            boolean deleteText = mapperText.delete(textId);
            assertEquals(true, deleteText);
        }
    }
    
    
    //this method also tests mapText
    @Test
    public void testGetText() throws AccountNotFoundException, MongoException {
    	Text t = new Text(
    			textId, title, intro, corpus, conc, comments, isPrivate, author);
    	
    	ArrayList<Document> comms = new ArrayList<Document>();
    	Document comm = new Document("_id", comments.get(0).getId())
				.append("u_id", author.getId())
				.append("content", "Test");
    	comms.add(comm);
    	
    	Document textDoc = new Document("_id", textId)
                .append("title", title)
                .append("intro", intro)
                .append("corpus", corpus)
                .append("conclusion", conc)
                .append("userId", author.getId())
                .append("isPrivate", isPrivate)
                .append("comments", comms);
    	
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	FindIterable<Document> mockResult = mock(FindIterable.class);
    	try (MockedStatic<mapperComment> utilitiesComm = Mockito.mockStatic(mapperComment.class)
    			; MockedStatic<mapperUser> utilitiesUser = Mockito.mockStatic(mapperUser.class)) {
	        when(mockResult.first()).thenReturn(textDoc);
	        when(mockCollection.find(Filters.eq("_id", textId)))
	        .thenReturn(mockResult);
	        utilitiesComm.when(() -> mapperComment.mapComment(comm))
	        .thenReturn(comments.get(0));
	        when(mockCollection.find(Filters.eq("_id", textId)))
	        .thenReturn(mockResult);
	        utilitiesUser.when(() -> mapperUser.getUser(author.getId()))
	        .thenReturn(author);
	
	        Database.texts = mockCollection;
	
	        
	        Text result = mapperText.getText(textId);
	        assertEquals(t.getId(), result.getId());
	        assertEquals(t.getTitle(), result.getTitle());
	        assertEquals(t.getAuthor().getId(), result.getAuthor().getId());
    	}
    }
    
    @Test
    public void testGetTextException() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	FindIterable<Document> mockResult = mock(FindIterable.class);
    	try (MockedStatic<mapperComment> utilitiesComm = Mockito.mockStatic(mapperComment.class)
    			; MockedStatic<mapperUser> utilitiesUser = Mockito.mockStatic(mapperUser.class)) {
	        when(mockResult.first()).thenReturn(null);
	        when(mockCollection.find(Filters.eq("_id", textId)))
	        .thenReturn(mockResult);
	
	        Database.texts = mockCollection;
	
	        assertThrows(NoSuchElementException.class, () -> mapperText.getText(textId));
	        
    	}
    }
    
    @Test
    public void testChangeVisibility() {
        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        UpdateResult mockResult = mock(UpdateResult.class);
        
        when(mockResult.getModifiedCount()).thenReturn(1L);
        when(mockCollection.updateOne(eq("_id", textId), set("isPrivate", true)))
        .thenReturn(mockResult);

        Database.texts = mockCollection;

        boolean result = mapperText.changeVisibility(textId, true);
        assertTrue(result);
    }
    
    @Test
    public void testGetAllVisibleTexts() {
    	Document textDoc = new Document("_id", textId)
                .append("title", title)
                .append("intro", intro)
                .append("corpus", corpus)
                .append("conclusion", conc)
                .append("userId", author.getId())
                .append("isPrivate", isPrivate)
                .append("comments", new ArrayList<String>());

        FindIterable<Document> mockIterable = mock(FindIterable.class);
        MongoCursor<Document> mockedCursor = mock(MongoCursor.class);
        when(mockIterable.iterator()).thenReturn(mockedCursor);
        when(mockedCursor.hasNext()).thenReturn(true, false);
        when(mockedCursor.next()).thenReturn(textDoc);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        when(mockCollection.find(eq("isPrivate", false))).thenReturn(mockIterable);
        Database.texts = mockCollection;
        
        //no need to mock mapperComment since the list is empty
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {

        utilities.when(() -> mapperUser.getUser(author.getId()))
        .thenReturn(author);

        ArrayList<Text> result = mapperText.getAllVisibleTexts();
        assertEquals(1, result.size());
        }
    }
    
    @Test
    public void testGetAllTextsFromAuthor() {
    	Document textDoc = new Document("_id", textId)
                .append("title", title)
                .append("intro", intro)
                .append("corpus", corpus)
                .append("conclusion", conc)
                .append("userId", author.getId())
                .append("isPrivate", isPrivate)
                .append("comments", new ArrayList<String>());

        FindIterable<Document> mockIterable = mock(FindIterable.class);
        MongoCursor<Document> mockedCursor = mock(MongoCursor.class);
        
        when(mockIterable.iterator()).thenReturn(mockedCursor);
        when(mockedCursor.hasNext()).thenReturn(true, false);
        when(mockedCursor.next()).thenReturn(textDoc);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        when(mockCollection.find(
        		eq("userId", author.getId()))).thenReturn(mockIterable);
        Database.texts = mockCollection;

        //no need to mock mapperComment since the list is empty
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {

	        utilities.when(() -> mapperUser.getUser(author.getId()))
	        .thenReturn(author);
	
	        ArrayList<Text> result = mapperText.getAllTextsFromAuthor(
	        		author.getId(), true);
	        assertEquals(1, result.size());
        }
    }
    
    
    
    
}
