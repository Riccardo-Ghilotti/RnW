package project.RnW.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import project.RnW.mappers.mapperReport;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceReport.ReportResolveException;
import project.RnW.service.serviceReport.ReportStoreException;

public class serviceReportTest {
	
	private Text text;
	private User reporter;
	private String content;
	
	@BeforeEach
	public void setup() {
		text = new Text(new ObjectId(),
						"Test",
						new ArrayList<String>(),
						new ArrayList<String>(),
						new ArrayList<String>(),
						new ArrayList<Comment>(),
						false,
						new User(new ObjectId(),
								"test",
								false));
		content = "Test comment";
		reporter = new User(new ObjectId(),
				"test",
				false);
	}
	
	
	@Test
	public void testSendReportException() {
		try (MockedStatic<mapperReport> utilities = Mockito.mockStatic(mapperReport.class)) {
			utilities.when(() -> mapperReport.insert(text.getId(), content, reporter.getId()))
			.thenReturn(false);
			
			assertThrows(ReportStoreException.class, () -> 
			serviceReport.sendReport(text, reporter, content));
		}
	}
	
	
	@Test
	public void testResolveReportException() {
		try (MockedStatic<mapperReport> utilities = Mockito.mockStatic(mapperReport.class)) {
			utilities.when(() -> mapperReport.resolveReport(reporter.getId()))
			.thenReturn(false);
			
			assertThrows(ReportResolveException.class, () -> 
			serviceReport.resolveReport(reporter.getId()));
		}
	}
}
