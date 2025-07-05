package project.RnW.service;

import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;

import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;

public class serviceText {

	
	public static String saveText(String id, String title, String intro,
			String corpus, String conc, String userId) 
					throws MongoException, IllegalArgumentException, 
					AccessDeniedException{
		ObjectMapper mapper = new ObjectMapper();
		
		try {
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
			System.out.println("Errore: " + e.toString());
		}
		if(id.equals("-1")) {
			Text t = saveNewText(title, introList, corpusList,
					concList, true,  mapperUser.getUser(
							new ObjectId(userId)));
			id = t.getId().toString();
		}
		else {
			User u = mapperUser.getUser(new ObjectId(userId));
			Text t = mapperText.getText(new ObjectId(id));
			updateText(introList, corpusList, concList, u, t);
		}
		return id;
	}

	public static Text saveNewText(String title, ArrayList<String> intro, 
			ArrayList<String> corpus,
			ArrayList<String> conclusion, boolean isPrivate, User author)
	throws MongoException, IllegalArgumentException{
		
		if(intro.isEmpty() || corpus.isEmpty() || conclusion.isEmpty())
			throw new IllegalArgumentException(
					"Le tre macro sezioni devono contenere del testo");
		
		try {
			ObjectId id = mapperText.insert(
					title, intro, corpus, conclusion, isPrivate, author);
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
		}catch(MongoWriteException me) {
				throw new MongoException("Errore nella creazione dell'utente");
		}
	}
	
	public static void updateText(ArrayList<String> intro,
			ArrayList<String> corpus,
			ArrayList<String> conc,
			User u,
			Text t) 
			throws AccessDeniedException {
		if(t.isAuthor(u)) {
			mapperText.update(
					t.getId(), intro, corpus, conc);
			t.changeIntro(intro, u);
			t.changeCorpus(corpus, u);
			t.changeConclusion(conc, u);
		}else {
			throw new AccessDeniedException("Non sei l'autore di questo testo");
		}
	}

	public static void delete(User u, Text t) throws AccessDeniedException {
		if (t.isAuthor(u)|| u.isAdmin())
			mapperText.delete(t.getId());
		else throw new AccessDeniedException(
				"Non hai i permessi per cancellare questo testo");
	}

	public static ArrayList<Text> getAllTexts() {
		ArrayList<Text> textList = mapperText.getAllVisibleTexts();
		return textList;
	}

	public static ArrayList<Text> getAllTextsFromAuthor(ObjectId objectId) {
		ArrayList<Text> textList = mapperText.getAllTextsFromAuthor(objectId);
		return textList;
	}
	

	
	public static Text getText(ObjectId objectId) {
		return mapperText.getText(objectId);
	}
	public static Text getText(String objectId) {
		return mapperText.getText(new ObjectId(objectId));
	}

	
	public static void setPrivate(Text t, User u) {
		boolean isPrivate = !t.isPrivate();
		t.setPrivate(isPrivate, u);
		if(!(mapperText.changeVisibility(t.getId(), isPrivate) && 
				t.isPrivate() != isPrivate))
			throw new MongoException(
					"Impossibile cambiare la visibilità del testo");
	}
}
