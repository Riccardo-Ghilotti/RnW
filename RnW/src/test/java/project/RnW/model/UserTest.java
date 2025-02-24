package project.RnW.model;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

public class UserTest {
	
	@Test
	public void TestUser() {
		User u = new User("Elvis", "ThisIsATest", false);
		assertEquals("Elvis",u.getName());
		u.delete();
	}
	
	@Test
	public void TestDelete() {
		User u = new User("Elvis", "ThisIsATest", false);
		u.delete();
		ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbInfo.getString("db.url"), dbInfo.getString("db.user"), dbInfo.getString("db.pwd"));
		Statement stmt = conn.createStatement();
		assertFalse(stmt.executeQuery("SELECT id FROM RnW.users WHERE name=\"Elvis\"").next());
		}
		catch(Exception e) {
			//TODO: fix exception
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestChangeName() {
		User u = new User("Elvis", "ThisIsATest", false);
		u.changeName("Paul");
		assertEquals("Paul", u.getName());
		u.delete();
	}
	
	@Test
	public void TestGetUser() {
		User u = new User("Elvis", "ThisIsATest", false);
		assertEquals("Elvis", User.getUser("Elvis").getName());
		int id = u.getId();
		assertEquals(id, User.getUser(id).getId());
		u.delete();
		
	}
	
}
