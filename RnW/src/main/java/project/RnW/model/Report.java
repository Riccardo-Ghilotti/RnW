package project.RnW.model;

import org.bson.types.ObjectId;

public class Report {
	
	private ObjectId id;
	private Text reported;
	private String content;
	private User reporter;
	
	
	public Report(ObjectId id, Text reported, String content, User reporter) {
		this.id = id;
		this.reported = reported;
		this.content = content;
		this.reporter = reporter;
	}

	//returns the id of the report.
	public ObjectId getId() {
		return id;
	}

	//returns the Text that was reported.
	public Text getReported() {
		return reported;
	}

	//returns the content of the report.
	public String getContent() {
		return content;
	}

	//returns the User that made the report.
	public User getReporter() {
		return reporter;
	}
}
