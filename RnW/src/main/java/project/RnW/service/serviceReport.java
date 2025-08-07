package project.RnW.service;

import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;

import project.RnW.service.serviceUser.InvalidIdException;
import project.RnW.mappers.mapperReport;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.model.Report;

public class serviceReport {

	//this class handles business logic for Report objects.
	
	public static void sendReport(Text t, User u, String report) 
			throws MongoException, ReportStoreException{
		if(!mapperReport.insert(t.getId(), report, u.getId()))
				throw new ReportStoreException("Report was not saved");
	}
	
	public static void resolveReport(ObjectId ObjectId) 
			throws MongoException, ReportResolveException{
		if(!mapperReport.resolveReport(ObjectId))
			throw new ReportResolveException("Report was not resolved");
	}
	
	public static void resolveReport(String ObjectId) 
			throws MongoException, ReportResolveException, InvalidIdException{
		try {
			if(!mapperReport.resolveReport(new ObjectId(ObjectId)))
				throw new ReportResolveException("Report was not resolved");
		} catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}
	
	//returns all the reports currently stored in the db
	public static ArrayList<Report> getReports() throws AccountNotFoundException, MongoException {
		ArrayList<Report> reportsDoc = mapperReport.getReports();
		return reportsDoc;
	}
	
	public static class ReportStoreException extends Exception{
		public ReportStoreException(String msg) {
			super(msg);
		}
	}
	
	public static class ReportResolveException extends Exception{
		public ReportResolveException(String msg) {
			super(msg);
		}
	}
}
