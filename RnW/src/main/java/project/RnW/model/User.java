package project.RnW.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import java.sql.Statement;
public class User {
	
	private ResourceBundle dbInfo = null;
	private int id;
	private String name;
	

	public User(String name, String pwd, boolean admin) {
		int id_temp = insert(name, pwd, admin);
		if(id_temp > -1) {
			this.id=id_temp;
			this.name = name;
		}
	}


	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	
	public int insert(String name, String pwd, boolean admin) {
		int id = -1;
		try{
			dbInfo = ResourceBundle.getBundle("dbconfig");
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbInfo.getString("db.url"), dbInfo.getString("db.user"), dbInfo.getString("db.pwd"));
			PreparedStatement pst = conn.prepareStatement("INSERT INTO users (NAME, PW, ADMINISTRATOR) VALUES (?,?,?)");
			pst.setString(1, name);
			pst.setString(2, pwd);
			pst.setBoolean(3, admin);
			pst.execute();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id FROM RnW.users WHERE name=\"" + name + "\"");
			if(rs.next()) {
				id = rs.getInt("id");
			}
		}catch(Exception ex) {
			System.out.println("ERRORE " + ex.toString());
		}
		return id;
	}
	
	public void update(User u) {
		
	}
	
	public void delete(User u) {
		
	}
	
}
