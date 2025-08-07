package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.InsertOneResult;

import project.RnW.db.Database;
import project.RnW.model.Report;

public class mapperReport {
	
	// this class handles database operations for text reports, 
	// including storage, retrieval, and deletion.
	// Also maps database documents to Report objects.
	
	//formats and inserts a new report into the database.
	public static boolean insert(ObjectId idReported, String report,
			ObjectId objectId) {
		Document document = new Document("id_reported", idReported)
				.append("report", report)
				.append("id_reporter", objectId);
		InsertOneResult result = Database.reports.insertOne(document);
		return result.getInsertedId() != null;
	}
	
	//removes a report document from the database.
	public static boolean resolveReport(ObjectId objectId) 
	throws MongoException{
		return Database.reports.
				deleteOne(eq("_id", objectId)).getDeletedCount() > 0;
	}

	//gathers all the report documents and maps them to Report objects.
	public static ArrayList<Report> getReports() 
	throws MongoException, AccountNotFoundException{
		FindIterable<Document> reportsDoc = Database.reports.find();
		ArrayList<Report> reports = new ArrayList<Report>();
		for(Document doc : reportsDoc) {
			reports.add(new Report(doc.getObjectId("_id"),
					mapperText.getText(doc.getObjectId("id_reported")),
					doc.getString("report"),
					mapperUser.getUser(doc.getObjectId("id_reporter"))));
		}
		
		return reports;
	}
	
	//given the id of a text, 
	//deletes all reports assigned to that text from the database.
	public static boolean deleteReportsOfText(ObjectId id) 
	throws MongoException{
		long toBeDeleted = Database.reports.countDocuments(
				eq("id_reported", id));
		return Database.reports.
				deleteMany(eq("id_reported", id)).
				getDeletedCount() == toBeDeleted;
	}
}
