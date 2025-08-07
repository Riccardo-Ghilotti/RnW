package project.RnW.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.security.auth.login.AccountNotFoundException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import project.RnW.db.Database;
import project.RnW.model.User;

public class mapperUserTest {

	private ObjectId userId;
	private String email;
	private String name;
	private String password;
	private String hashedPassword;

	@BeforeEach
	public void setup() {
		userId = new ObjectId();
		email = "user@example.com";
		name = "TestUser";
		password = "password123";
		hashedPassword = Hashing.sha256()
				.hashString(password, StandardCharsets.UTF_8)
				.toString();
    }

    @Test
    public void testUserInsert() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	InsertOneResult mockResult = mock(InsertOneResult.class);

    	when(mockResult.getInsertedId()).thenReturn(new BsonObjectId(userId));
    	when(mockCollection.insertOne(any(Document.class))).thenReturn(mockResult);

    	Database.users = mockCollection;

    	ObjectId result = mapperUser.insert(email, name, password, false);
    	assertEquals(userId, result);
    }

    @Test
    public void testUpdateName() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	UpdateResult mockResult = mock(UpdateResult.class);

    	when(mockResult.getModifiedCount()).thenReturn(1L);
    	when(mockCollection.updateOne(eq("_id", userId), set("name", "test-new")))
    	.thenReturn(mockResult);

    	Database.users = mockCollection;

    	boolean result = mapperUser.update(userId, "test-new", null);
    	assertTrue(result);
    }

    @Test
    public void testUpdatePassword() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	UpdateResult mockResult = mock(UpdateResult.class);

    	when(mockResult.getModifiedCount()).thenReturn(1L);
    	when(mockCollection.updateOne(eq("_id", userId), 
        		set("password", "newpassword"
                .toString()))).thenReturn(mockResult);

    	Database.users = mockCollection;

    	boolean result = mapperUser.update(userId, "test", "newpassword");
    	assertTrue(result);
    }

    @Test
    public void testDeleteUser() {
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	DeleteResult mockResult = mock(DeleteResult.class);

    	when(mockResult.getDeletedCount()).thenReturn(1L);
    	when(mockCollection.deleteOne(eq("_id", userId))).thenReturn(mockResult);

    	Database.users = mockCollection;

    	boolean deleted = mapperUser.delete(userId);
    	assertTrue(deleted);
    }

    //these tests will also test mapUser
    @Test
	public void testGetUserByID() throws AccountNotFoundException, MongoException {
		Document userDoc = new Document("_id", userId)
				.append("name", name)
	            .append("mail", email)
	            .append("password", hashedPassword)
	            .append("admin", false);
	
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
		FindIterable<Document> mockResult = mock(FindIterable.class);
	    
		when(mockResult.first()).thenReturn(userDoc);
		when(mockCollection.find(eq("_id", userId))).thenReturn(mockResult);
	    
		Database.users = mockCollection;
	    
	    User user = mapperUser.getUser(userId);
	
	    assertNotNull(user);
	    assertEquals(userId, user.getId());
	    assertEquals(name, user.getName());
	    assertFalse(user.isAdmin());
	}

	@Test
    public void testGetUserByIdMissingUserException() {
    	//Since testId is not in the database, the query returns null.
    	//Since the query returns null, mapUser throws an AccountNotFoundException.
    	//This exception signals that nothing was found in the database.
    	ObjectId testId = new ObjectId();
    	
    	MongoCollection<Document> mockCollection = mock(MongoCollection.class);
    	FindIterable<Document> mockResult = mock(FindIterable.class);
	     
    	when(mockResult.first()).thenReturn(null);
    	when(mockCollection.find(eq("_id", testId))).thenReturn(mockResult);
    	Database.users = mockCollection;

    	assertThrows(AccountNotFoundException.class, 
        		() ->mapperUser.getUser(testId));
    }
	

    @Test
    public void testReturnPassword() {
        Document userDoc = new Document("_id", userId)
                .append("password", hashedPassword);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockResult = mock(FindIterable.class);

        when(mockResult.first()).thenReturn(userDoc);
        when(mockCollection.find(eq("_id", userId))).thenReturn(mockResult);

        Database.users = mockCollection;

        String result = mapperUser.returnPassword(userId);
        assertTrue(result.equals( Hashing.sha256()
				.hashString(password, StandardCharsets.UTF_8).toString()));
    }

    //This method doesn't use Object Ids to find the user, but rather their credentials
    @Test
    public void testGetAndMapUser() throws AccountNotFoundException, IllegalArgumentException, MongoException {
        Document userDoc = new Document("_id", userId)
                .append("name", name)
                .append("mail", email)
                .append("password", hashedPassword)
                .append("admin", false);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockMailFind = mock(FindIterable.class);
        FindIterable<Document> mockCredFind = mock(FindIterable.class);

        when(mockMailFind.first()).thenReturn(userDoc);
        when(mockCredFind.first()).thenReturn(userDoc);

        when(mockCollection.find(eq("mail", email)))
                .thenReturn(mockMailFind);
        when(mockCollection.find(and(eq("mail", email),
        		eq("password", hashedPassword))))
                .thenReturn(mockCredFind);

        Database.users = mockCollection;

        User result = mapperUser.getAndMapUser(email, hashedPassword);
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(userId, result.getId());
    }

    @Test
    public void testGetAndMapUserInvalidCredentials() {
        Document userDoc = new Document("_id", userId)
                .append("name", name)
                .append("mail", email)
                .append("password", hashedPassword)
                .append("admin", false);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockMailFind = mock(FindIterable.class);
        FindIterable<Document> mockCredFind = mock(FindIterable.class);

        when(mockMailFind.first()).thenReturn(userDoc);
        when(mockCredFind.first()).thenReturn(null);

        when(mockCollection.find(eq("mail", email)))
                .thenReturn(mockMailFind);
        when(mockCollection.find(and(eq("mail", email),
				eq("password", "wrongpassword"))))
                .thenReturn(mockCredFind);

        Database.users = mockCollection;

        assertThrows(IllegalArgumentException.class, () -> {
            mapperUser.getAndMapUser(email, "wrongpassword");
        });
    }

    
    @Test
    public void testGetAndMapUserNotFound() {
        Document userDoc = new Document("_id", userId)
                .append("name", name)
                .append("mail", email)
                .append("password", hashedPassword)
                .append("admin", false);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        FindIterable<Document> mockIterable = mock(FindIterable.class);

        when(mockIterable.first()).thenReturn(null);

        when(mockCollection.find(eq("mail", email)))
                .thenReturn(mockIterable);

        Database.users = mockCollection;

        assertThrows(AccountNotFoundException.class, () -> {
            mapperUser.getAndMapUser(email, "test");
        });
    }

    
    @Test
    public void testGetAllUsers() {
        Document doc1 = new Document("_id", new ObjectId())
                .append("name", "User1").append("admin", false);

        FindIterable<Document> mockIterable = mock(FindIterable.class);
        MongoCursor<Document> mockedCursor = mock(MongoCursor.class);
        when(mockIterable.iterator()).thenReturn(mockedCursor);
        when(mockedCursor.hasNext()).thenReturn(true, false);
        when(mockedCursor.next()).thenReturn(doc1);

        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        when(mockCollection.find()).thenReturn(mockIterable);

        Database.users = mockCollection;

        assertEquals(1, mapperUser.getAllUsers().size());
    }
}
