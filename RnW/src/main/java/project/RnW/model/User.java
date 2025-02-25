package project.RnW.model;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;

import com.google.common.hash.Hashing;

import java.sql.Statement;
public class User {
	
	private static ResourceBundle dbInfo = ResourceBundle.getBundle("dbconfig");;
	private int id;
	private String name;
	private boolean admin;
	
	public User(String name, String pwd, boolean admin) {
		pwd = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
		int id_temp = insert(name, pwd, admin);
		if(id_temp > -1) {
			this.id=id_temp;
			this.name = name;
			this.admin = admin;
		}
	}
	
	public User(int id, String name, boolean admin) {
		this.id = id;
		this.name = name;
		this.admin = admin;
	}


	private static Connection loadDB() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbInfo.getString("db.url"), dbInfo.getString("db.user"), dbInfo.getString("db.pwd"));
		return conn;
	}

	private int insert(String name, String pwd, boolean admin) {
		int id = -1;
		try{
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("INSERT INTO users (NAME, PW, ADMINISTRATOR) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, name);
			pst.setString(2, pwd);
			pst.setBoolean(3, admin);
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			if(rs.next()) {
				id = rs.getInt(1);
			}
		}catch(Exception ex) {
			System.out.println("ERROR " + ex.toString()); //TODO: fix exception
		}
		return id;
	}

	protected void update(String name, String pwd) throws ClassNotFoundException, SQLException {
			Connection conn = loadDB();
			PreparedStatement stmt = null;
			if (pwd == null) {
				stmt = conn.prepareStatement("UPDATE users SET name = (?) WHERE id = (?);");
		    	stmt.setString(1, name);
			    stmt.setInt(2, this.id);
			    this.name = name;
			    }
		    else {
		    	stmt = conn.prepareStatement("UPDATE users SET pw = (?) WHERE id = (?);");
		    	stmt.setString(1, pwd);
			    stmt.setInt(2, this.id);
			    }
			stmt.executeUpdate();
	}

	public void delete() {
		try {
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE name=(?);");
			pst.setString(1, this.name);
			pst.execute();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void changeName(String name) {
		try {
			update(name, null);
		}catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("ERROR: name already taken");//TODO: print to view
		}catch(Exception e) {
			System.out.println("ERROR: " + e);//TODO: fix exception
		}
	}

	public void changePassword(String pwd) {
		pwd = Hashing.sha256().hashString(pwd, StandardCharsets.UTF_8).toString();
		try {
			update(null, pwd);
		}catch(Exception e) {
			System.out.println("ERROR: e");//TODO: fix exception
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public static User getUser(String name) {
		User u = null;
		try {
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("SELECT id, administrator FROM users WHERE name=(?)");
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				int temp_id = rs.getInt("id");
				boolean temp_admin = rs.getBoolean("administrator");
				u = new User(temp_id,name,temp_admin);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}
	
	public static User getUser(int id) {
		User u = null;
		try {
			Connection conn = loadDB();
			PreparedStatement pst = conn.prepareStatement("SELECT name, administrator FROM users WHERE id=(?)");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				String temp_name = rs.getString("name");
				boolean temp_admin = rs.getBoolean("administrator");
				u = new User(id,temp_name,temp_admin);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;

	}

	public boolean isAdmin() {
		return admin;
	}
	
}

