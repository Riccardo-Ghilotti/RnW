package project.RnW.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;

import project.RnW.config.Config;
import project.RnW.config.WebViewConfig;
import project.RnW.controller.ControllerREST.textControllerREST;
import project.RnW.controller.ControllerWeb.textController;
import project.RnW.controller.ControllerWeb.userController;
import project.RnW.mappers.mapperComment;
import project.RnW.mappers.mapperComment.CommentNotFoundException;
import project.RnW.mappers.mapperReport;
import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Comment;
import project.RnW.model.Report;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceUser;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WebViewConfig.class })
@WebAppConfiguration
public class TextIT {
	
		@Autowired
		private WebApplicationContext webAppContext;
		
		private MockMvc mockMvc;
	
	    private ArrayList<String> intro;
	    private ArrayList<String> corpus;
	    private ArrayList<String> conclusion;
	    
	    private ObjectId userId;
	    private ObjectId userId2;
	    private User authorTxt;
	    private ObjectId textId;	    

	    @BeforeEach
	    public void setup() {
	    	
	    	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webAppContext)
	    			.build();
	    	
	        intro = new ArrayList<String>();
	        intro.add("Introduction");
	        corpus = new ArrayList<String>();
	        corpus.add("Body");
	        conclusion = new ArrayList<String>();
	        conclusion.add("Conclusion");
	        	        
	        
	    	userId = mapperUser.insert("test@example.com","name", "test", false);

	    	authorTxt = new User(userId, "name", false);
	    	textId = mapperText.insert("title test", intro, corpus, conclusion, false, authorTxt);
	    	
	    	userId2 = mapperUser.insert(
	    			"test2@example.com","name", "test", false);
	    }
	    
	    
	    @AfterEach
	    public void cleanup() {
	    	mapperUser.delete(userId);
    		mapperText.delete(textId);
    		mapperUser.delete(userId2);
	    }
	    
	    
	    @Test
	    public void testWriteTextNew() throws Exception {
	    	
	    	MvcResult result = mockMvc.perform(post("/writeText")
						.param("id", userId.toString()))
	    				.andExpect(status().isOk())
	    				.andExpect(view().name("writeText")).andReturn();
		    	ModelAndView mv = result.getModelAndView();
		    	Map<String, Object> model = mv.getModel();
		    	
		    	
		    	assertEquals(-1, model.get("ID"));
		    	assertEquals(new ArrayList<String>(), model.get("INTRO"));
		    	assertEquals(new ArrayList<String>(), model.get("CORPUS"));
		    	assertEquals(new ArrayList<String>(), model.get("CONC"));
		    	assertEquals(userId.toString() ,model.get("U_ID"));
    	}
	    
	    @Test
	    public void testWriteTextOld() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/writeText")
	    				.param("userId", userId.toString())
	    				.param("textId", textId.toString()))
	    				.andExpect(status().isOk())
	    				.andExpect(view().name("writeText")).andReturn();
		    	ModelAndView mv = result.getModelAndView();
		    	Map<String, Object> model = mv.getModel();
		    	
		    	
		    	assertEquals(textId.toString(), model.get("ID"));
		    	assertEquals("title test", model.get("TITLE"));
		    	assertEquals("[\"Introduction\"]", model.get("INTRO"));
		    	assertEquals("[\"Body\"]", model.get("CORPUS"));
		    	assertEquals("[\"Conclusion\"]", model.get("CONC"));
		    	assertEquals(userId.toString() ,model.get("U_ID"));
    	}
	    
	    
	    
	    @Test
	    public void testWriteTestUserNotFound() throws Exception {
	    	
	        	ObjectId userId2 = new ObjectId();
	        	MvcResult result = mockMvc.perform(post("/writeText")
	    				.param("userId", userId2.toString())
	    				.param("textId", textId.toString()))
	    				.andExpect(status().isOk())
	    				.andExpect(view().name("login")).andReturn();
	        	
	        	
	        	
	    		ModelAndView mv = result.getModelAndView();
		    	Map<String, Object> model = mv.getModel();
		    	
		    	assertEquals("Impossibile trovare l'account", 
		    			model.get("ERROR"));
		    	}
	    
	    @Test
	    public void testWriteTestTextNotFound() throws Exception {
	    	ObjectId textId2 = new ObjectId();
	    	MvcResult result = mockMvc.perform(post("/writeText")
    				.param("userId", userId.toString())
    				.param("textId", textId2.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("login")).andReturn();
        	
        	
        	
    		ModelAndView mv = result.getModelAndView();
	    	Map<String, Object> model = mv.getModel();
	    	
	    	assertEquals("Impossibile trovare il testo", 
	    			model.get("ERROR"));
		    	}
	    
	    @Test
	    public void testWriteTextNotAuthor() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/writeText")
    				.param("userId", userId2.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("home")).andReturn();
	    	
		    	ModelAndView mv = result.getModelAndView();
		    	Map<String, Object> model = mv.getModel();
		    	
		    	assertEquals(model.get("ERROR"), 
		    			"Non sei l'autore di questo testo");
    	}
	    
	    //this also tests a textLoad run without any errors
	    @Test
	    public void testTextSent() throws Exception {
	    	
	    	MvcResult result = mockMvc.perform(post("/saveText")
	    			.param("author", userId.toString())
    				.param("text_id", textId.toString())
    				.param("title", "title test")
    				.param("intro", "[\"Intro\"]")
	    			.param("corpus", "[\"Body of the text\"]")
	    			.param("conc", "[\"Conc\"]"))
    				.andExpect(status().isOk())
    				.andExpect(view().name("readText")).andReturn();
	    	
	    	ModelAndView mv = result.getModelAndView();
	    	Map<String, Object> model = mv.getModel();
	    	
	    	assertEquals(textId.toString(), model.get("ID"));
	    	assertEquals("title test", model.get("TITLE"));
	    	assertEquals("[\"Intro\"]", model.get("INTRO"));
	    	assertEquals("[\"Body of the text\"]", 
	    			model.get("CORPUS"));
	    	assertEquals("[\"Conc\"]", model.get("CONC"));
	    	assertEquals(userId.toString(), model.get("U_ID"));
	    	
	    }
	    
	    
	    //this test will also cover most exceptions, since the method catches
	    //all exceptions that are subclasses of Exception, except AccessDeniedException, and follows this behavior.
	    @Test
	    public void textSentEmptySections() throws Exception {
	    	
	    	MvcResult result = mockMvc.perform(post("/saveText")
	    			.param("author", userId.toString())
    				.param("text_id", textId.toString())
    				.param("title", "title test")
    				.param("intro", "[]")
	    			.param("corpus", "[]")
	    			.param("conc", "[]"))
    				.andExpect(status().isOk())
    				.andExpect(view().name("writeText")).andReturn();
	    	
		    	ModelAndView mv = result.getModelAndView();
		    	Map<String, Object> model = mv.getModel();
		    	
		    	assertEquals(textId.toString(), model.get("ID"));
		    	assertEquals("title test", model.get("TITLE"));
		    	assertEquals("[]", model.get("INTRO"));
		    	assertEquals("[]", model.get("CORPUS"));
		    	assertEquals("[]", model.get("CONC"));
		    	assertEquals(userId.toString(), model.get("U_ID"));
		    	assertEquals("Errore nel salvataggio del testo, per favore riprovare \n"
		    			+ "Se non sono stati fatti cambiamenti, allora tornare alla pagina precedente",
		    			model.get("ERROR"));
	    }
	    
	    @Test
	    public void textSentAccessDenied() throws Exception {
	    	
	    	
	    	MvcResult result = mockMvc.perform(post("/saveText")
    				.param("author", userId2.toString())
    				.param("text_id", textId.toString())
    				.param("title", "title test")
    				.param("intro", "[\"Intro\"]")
	    			.param("corpus", "[\"Body of the text\"]")
	    			.param("conc", "[\"Conc\"]"))
    				.andExpect(status().isOk())
    				.andExpect(view().name("login")).andReturn();
	    	
	    	
	    	ModelAndView mv = result.getModelAndView();
	    	
	    	Map<String, Object> model = mv.getModel();
	    	
	    	
	    	assertEquals("Non hai i permessi per effettuare questa azione",
	    			model.get("ERROR"));
	    	}
	    
	    @Test
	    public void testTextLoad() throws Exception {
	    	ObjectId userId2 = new ObjectId();
	    	
	    	MvcResult result = mockMvc.perform(post("/text")
    				.param("userId", userId2.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("readText")).andReturn();
	    	
	    	
	    	
    		ModelAndView mv = result.getModelAndView();
	    	Map<String, Object> model = mv.getModel();
	    	
	    	
	    	assertEquals(textId.toString() , model.get("ID"));
	    	assertEquals("title test" , model.get("TITLE"));
	    	assertEquals("[\"Introduction\"]", model.get("INTRO"));
	    	assertEquals("[\"Body\"]", model.get("CORPUS"));
	    	assertEquals("[\"Conclusion\"]", model.get("CONC"));
	    	assertEquals(null, model.get("U_ID")); //userId2 is not in the db
	    	assertEquals(false , model.get("IS_ADMIN"));
	    	assertEquals(false, model.get("IS_AUTHOR"));
	    }
	    
	    
	    //the isAdmin functionality works in a similar way to isAuthor and will not be tested
	    @Test
	    public void testTextLoadAuthor() throws Exception {
	    	MvcResult result =  mockMvc.perform(post("/text")
    				.param("userId", userId.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("readText")).andReturn();
	    	
    		ModelAndView mv = result.getModelAndView();
	    	Map<String, Object> model = mv.getModel();
	    	
	    	
	    	assertEquals(true, model.get("IS_AUTHOR"));
	    	}
	    
	    @Test
	    public void testTextLoadMissingText() throws Exception {
	    	ObjectId textId2 = new ObjectId();
	    	
	    	MvcResult result = mockMvc.perform(post("/text")
    				.param("userId", userId.toString())
    				.param("textId", textId2.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("login")).andReturn();
    		
	    	ModelAndView mv = result.getModelAndView();
    		Map<String, Object> model = mv.getModel();
	    	
    		
    		assertEquals("Impossibile trovare il testo", model.get("ERROR"));
	    }
	    
	    @Test
	    public void testDeleteText() throws Exception {
	    	mockMvc.perform(post("/deleteText")
    				.param("id", userId.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("home"));
	    	
	    	assertThrows(NoSuchElementException.class,() -> mapperText.getText(textId));
	    }
	    
	    @Test
	    public void testDeleteTextAccessDenied() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/deleteText")
    				.param("id", userId2.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("login")).andReturn();
	    	
	    	ModelAndView mv = result.getModelAndView();
	    	
	    	assertEquals("Non hai i permessi per effettuare questa azione", 
	    			mv.getModel().get("ERROR"));
	    }
	    
	    @Test
	    public void testDeleteTextNotFound() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/deleteText")
    				.param("id", userId.toString())
    				.param("textId", new ObjectId().toString()))
    				.andExpect(status().isOk())
    				.andExpect(view().name("login")).andReturn();
	    	
	    	ModelAndView mv = result.getModelAndView();
	    	
	    	assertEquals("Impossibile trovare il testo", 
	    			mv.getModel().get("ERROR"));
	    }
	    
	    @Test
	    public void testDeleteTextNotDeleted() throws Exception {
	    	try (MockedStatic<mapperText> utilities = Mockito.mockStatic(mapperText.class)) {
	    		utilities.when(() -> mapperText.getText(textId))
	    		.thenReturn(new Text(textId,
								"test", 
								intro, 
								corpus, 
								conclusion, 
								new ArrayList<Comment>(), 
								false, 
								authorTxt));
	    		utilities.when(() -> mapperText.delete(textId))
				.thenReturn(false);
				
	    		
	    		MvcResult result = mockMvc.perform(post("/deleteText")
	    				.param("id", userId.toString())
	    				.param("textId", textId.toString()))
	    				.andExpect(status().isOk())
	    				.andExpect(view().name("login")).andReturn();
		    	
		    	ModelAndView mv = result.getModelAndView();
		    	
		    	assertEquals("Impossibile cancellare il testo", 
		    			mv.getModel().get("ERROR"));
	    	}
	    }
	    
	    @Test
	    public void testComment() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/comment")
    				.param("userId", userId.toString())
    				.param("textId", textId.toString())
    				.param("content", "test"))
    				.andExpect(status().isOk()).andReturn();
	    	
	    	String commentResponse = result.getResponse().getContentAsString();
	    	String[] response = commentResponse.split("#");
	    	assertEquals("Commento aggiunto!", response[0]);
	    	assertNotEquals("", response[1]);
	    }
	    
	    @Test
	    public void testCommentUnsavedException() throws Exception {
	    	try (MockedStatic<mapperComment> utilities = Mockito.mockStatic(mapperComment.class)) {
	    		utilities.when(() -> mapperComment.insert(userId, textId, "test"))
	    		.thenReturn(null);
		    	MvcResult result = mockMvc.perform(post("/comment")
	    				.param("userId", userId.toString())
	    				.param("textId", textId.toString())
	    				.param("content", "test"))
	    				.andExpect(status().isInternalServerError()).andReturn();
		    	
		    	String commentResponse = result.getResponse().getContentAsString();

		    	assertEquals("Il commento non è stato salvato, riprova", commentResponse);
	    	}
	    }
	    
	    
	    @Test
	    public void testReport() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/reportText")
    				.param("idReporter", userId.toString())
    				.param("idReported", textId.toString())
    				.param("report", "test"))
    				.andExpect(status().isOk()).andReturn();
	    	
	    	String commentResponse = result.getResponse().getContentAsString();
	    	assertEquals(commentResponse, "Segnalazione effettuata con successo");
	    }
	    
	    @Test
	    public void testReportUserNotFound()  throws Exception {
	    	MvcResult result = mockMvc.perform(post("/reportText")
    				.param("idReporter", new ObjectId().toString())
    				.param("idReported", textId.toString())
    				.param("report", "test"))
    				.andExpect(status().isNotFound()).andReturn();
	    	
	    	String commentResponse = result.getResponse().getContentAsString();
	    	assertEquals(commentResponse, "Impossibile trovare l'account");
	    }
	    
	    @Test
	    public void testReportTextNotFound()  throws Exception {
	    	MvcResult result = mockMvc.perform(post("/reportText")
    				.param("idReporter", userId.toString())
    				.param("idReported", new ObjectId().toString())
    				.param("report", "test"))
    				.andExpect(status().isNotFound()).andReturn();
	    	
	    	String commentResponse = result.getResponse().getContentAsString();
	    	assertEquals(commentResponse, "Impossibile trovare il testo");
	    }
	    
	    @Test
	    public void testReportStoreException()  throws Exception {
	    	try (MockedStatic<mapperReport> utilities = Mockito.mockStatic(mapperReport.class)) {
	    		utilities.when(() -> mapperReport.insert(userId, "test", textId))
	    		.thenReturn(false);
	    		
	    		MvcResult result = mockMvc.perform(post("/reportText")
	    				.param("idReporter", userId.toString())
	    				.param("idReported", textId.toString())
	    				.param("report", "test"))
	    				.andExpect(status().isInternalServerError()).andReturn();
		    	
		    	String commentResponse = result.getResponse().getContentAsString();
		    	assertEquals(commentResponse, "Impossibile salvare la segnalazione");
	    	}
    	}
	    
	    @Test
	    public void testChangeVisibility() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/changeVisibility")
    				.param("userId", userId.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk()).andReturn();
	    	
	    	String visibilityResponse = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Visibilità del testo cambiata!", visibilityResponse);
	    }
	    
	    
	    @Test
	    public void testChangeVisibilityAccessException() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/changeVisibility")
    				.param("userId", userId2.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isForbidden()).andReturn();
	    	
	    	String visibilityResponse = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Non hai i permessi per compiere questa azione", visibilityResponse);

	    }
	    
	    @Test
	    public void testChangeVisibilityUserNotFound() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/changeVisibility")
    				.param("userId", new ObjectId().toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isNotFound()).andReturn();
	    	
	    	String visibilityResponse = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Impossibile trovare l'account", visibilityResponse);

	    }
	    
	    @Test
	    public void testChangeVisibilityTextNotFound() throws Exception {
	    	MvcResult result = mockMvc.perform(post("/changeVisibility")
    				.param("userId", userId2.toString())
    				.param("textId", new ObjectId().toString()))
    				.andExpect(status().isNotFound()).andReturn();
	    	
	    	String visibilityResponse = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Impossibile trovare il testo", visibilityResponse);

	    }
	    
	    @Test
	    public void testChangeVisibilityException() throws Exception {
	    	try (MockedStatic<mapperText> utilities = Mockito.mockStatic(mapperText.class)) {
	    		utilities.when(() -> mapperText.getText(textId))
	    		.thenReturn(new Text(textId,
								"test", 
								intro, 
								corpus, 
								conclusion, 
								new ArrayList<Comment>(), 
								false, 
								authorTxt));
	    		utilities.when(() -> mapperText.changeVisibility(textId, true))
	    		.thenReturn(false);
	    		
	    		MvcResult result = mockMvc.perform(post("/changeVisibility")
	    				.param("userId", userId.toString())
	    				.param("textId", textId.toString()))
	    				.andExpect(status().isInternalServerError()).andReturn();
	    		String visibilityResponse = result.getResponse().getContentAsString();
		    	
		    	assertEquals("Impossibile cambiare la visibilità del testo", visibilityResponse);
	    	}
	    }
	    
	    @Test
	    public void testResolveReport() throws Exception {
	    	mapperReport.insert(textId, "Test", userId);
	    	ArrayList<Report> reports = mapperReport.getReports();
	    	ObjectId repId = null;
	    	for(Report rep : reports) {
	    		if(textId.equals(rep.getReported().getId()) && 
	    				"Test".equals(rep.getContent()) &&
	    				userId.equals(rep.getReporter().getId()))
	    			repId = rep.getId();
	    	}
	        
	        MvcResult result = mockMvc.perform(post("/resolveReport")
    				.param("reportId", repId.toString()))
    				.andExpect(status().isOk())
    				.andReturn();
        	
        	
        	
    		String responseBody = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Segnalazione risolta!",
	    			responseBody);
	    }
	    
	    //In this case there's no need to mock the mapper class since here
	    //the exception will be thrown even if the report can't be found
	    @Test
	    public void testResolveReportNotDeleted() throws Exception {
	        
	        MvcResult result = mockMvc.perform(post("/resolveReport")
    				.param("reportId", new ObjectId().toString()))
    				.andExpect(status().isInternalServerError())
    				.andReturn();
        	
        	
        	
    		String responseBody = result.getResponse().getContentAsString();
	    	
	    	assertEquals("Impossibile risolvere la segnalazione",
	    			responseBody);

	    }
	    
	    @Test
	    public void testDeleteComment() throws Exception {
	    	ObjectId comment = mapperComment.insert(
	    			userId, textId, "Test Comment");
	    	
	    	MvcResult result = mockMvc.perform(post("/deleteComment")
    				.param("commentId", comment.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isOk())
    				.andReturn();
	    	
	    	String commentDeleted = result.getResponse().getContentAsString();
	        assertEquals("Commento rimosso!", commentDeleted);
	        
	    	assertThrows(CommentNotFoundException.class,() -> 
	    	mapperComment.getComment(textId, comment));

	    }
	    
	    @Test
	    public void testDeleteCommentNotFound() throws Exception {
	    	ObjectId comment = new ObjectId();
	    	
	    	MvcResult result = mockMvc.perform(post("/deleteComment")
    				.param("commentId", comment.toString())
    				.param("textId", textId.toString()))
    				.andExpect(status().isNotFound())
    				.andReturn();
	    	
	    	String commentDeleted = result.getResponse().getContentAsString();
	        assertEquals("Impossibile trovare il commento", commentDeleted);
	    }
	    
	    @Test
	    public void testDeleteCommentNotDeleted() throws Exception{
	    	try (MockedStatic<mapperComment> utilities = Mockito.mockStatic(mapperComment.class)) {
	    		ObjectId comment = new ObjectId();
	    		
	    		utilities.when(() -> mapperComment.getComment(textId, comment))
	    		.thenReturn(new Comment(comment, serviceUser.getUser(userId), "test"));
	    		
		    	MvcResult result = mockMvc.perform(post("/deleteComment")
	    				.param("commentId", comment.toString())
	    				.param("textId", textId.toString()))
	    				.andExpect(status().isInternalServerError())
	    				.andReturn();
		    	
		    	String commentDeleted = result.getResponse().getContentAsString();
		        assertEquals("Il commento non è stato cancellato, riprova", commentDeleted);
	    	}
	    }
	    
}
