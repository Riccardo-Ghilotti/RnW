package project.RnW.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TextTest {
	
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
	        intro.add("Introduction");
	        corpus = new ArrayList<String>();
	        corpus.add("Body");
	        conclusion = new ArrayList<String>();
	        conclusion.add("Conclusion");
	        comments = new ArrayList<Comment>();

	        text = new Text(id,
	        		"Text Title", 
	        		intro, 
	        		corpus, 
	        		conclusion, 
	        		comments, 
	        		true, 
	        		author);
	    }

	    @Test
	    public void testTextConstructorAndGetters() {
	        assertEquals(id, text.getId());
	        assertEquals("Text Title", text.getTitle());
	        assertEquals(intro, text.getIntro());
	        assertEquals(corpus, text.getCorpus());
	        assertEquals(conclusion, text.getConclusion());
	        assertEquals(comments, text.getComments());
	        assertEquals(author, text.getAuthor());
	        assertTrue(text.isPrivate());
	    }

	    @Test
	    public void testEquals() {
	        Text test = new Text(id, 
	        		"test", 
	        		intro, 
	        		corpus, 
	        		conclusion, 
	        		comments, 
	        		false, 
	        		author);
	        
	        assertTrue(text.equals(test));
	    }


	    @Test
	    public void testSetPrivate() throws AccessDeniedException {
	        text.setPrivate(false, author);
	        
	        assertFalse(text.isPrivate());
	    }
	    
	    @Test
	    public void testSetPrivateException() throws AccessDeniedException {
	        assertThrows(AccessDeniedException.class , () -> 
	        text.setPrivate(false, new User(new ObjectId(), "melvin", false)));
	    }
	    
		@Test
		public void testChangeIntro() throws AccessDeniedException {
			ArrayList<String> i = new ArrayList<String>();
			i.add("test");
			
			text.changeIntro(i, author);
			
			assertEquals(i, text.getIntro());
		}
		
		@Test
		public void testChangeIntroException() {
			User s = new User(new ObjectId(), "test", false);
			
			assertThrows(AccessDeniedException.class, () -> 
				text.changeIntro(new ArrayList<String>(), s));
		}
		
		
		@Test
		public void testChangeCorpus() throws AccessDeniedException {
			ArrayList<String> i = new ArrayList<String>();
			i.add("test");
			
			text.changeCorpus(i, author);
			
			assertEquals(text.getCorpus(), i);
		}
		
		@Test
		public void testChangeCorpusException() {
			User s = new User(new ObjectId(), "test", false);
			
			assertThrows(AccessDeniedException.class,() -> 
				text.changeCorpus(new ArrayList<String>(), s));
		}
		
		@Test
		public void testChangeConclusion() throws AccessDeniedException {
			ArrayList<String> i = new ArrayList<String>();
			i.add("test");
	
			text.changeConclusion(i, author);
			
			assertEquals(text.getConclusion(), i);
		}
		
		@Test
		public void testChangeConclusionException() {
			User s = new User(new ObjectId(), "test", false);
			
			assertThrows(AccessDeniedException.class, () -> 
			text.changeConclusion(new ArrayList<String>(), s));
		}
		
		
		
	}
