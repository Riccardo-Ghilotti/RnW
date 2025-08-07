package project.RnW.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import static com.mongodb.client.model.Filters.eq;

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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import project.RnW.db.Database;
import project.RnW.model.Comment;
import project.RnW.model.Report;
import project.RnW.model.Text;
import project.RnW.model.User;

public class mapperReportTest {

	
	private ObjectId reportId;
	private ObjectId reportedId;
	private String content;
	private ObjectId reporterId;
	
	
	@BeforeEach
	public void setup() {
		reportId = new ObjectId();
		reportedId = new ObjectId();
		content = "Test";
		reporterId = new ObjectId();
	}
	
	@Test
	public void testInsert() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        InsertOneResult mockResult = mock(InsertOneResult.class);

        when(mockResult.getInsertedId()).thenReturn(new BsonObjectId(reportId));
        when(mockCollection.insertOne(any(Document.class))).thenReturn(mockResult);

        Database.reports = mockCollection;

        boolean result = mapperReport.insert(reportedId, content, reporterId);
        assertTrue(result);
	}
	
	@Test
	public void resolveReport() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
        DeleteResult mockResult = mock(DeleteResult.class);

        when(mockResult.getDeletedCount()).thenReturn(1L);
        when(mockCollection.deleteOne(eq("_id", reportId))).thenReturn(mockResult);

        Database.reports = mockCollection;

        boolean deleted = mapperReport.resolveReport(reportId);
        assertTrue(deleted);
	}
	
	@Test
	public void getReports() throws AccountNotFoundException, MongoException {
		Document reportDoc = new Document("_id", reportId)
				.append("id_reported", reportedId)
				.append("report", content)
				.append("id_reporter", reporterId);
		
        MongoCollection<Document> mockCollection = mock(MongoCollection.class);
		FindIterable<Document> mockIterable = mock(FindIterable.class);
        MongoCursor<Document> mockedCursor = mock(MongoCursor.class);
        when(mockIterable.iterator()).thenReturn(mockedCursor);
        when(mockedCursor.hasNext()).thenReturn(true, false);
        when(mockedCursor.next()).thenReturn(reportDoc);
        
        when(mockCollection.find()).thenReturn(mockIterable);
        
        Database.reports = mockCollection;
        
        try (MockedStatic<mapperText> utilitiesText = Mockito.mockStatic(mapperText.class)
    			; MockedStatic<mapperUser> utilitiesUser = Mockito.mockStatic(mapperUser.class)) {
        	utilitiesText.when(() -> mapperText.getText(reportedId))
        	.thenReturn(new Text(reportedId, "test",
        						new ArrayList<String>(),
        						new ArrayList<String>(),
        						new ArrayList<String>(),
        						new ArrayList<Comment>(),
        						false,
        						new User(new ObjectId(), "test", false)));
        	utilitiesUser.when(() -> mapperUser.getUser(reporterId))
        	.thenReturn(new User(reporterId, "test", false));
        	
        	ArrayList<Report> report = mapperReport.getReports();
        	assertEquals(reportId, report.get(0).getId());
        	assertEquals(reportedId, report.get(0).getReported().getId());
        	assertEquals(content, report.get(0).getContent());
        	assertEquals(reporterId, report.get(0).getReporter().getId());
        }
        
	}
	
	@Test
	public void testDeleteReportsOfTexts() {
		MongoCollection<Document> mockCollection = mock(MongoCollection.class);
		DeleteResult mockDeleteResult = mock(DeleteResult.class);
		
		when(mockCollection.countDocuments(eq("id_reported", reportedId)))
		.thenReturn(1L);
		
		when(mockDeleteResult.getDeletedCount()).thenReturn(1L);
		when(mockCollection.deleteMany(eq("id_reported", reportedId)))
		.thenReturn(mockDeleteResult);
		
		Database.reports = mockCollection;
		
		boolean deletedReports = mapperReport.deleteReportsOfText(reportedId);
		assertTrue(deletedReports);
	}
}
