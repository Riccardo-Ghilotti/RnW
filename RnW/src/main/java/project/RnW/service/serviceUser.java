package project.RnW.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import javax.naming.directory.AttributeInUseException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;

import project.RnW.mappers.mapperComment;
import project.RnW.mappers.mapperReport;
import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceText.TextNotDeletedException;

public class serviceUser {
	
	//this class handles business logic for User objects.

	//called to create a User in the database and return it.
	public static User createUser(
			String mail,String name, String pwd, boolean admin) 
					throws MongoException, AttributeInUseException, AccountLockedException{
		ObjectId id_temp = null;
		try {
		 pwd = Hashing.sha256()
					.hashString(pwd, StandardCharsets.UTF_8).toString();
		 
		 id_temp = mapperUser.insert(mail, name, pwd, admin);
		if(id_temp != null)
			return new User(id_temp, name, admin);
		else
			throw new AccountLockedException("Encountered an error in account creation");}
		catch(MongoException e) {
			if(e.getCode() == 11000) //error code for duplicate unique value
				throw new AttributeInUseException("Email already in use");
			else
				throw e;
		}
	}
	
	public static User getUser(String str) 
	throws AccountNotFoundException, InvalidIdException{
		try {
		return mapperUser.getUser(new ObjectId(str));
		} // if str is null new ObjectId() throws IllegalArgumentException
		catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}
	
	public static User getUser(ObjectId objId) 
	throws MongoException, AccountNotFoundException{
				return mapperUser.getUser(objId);
			}
	
	public static User login(String mail, String password)
	throws IllegalArgumentException, MongoException, AccountNotFoundException{
		password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
		return mapperUser.getAndMapUser(mail, password);
	}
	
	public static User register(String mail, String name, 
			String password, String rPassword) 
					throws MongoException, AccountLockedException, AttributeInUseException, DifferentPasswordsException{

		if(password.equals(rPassword))
			return createUser(mail, name, password, false);
		else
			throw new DifferentPasswordsException("password and rPassword are different");
	}
	
	public static void changeName(String name, User u) throws MongoException, NameUnchangedException{
		if(mapperUser.update(u.getId(), name, null))
			u.changeName(name);
		else
			throw new NameUnchangedException("Name is unchanged");
	}
	
	public static void changePassword(String pwd, String newPwd, User u) throws PasswordUnchangedException {
		pwd = Hashing.sha256()
				.hashString(pwd, StandardCharsets.UTF_8).toString();
		newPwd = Hashing.sha256()
				.hashString(newPwd, StandardCharsets.UTF_8).toString();
		if(pwd.equals(mapperUser.returnPassword(u.getId()))) {
			if(!mapperUser.update(u.getId(), null, newPwd))
				throw new PasswordUnchangedException(
						"Password is unchanged");
		}
		else {
			throw new IllegalArgumentException(
					"Old password is incorrect");
		}
	}
	
	//method called to delete a user from the db
	public static void delete(User u) throws MongoException, AccountNotFoundException, UserDeletionException, TextNotDeletedException{
		ArrayList<Text> texts = 
				mapperText.getAllTextsFromAuthor(u.getId(), true);
		for(Text t : texts) {
			try{
				serviceText.delete(u, t);
			}catch(AccessDeniedException e) {
				//This cannot happen since this method deletes only u's texts
				e.printStackTrace();
			}
		}
		mapperComment.deleteFromUser(u.getId());
		if(!(mapperReport.deleteReportsOfUser(u.getId()) && mapperUser.delete(u.getId())))
			throw new UserDeletionException("User was not deleted from the database");
	}
	
	public static ArrayList<User> getAllUsers() throws MongoException{
		ArrayList<User> userList =  mapperUser.getAllUsers();
		return userList;
		}
	
	
	public static class DifferentPasswordsException extends Exception{
		public DifferentPasswordsException(String msg) {
			super(msg);
		}
	}
	
	public static class NameUnchangedException extends Exception{
		public NameUnchangedException(String msg) {
			super(msg);
		}
	}
	
	public static class PasswordUnchangedException extends Exception{
		public PasswordUnchangedException(String msg) {
			super(msg);
		}
	}
	
	public static class UserDeletionException extends Exception{
		public UserDeletionException(String msg) {
			super(msg);
		}
	}
	
	public static class InvalidIdException extends Exception{
		public InvalidIdException(String msg) {
			super(msg);
		}
	}
	
	
}
