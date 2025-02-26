package project.RnW.model;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

public class Administrator extends User{

	public Administrator(String name, String pwd) {
		super(name, pwd, true);
	}
	
	
	public void deleteUser(User u) {
		u.delete();
	}
	
	public void updateUser(User u, String name) {
		try {
			u.update(name,null);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteText(Text t) {
		try {
			t.delete(this);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteComment(Comment c) {
		try {
			c.delete(this);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
