package project.RnW.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import project.RnW.controller.ControllerWeb.textController;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceText;
import project.RnW.service.serviceUser;
import project.RnW.service.serviceUser.InvalidIdException;

public class textControllerTest {

	//since the view interacts directly with the controller, many integration
	//tests will also directly test the logic of the controller. 
	//That's why controllers' unit tests are missing or very scanty.

	private ArrayList<Text> texts;
	private textController textC;
	
	private ObjectId id;
    private User author;
    private Text text;

    private ArrayList<String> intro;
    private ArrayList<String> corpus;
    private ArrayList<String> conclusion;
    private ArrayList<Comment> comments;

    @BeforeEach
    public void setup() {
        id = new ObjectId();
        author = new User(new ObjectId(), "elvis", false);
        intro = new ArrayList<String>();
        intro.add("Introduzione");
        corpus = new ArrayList<String>();
        corpus.add("Svolgimento del testo");
        conclusion = new ArrayList<String>();
        conclusion.add("Conclusione");
        comments = new ArrayList<Comment>();

        text = new Text(id,
        		"Titolo Testo", 
        		intro, 
        		corpus, 
        		conclusion, 
        		comments, 
        		true, 
        		author);;
		texts =  new ArrayList<Text>();
		texts.add(text);

		textC = new textController();
	}
	
	
	@Test
	public void testWriteTextOld() throws AccountNotFoundException, MongoException, InvalidIdException {
		try (MockedStatic<serviceText> utilitiesT = Mockito.mockStatic(serviceText.class)
				; MockedStatic<ControllerUtils> utilitiesCU = Mockito.mockStatic(ControllerUtils.class)
				; MockedStatic<serviceUser> utilitiesU = Mockito.mockStatic(serviceUser.class)) {
		
			utilitiesT.when(() -> serviceText.getText(text.getId().toString()))
			.thenReturn(text);
			utilitiesU.when(() -> serviceUser.getUser(author.getId().toString()))
			.thenReturn(author);
			
			utilitiesCU.when(() -> ControllerUtils.getMacroSectionsAsString(text))
			.thenReturn(new String[]{"Introduction", "Corpus", "Conclusion"});
			
			ModelAndView mv = textC.writeText(
					author.getId().toString(), text.getId().toString());
			Map<String, Object> model = mv.getModel();
			
			assertEquals(model.get("ID"), text.getId().toString());
			assertEquals(model.get("TITLE"), text.getTitle());
			assertEquals(model.get("INTRO"), "Introduction");
			assertEquals(model.get("CORPUS"), "Corpus");
			assertEquals(model.get("CONC"), "Conclusion");
			assertEquals(model.get("U_ID"), author.getId().toString());
		}
	}
	
	@Test
	public void testTextLoad() throws AccountNotFoundException, InvalidIdException {
		try (MockedStatic<serviceText> utilitiesT = Mockito.mockStatic(serviceText.class)
				; MockedStatic<ControllerUtils> utilitiesCU = Mockito.mockStatic(ControllerUtils.class)
				; MockedStatic<serviceUser> utilitiesU = Mockito.mockStatic(serviceUser.class)) {
		
			utilitiesT.when(() -> serviceText.getText(text.getId().toString()))
			.thenReturn(text);
			utilitiesU.when(() -> serviceUser.getUser(author.getId().toString()))
			.thenReturn(author);
			
			utilitiesCU.when(() -> ControllerUtils.getMacroSectionsAsString(text))
			.thenReturn(new String[]{"Introduction", "Corpus", "Conclusion"});
			
			ModelAndView mv = textC.textLoad(text.getId().toString(),
					author.getId().toString());
			Map<String, Object> model = mv.getModel();
			
			
			assertEquals(model.get("ID"), text.getId().toString());
			assertEquals(model.get("TITLE"), text.getTitle());
			assertEquals(model.get("INTRO"), "Introduction");
			assertEquals(model.get("CORPUS"), "Corpus");
			assertEquals(model.get("CONC"), "Conclusion");
			assertEquals(model.get("U_ID"), author.getId().toString());
		}
	}
	
	
	
}
