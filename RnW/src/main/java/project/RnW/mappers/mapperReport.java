package project.RnW.mappers;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.InsertOneResult;

import project.RnW.model.Database;
import project.RnW.model.Report;

public class mapperReport {
	
	public static boolean sendReport(ObjectId idReported, String report,
			ObjectId id) {
		Document document = new Document("id_reported", idReported)
				.append("report", report)
				.append("id_reporter", id);
		InsertOneResult result = Database.reports.insertOne(document);
		return result.wasAcknowledged();
	}
	
	public static boolean resolveReport(ObjectId objectId) {
		return Database.reports.
				deleteOne(eq("_id", objectId)).getDeletedCount() > 0;
	}

	
	public static ArrayList<Report> getReports() {
		FindIterable<Document> reportsDoc = Database.reports.find();
		ArrayList<Report> reports = new ArrayList<Report>();
		for(Document doc : reportsDoc) {
			reports.add(new Report(doc.getObjectId("_id"),
					mapperText.getText(doc.getObjectId("reportedId")),
					doc.getString("report"),
					mapperUser.getUser(doc.getObjectId("reporterId"))));
		}
		return reports;
	}
	
	public static boolean deleteReportsOfText(ObjectId id) {
		long toBeDeleted = Database.reports.countDocuments(
				eq("reportedId", id));
		return Database.reports.
				deleteMany(eq("reportedId", id)).
				getDeletedCount() == toBeDeleted;
	}
}
