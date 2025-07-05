package project.RnW.service;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import project.RnW.mappers.mapperReport;
import project.RnW.model.Text;
import project.RnW.model.Report;

public class serviceReport {

	
	public static boolean sendReport(Text t,ObjectId idReporter, String report) {
		return mapperReport.sendReport(t.getId(), report, idReporter);
	}
	
	public static boolean sendReport(Text t, String idReporter, String report) {
		return mapperReport.sendReport(t.getId(), report, new ObjectId(idReporter));
	}
	
	public static boolean resolveReport(ObjectId ObjectId) {
		return mapperReport.resolveReport(ObjectId);
	}
	
	public static boolean resolveReport(String ObjectId) {
		return mapperReport.resolveReport(new ObjectId(ObjectId));
	}
	
	public static ArrayList<Report> getReports() {
		ArrayList<Report> reportsDoc = mapperReport.getReports();
		return reportsDoc;
	}
}
