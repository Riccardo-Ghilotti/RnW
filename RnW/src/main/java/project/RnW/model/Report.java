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
	public ObjectId getId() {
		return id;
	}
	public Text getReported() {
		return reported;
	}
	public String getContent() {
		return content;
	}
	public User getReporter() {
		return reporter;
	}
}
