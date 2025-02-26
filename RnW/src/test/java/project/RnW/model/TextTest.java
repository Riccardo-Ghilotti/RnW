package project.RnW.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;


public class TextTest {
	
	@Test
	public void testText() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals(t.getTitle(), "Test");
		assertEquals(t.getAuthor(), u);
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testCompose() {
		ArrayList<String> s = new ArrayList<String>();
		s.add("test1");
		s.add("test2");
		String s1 = Text.compose(s);
		assertEquals("test1|test2",s1);
	}
	
	@Test
	public void testDelete() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}

	@Test
	public void testDeleteException() {
		User u = new User("Elvis","Test", false);
		User s = new User("Melvin","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertThrows(AccessDeniedException.class, () -> t.delete(s));
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
		s.delete();
	}

	@Test
	public void testChangeIntro() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		try {
			t.changeIntro("p", u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("p", t.getIntro());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testChangeIntroException() {
		User u = new User("Elvis","Test", false);
		User s = new User("Melvin","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertThrows(AccessDeniedException.class, () -> t.changeIntro("p", s));
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
		s.delete();
	}
	
	
	@Test
	public void testChangeCorpus() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		try {
			t.changeCorpus("p", u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(t.getCorpus(), "p");
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testChangeCorpusException() {
		User u = new User("Elvis","Test", false);
		User s = new User("Melvin","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertThrows(AccessDeniedException.class, () -> t.changeCorpus("p", s));
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
		s.delete();
	}
	
	@Test
	public void testChangeConclusion() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		try {
			t.changeConclusion("p", u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(t.getConclusion(), "p");
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testChangeConclusionException() {
		User u = new User("Elvis","Test", false);
		User s = new User("Melvin","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertThrows(AccessDeniedException.class, () -> t.changeConclusion("p", s));
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
		s.delete();
	}
	
	
	@Test
	public void testGetAuthor() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals(u, t.getAuthor());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testGetTitle() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals("Test", t.getTitle());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testGetIntro() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals("t", t.getIntro());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testGetCorpus() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals("t", t.getCorpus());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	@Test
	public void testGetConclusion() {
		User u = new User("Elvis","Test", false);
		Text t = new Text("Test","t","t","t", u);
		assertEquals("t", t.getConclusion());
		try {
			t.delete(u);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.delete();
	}
	
	
}
