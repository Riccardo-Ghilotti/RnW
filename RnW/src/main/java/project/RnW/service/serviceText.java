package project.RnW.service;

import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;

import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceUser.InvalidIdException;

public class serviceText {

	//this class handles business logic for Text objects.
	
	//this is the main procedure called to save a text.
	public static String saveText(String id, String title, String intro,
			String corpus, String conc, String userId) 
					throws MongoException, IllegalArgumentException, 
					AccessDeniedException, AccountNotFoundException, TextUnsavedException, 
					EmptyMacroSectionsException, InvalidIdException, ModifiedTitleException{
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			title = new String(title.getBytes("ISO-8859-1"), "UTF-8");
	        intro = new String(intro.getBytes("ISO-8859-1"), "UTF-8");
	        corpus = new String(corpus.getBytes("ISO-8859-1"), "UTF-8");
	        conc = new String(conc.getBytes("ISO-8859-1"), "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } 
		
		
		ArrayList<String> introList = null;
		ArrayList<String> corpusList = null;
		ArrayList<String> concList = null;
		ArrayList<Comment> commentList = null;
		try {
			introList = mapper.readValue(intro, ArrayList.class);
			corpusList = mapper.readValue(corpus, ArrayList.class);
			concList = mapper.readValue(conc, ArrayList.class);
		} catch (JsonProcessingException e) {
			System.out.println("ERROR: " + e.toString());
		}
		
		if(title.equals("") || introList.isEmpty() || corpusList.isEmpty() || concList.isEmpty())
			throw new EmptyMacroSectionsException(
					"Macro-sections cannot be empty");
		
		//if the Text is new, the id parameter will be set to "-1" and this check
		//controls whether the user wants to save a new text or update an older one.
		if(id.equals("-1")) {
			try {
				Text t = saveNewText(title, introList, corpusList,
						concList,  true, mapperUser.getUser(
								new ObjectId(userId)));
				id = t.getId().toString();	
			} catch(IllegalArgumentException e) {
				throw new InvalidIdException("id is malformed");
			}
		}
		else {
			try {
				User u = mapperUser.getUser(new ObjectId(userId));
				Text t = mapperText.getText(new ObjectId(id));
				if(!t.getTitle().equals(title))
					throw new ModifiedTitleException("Title of an existing text cannot be changed");
				updateText(introList, corpusList, concList, u, t);
			}
			catch(IllegalArgumentException e) {
				throw new InvalidIdException("id is malformed");
			}
		}
		return id;
	}

	public static Text saveNewText(String title, ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion, boolean isPrivate, User author)
	throws MongoException, IllegalArgumentException, TextUnsavedException{	
			ObjectId id = mapperText.insert(
					title, intro, corpus, conclusion, isPrivate, author);
			if(id == null)
				throw new TextUnsavedException("Text was not updated");
			Text t = new Text(
					id,
					title,
					intro, 
					corpus,
					conclusion,
					null,
					isPrivate,
					author);
			return t;
	}
	
	public static void updateText(ArrayList<String> intro,
			ArrayList<String> corpus,
			ArrayList<String> conc,
			User u,
			Text t) 
			throws AccessDeniedException, MongoException, TextUnsavedException {
			t.changeIntro(intro, u);
			t.changeCorpus(corpus, u);
			t.changeConclusion(conc, u);
			if(!mapperText.update(
					t.getId(), intro, corpus, conc))
				throw new TextUnsavedException("Text was not updated");
	}

	public static void delete(User u, Text t) throws AccessDeniedException,
		MongoException, TextNotDeletedException{
		if (t.isAuthor(u) || u.isAdmin()) {
			if(!mapperText.delete(t.getId()))
				throw new TextNotDeletedException("Text was not deleted");
			}
		else throw new AccessDeniedException(
				"User doesn't have permission to delete the text given");
		}

	public static ArrayList<Text> getAllTexts() throws MongoException {
		ArrayList<Text> textList = mapperText.getAllVisibleTexts();
		return textList;
	}

	public static ArrayList<Text> getAllTextsFromAuthor(ObjectId objectId, boolean isOwner) throws AccountNotFoundException, MongoException {
		ArrayList<Text> textList = mapperText.getAllTextsFromAuthor(objectId, isOwner);
		return textList;
	}
	

	
	public static Text getText(ObjectId objectId) throws MongoException, AccountNotFoundException{
		return mapperText.getText(objectId);
	}
	public static Text getText(String objectId) throws MongoException, AccountNotFoundException, InvalidIdException{
		try {
			return mapperText.getText(new ObjectId(objectId));
			}
		catch(IllegalArgumentException e) {
			throw new InvalidIdException("id is malformed");
		}
	}

	
	public static void changeVisibility(Text t, User u) throws AccessDeniedException, ChangeVisibilityException{
		boolean isPrivate = !t.isPrivate();
		t.setPrivate(isPrivate, u);
		if(!(mapperText.changeVisibility(t.getId(), isPrivate)))
				throw new ChangeVisibilityException(
						"Couldn't change text");
	}
	
	public static class TextUnsavedException extends Exception{
		public TextUnsavedException(String msg) {
			super(msg);
		}
	}
	
	
	public static class ChangeVisibilityException extends Exception{
		public ChangeVisibilityException(String msg) {
			super(msg);
		}
	}
	
	public static class EmptyMacroSectionsException extends Exception{
		public EmptyMacroSectionsException(String msg) {
			super(msg);
		}
	}
	
	public static class TextNotDeletedException extends Exception{
		public TextNotDeletedException(String msg) {
			super(msg);
		}
	}
	
	public static class ModifiedTitleException extends Exception{
		public ModifiedTitleException(String msg) {
			super(msg);
		}
	}
}
