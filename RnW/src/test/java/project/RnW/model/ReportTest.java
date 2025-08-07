package project.RnW.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    public void testReportConstructorAndGetters() {
        ObjectId reportId = new ObjectId();
        ObjectId textId = new ObjectId();
        ObjectId reporterId = new ObjectId();

        User reporter = new User(reporterId, "elvis", false);
        Text reportedText = new Text(
                textId,
                "title",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                false,
                reporter
        );
        String content = "test";

        Report report = new Report(reportId, reportedText, content, reporter);

        assertEquals(reportId, report.getId());
        assertEquals(reportedText, report.getReported());
        assertEquals(content, report.getContent());
        assertEquals(reporter, report.getReporter());
    }

}