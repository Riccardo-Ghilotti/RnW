package project.RnW.model;

import java.nio.file.AccessDeniedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class Comment {
	
	
	private static ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");
	private int id;
	private User  user;
	private Text text;
	private String content;

	
	public Comment(User user, Text text, String content) {
		this.user = user;
		this.text = text;
		this.content = content;
		
		this.id = this.insert();
	}
	
	
	private static Connection loadDB() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbInfo.getString("db.url"), dbInfo.getString("db.user"), dbInfo.getString("db.pwd"));
		return conn;
	}

	
	
	private int insert() {
		id = -1;
		try {
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("INSERT INTO comments (userId, textId, content) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, user.getId());
			pst.setInt(2, text.getId());
			pst.setString(3, content);
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			if(rs.next()) {
				id = rs.getInt(1);
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	
	protected void delete(User u) throws AccessDeniedException {
		if(u.getId() == user.getId() || u.isAdmin()) {
			try {
				Connection conn = loadDB();
				PreparedStatement pst = conn.prepareStatement("DELETE FROM comments WHERE id=(?);");
				pst.setInt(1, this.id);
				pst.execute();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new AccessDeniedException("Non hai il permesso di cancellare il commento");
		}
	}
	
	
	
	
	
	public User getUser() {
		return user;
	}

	public Text getText() {
		return text;
	}


	public String getContent() {
		return content;
	}
	
}
