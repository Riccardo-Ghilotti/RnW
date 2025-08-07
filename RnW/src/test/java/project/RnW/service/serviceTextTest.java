package project.RnW.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.mongodb.MongoException;

import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceText.ChangeVisibilityException;
import project.RnW.service.serviceText.EmptyMacroSectionsException;
import project.RnW.service.serviceText.TextNotDeletedException;
import project.RnW.service.serviceText.TextUnsavedException;
import project.RnW.service.serviceUser.InvalidIdException;

public class serviceTextTest {

	private ObjectId textId;
    private String title;
	private ArrayList<String> intro;
	private ArrayList<String> corpus;
	private ArrayList<String> conc;
    private ArrayList<Comment> comments;
    private boolean isPrivate;
    private User author;
    private Text text;

    @BeforeEach
    public void setup() {
        textId = new ObjectId();
        title = "Test";
        intro = new ArrayList<String>();
        intro.add("Introduction");
        corpus = new ArrayList<String>();
        corpus.add("Corpus");
        conc = new ArrayList<String>();
        conc.add("Conclusion");
        isPrivate = false;
        author = new User(new ObjectId(),"nome",false);
        comments = new ArrayList<Comment>();
        comments.add(new Comment(new ObjectId(), author, "Test"));
        text = new Text(textId, title, intro, corpus, conc, comments, isPrivate, author);
    }
	
    
    @Test
    public void testSaveTextNewText() 
    		throws AccessDeniedException, 
    			MongoException, 
    			IllegalArgumentException, 
    			AccountNotFoundException, 
    			TextUnsavedException, 
    			EmptyMacroSectionsException, InvalidIdException{
    	String introString = "[\"Introduction\"]";
    	String corpusString = "[\"Corpus\"]";
    	String concString = "[\"Conclusion\"]";
    	
    	
    	try (MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)
    			; MockedStatic<mapperUser> utilitiesU = Mockito.mockStatic(mapperUser.class)){
    		
    		utilitiesT.when(() -> mapperText.insert(
    				eq(title), 
    				eq(new ArrayList<>(List.of("Introduction"))),
			        eq(new ArrayList<>(List.of("Corpus"))),
			        eq(new ArrayList<>(List.of("Conclusion"))), 
    				eq(true), 
    				eq(author))).thenReturn(textId);
    		utilitiesU.when(() -> mapperUser.getUser(author.getId()))
    			.thenReturn(author);
    		
			String textId = serviceText.saveText("-1", "Test",
					introString, corpusString, concString, author.getId().toString());
			assertNotNull(textId);
		}
    	
    }
    
    @Test
    public void testSaveTextNewTextUnsavedException() 
    		throws AccessDeniedException, 
    			MongoException, 
    			IllegalArgumentException, 
    			AccountNotFoundException, 
    			TextUnsavedException, 
    			EmptyMacroSectionsException{
    	String introString = "[\"Introduction\"]";
    	String corpusString = "[\"Corpus\"]";
    	String concString = "[\"Conclusion\"]";
    	
    	
    	try (MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)
    			; MockedStatic<mapperUser> utilitiesU = Mockito.mockStatic(mapperUser.class)){
    		
    		utilitiesT.when(() -> mapperText.insert(
    				eq(title), 
    				eq(new ArrayList<>(List.of("Introduction"))),
			        eq(new ArrayList<>(List.of("Corpus"))),
			        eq(new ArrayList<>(List.of("Conclusion"))), 
    				eq(true), 
    				eq(author))).thenReturn(null);
    		
			assertThrows(TextUnsavedException.class, () -> serviceText.saveText("-1", "Test",
					introString, corpusString, concString, author.getId().toString()));
			
		}
    	
    }
    
    
    @Test
    public void testSaveTextOldText() throws AccessDeniedException, AccountNotFoundException, MongoException, IllegalArgumentException, TextUnsavedException, EmptyMacroSectionsException, InvalidIdException{
        String introString = "[\"Introduction\"]";
        String corpusString = "[\"Corpus\"]";
        String concString = "[\"Conclusion\"]";

        try (MockedStatic<mapperUser> utilitiesU = Mockito.mockStatic(mapperUser.class) 
        		; MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)){

        	utilitiesT.when(() -> mapperText.update(
                    eq(textId),
                    eq(new ArrayList<>(List.of("Introduction"))),
                    eq(new ArrayList<>(List.of("Corpus"))),
                    eq(new ArrayList<>(List.of("Conclusion")))
            )).thenReturn(true);
        	utilitiesT.when(() -> mapperText.getText(textId)).thenReturn(text);
            utilitiesU.when(() -> mapperUser.getUser(author.getId()))
            .thenReturn(author);
            String returnedId = serviceText.saveText(
                    textId.toString(),
                    "Test",
                    introString, 
                    corpusString,
                    concString,
                    author.getId().toString());
            assertEquals(returnedId, textId.toString());
        }
    }
    
    
    @Test
    public void testSaveTextEmptyList()  throws MongoException{
    	
    	try (MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)
    			; MockedStatic<mapperUser> utilitiesU = Mockito.mockStatic(mapperUser.class)){
    		
    		utilitiesT.when(() -> mapperText.insert(
    				eq(title), 
    				eq(new ArrayList<>()),
			        eq(new ArrayList<>()),
			        eq(new ArrayList<>()), 
    				eq(true), 
    				eq(author))).thenReturn(textId);
    		utilitiesU.when(() -> mapperUser.getUser(author.getId()))
    			.thenReturn(author);
			assertThrows(EmptyMacroSectionsException.class, () -> 
				serviceText.saveText("-1", "Test",
						"[]", "[]", "[]", author.getId().toString()));
		}
    }
    
    @Test
    public void testSaveTextUpdateUnsavedException() {
    	String introString = "[\"Introduction\"]";
        String corpusString = "[\"Corpus\"]";
        String concString = "[\"Conclusion\"]";
    	try (MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)
    			; MockedStatic<mapperUser> utilitiesU = Mockito.mockStatic(mapperUser.class)){
    		
    		utilitiesT.when(() -> mapperText.update(
                    eq(textId),
                    eq(new ArrayList<>(List.of("Introduction"))),
                    eq(new ArrayList<>(List.of("Corpus"))),
                    eq(new ArrayList<>(List.of("Conclusion")))
            )).thenReturn(false);
    		utilitiesT.when(() -> mapperText.getText(textId)).thenReturn(text);
    		utilitiesU.when(() -> mapperUser.getUser(author.getId()))
    			.thenReturn(author);
			assertThrows(TextUnsavedException.class, () -> 
				serviceText.saveText(textId.toString(), "Test",
						introString, corpusString, concString, author.getId().toString()));
		}
    }
    
    @Test
    public void testDeleteTextNotDeletedException() {
    	try (MockedStatic<mapperText> utilities = Mockito.mockStatic(mapperText.class)){
    		utilities.when(() -> mapperText.delete(textId))
    		.thenReturn(false);
    		
    		assertThrows(TextNotDeletedException.class, () ->
    			serviceText.delete(author, text));
    	}
    }
    
    @Test
    public void testDeleteAccessDeniedException() {
    	User u = new User(new ObjectId(), "Test", false);
    	
    	assertThrows(AccessDeniedException.class, () ->
    			serviceText.delete(u, text));
    }
    
    @Test
    public void tesChangeVisibilityException() {
    	try (MockedStatic<mapperText> utilities = Mockito.mockStatic(mapperText.class)){
    		
    		utilities.when(() -> mapperText.changeVisibility(textId, true))
    				.thenReturn(false);
    		
    		assertThrows(ChangeVisibilityException.class, () ->
    			serviceText.changeVisibility(text, author));
    	}
    }
}
