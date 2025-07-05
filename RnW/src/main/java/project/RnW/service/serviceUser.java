package project.RnW.service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;

import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Database;
import project.RnW.model.Text;
import project.RnW.model.User;

public class serviceUser {
	
	public static User createUser(
			String mail,String name, String pwd, boolean admin) 
					throws MongoException{
		ObjectId id_temp = mapperUser.insert(mail, name, pwd, admin);
		if(id_temp != null)
			return new User(id_temp, name, admin);
		else
			throw new MongoException("Errore nella creazione dell'utente");
	}

	
	
	public static User getUser(String str) 
	throws IllegalArgumentException{
		return mapperUser.getUser(new ObjectId(str));
	}
	
	public static User getUser(ObjectId objId) 
			throws IllegalArgumentException{
				return mapperUser.getUser(objId);
			}
	
	public static User login(String mail, String password)
	throws IllegalArgumentException{
		return mapperUser.login(mail, password);
	}
	
	public static User register(String mail, String name, 
			String password, String rPassword) 
					throws IllegalArgumentException, MongoException{
		if(password.equals(rPassword))
			return createUser(mail, name, password, false);
		else
			throw new IllegalArgumentException("Le due password non corrispondono");
	}
	
	public static void changeName(String name, User u) throws MongoException{
		if(mapperUser.update(u.getId(), name, null))
			u.changeName(name);
		else
			throw new MongoException("Errore nel salvataggio del nuovo nome");
	}
	
	public static void changePassword(String pwd, User u) {
		if(mapperUser.checkPassword(u.getId(), pwd)) {
			if(!mapperUser.update(u.getId(), null, pwd))
				throw new MongoException(
						"Errore nel salvataggio della nuova password");
		}
		else {
			throw new IllegalArgumentException(
					"Passowrd precedente non valida");
		}
	}
	
	public static void delete(User u) {
		ArrayList<Text> texts = 
				mapperText.getAllTextsFromAuthor(u.getId());
		for(Text t : texts) {
			try{
				serviceText.delete(u, t);
			}catch(AccessDeniedException e) {
				//This cannot happen
				e.printStackTrace();
			}catch(MongoException me) {
				throw new MongoException(
						"Errore nella cancellazione dei testi dell'utente");
			}
		}
		if(!mapperUser.delete(u.getId()))
			throw new MongoException("Errore nella cancellazione dell'utente");
	}
	
	public static ArrayList<User> getAllUsers(){
		ArrayList<User> userList =  mapperUser.getAllUsers();
		return userList;
		}
}
