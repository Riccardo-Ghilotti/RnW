package project.RnW.model;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class UserTest {
	
	@Test
	public void TestUser() {
		User u = new User("Elvis", "ThisIsATest",false);
		assertEquals("Elvis",u.getName() );
	}
}
