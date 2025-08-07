package project.RnW.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.MongoException;

import project.RnW.model.Comment;
import project.RnW.model.Report;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceText;
import project.RnW.service.serviceUser;

public class ControllerUtilsTest {
	
	private User user;
	private Text text;
	private ArrayList<Text> texts;
	
	@BeforeEach
	public void setup() {
		user = new User(new ObjectId(), 
				 "name",
				 false);
		text = new Text(new ObjectId(),
				"Test",
				new ArrayList<String>(),
				new ArrayList<String>(),
				new ArrayList<String>(),
				new ArrayList<Comment>(),
				false,
				user);
		texts =  new ArrayList<Text>();
		texts.add(text);
	}
			
	//the following methods will test setupUserPage and controllerUtils.getAllTextFromAuthor.
	@Test
	public void testSetupUserPage() throws AccountNotFoundException, MongoException {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
				
			utilities.when(() -> serviceText.getAllTextsFromAuthor(user.getId(), true))
			.thenReturn(texts);
		
			assertEquals("[{\"id\": \"" + text.getId() + "\",\"title\": \""
					+ text.getTitle()
					+ "\"}]",
					ControllerUtils.setupUserPage(user, new ModelAndView(), true)
					.getModel().get("TEXTS"));
		}
	}
	
	@Test
	public void testSetupUserPageNoTexts() throws AccountNotFoundException, MongoException {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
			
			utilities.when(() -> serviceText.getAllTextsFromAuthor(user.getId(), true))
			.thenReturn(new ArrayList<Text>());
			
			ModelAndView mv = ControllerUtils.setupUserPage(user, new ModelAndView(), true);
			Map<String, Object> model = mv.getModel();
			
			assertTrue(model.get("TEXTS") == null || "null".equals(model.get("TEXTS")));
		}
	}
	
	//The two following methods will test setupHome and controllerUtils.getAllTexts.
	@Test
	public void testSetupHome() {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
		
			utilities.when(() -> serviceText.getAllTexts()).thenReturn(texts);
			assertEquals("[{\"id\": \"" + text.getId() + "\",\"title\": \""
					+ text.getTitle()
					+ "\"}]",
					ControllerUtils.setupHome(new ModelAndView("home")).getModel().get("TEXTS"));
		}
	}
	
	@Test
	public void testSetupHomeNoTexts() {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
			
			utilities.when(() -> serviceText.getAllTexts())
			.thenReturn(new ArrayList<Text>());
			
			ModelAndView mv = ControllerUtils.setupHome(new ModelAndView());
			Map<String, Object> model = mv.getModel();
			
			assertEquals("null", model.get("TEXTS"));
		}
	}
	
	@Test
	public void testGetMacrosectionAsString() {
		String[] macroSections = ControllerUtils.getMacroSectionsAsString(text);
		
		assertTrue(macroSections[0] instanceof String);
		assertTrue(macroSections[1] instanceof String);
		assertTrue(macroSections[2] instanceof String);
	}
	
	@Test
	public void testFormatComments() {
		Comment comm = new Comment(new ObjectId(),
									user,
									"Test Comment");
		ArrayList<Comment> comments = new ArrayList<Comment>(); 
		comments.add(comm);
		ArrayList<ArrayList<String>> commentsString = 
				ControllerUtils.formatComments(comments, user.getId().toString());
		assertEquals(comm.getId().toString(), commentsString.get(0).get(0));
		assertEquals(user.getName(), commentsString.get(0).get(1));
		assertEquals(comm.getContent(), commentsString.get(0).get(2));
		
	}
	
	@Test
	public void testGetAllTextFromAuthor() throws AccountNotFoundException, MongoException {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
			ArrayList<Text> texts = new ArrayList<Text>();
			texts.add(text);
			
			utilities.when(() -> serviceText.getAllTextsFromAuthor(user.getId(), false))
			.thenReturn(texts);
			
			ArrayList<String[]> textsString = 
				ControllerUtils.getAllTextsFromAuthor(user, false);
			
			assertEquals(text.getId().toString(), textsString.get(0)[0]);
			assertEquals(text.getTitle(), textsString.get(0)[1]);
		}
	}
	
	@Test
	public void testGetUsersIds() {
		try (MockedStatic<serviceUser> utilities = Mockito.mockStatic(serviceUser.class)) {
			ArrayList<User> users = new ArrayList<User>();
			users.add(user);
			
			utilities.when(() -> serviceUser.getAllUsers())
			.thenReturn(users);
			
			ArrayList<String[]> usersString = 
				ControllerUtils.getUsersIds();
			
			assertEquals(user.getId().toString(), usersString.get(0)[0]);
			assertEquals(user.getName(), usersString.get(0)[1]);
		}
	}
	
	
	@Test
	public void testGetAllText() {
		try (MockedStatic<serviceText> utilities = Mockito.mockStatic(serviceText.class)) {
			ArrayList<Text> texts = new ArrayList<Text>();
			texts.add(text);
			
			utilities.when(() -> serviceText.getAllTexts())
			.thenReturn(texts);
			
			ArrayList<String[]> textsString = 
				ControllerUtils.getAllTexts();
			
			assertEquals(text.getId().toString(), textsString.get(0)[0]);
			assertEquals(text.getTitle(), textsString.get(0)[1]);
		}
	}
	
	
	@Test
	public void testGetReports() throws AccountNotFoundException, MongoException {
		try (MockedStatic<serviceReport> utilities = Mockito.mockStatic(serviceReport.class)) {
			Report report = new Report(new ObjectId(), text, "Test", user);
			
			ArrayList<Report> reports = new ArrayList<Report>();
			reports.add(report);
			
			utilities.when(() -> serviceReport.getReports())
			.thenReturn(reports);
			
			ArrayList<String[]> reportsString = 
				ControllerUtils.getReports();
			
			assertEquals(text.getId().toString(), reportsString.get(0)[0]);
			assertEquals(text.getTitle(), reportsString.get(0)[1]);
			assertEquals(user.getId().toString(), reportsString.get(0)[2]);
			assertEquals(user.getName(), reportsString.get(0)[3]);
			assertEquals(user.getId().toString(), reportsString.get(0)[4]);
			assertEquals(user.getName(), reportsString.get(0)[5]);
			assertEquals("Test", reportsString.get(0)[6]);
			assertEquals(report.getId().toString(), reportsString.get(0)[7]);

		}
	}
}
