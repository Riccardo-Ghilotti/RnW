package project.RnW.controller.ControllerWeb;

import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;

import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;

import project.RnW.controller.ControllerUtils;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceText;
import project.RnW.service.serviceText.TextNotDeletedException;
import project.RnW.service.serviceText.TextUnsavedException;
import project.RnW.service.serviceUser;
import project.RnW.service.serviceUser.InvalidIdException;


@Controller
public class textController {
	
	//this class handles all calls from the view that return a new page.
	//In particular, this class focuses on operations that have a Text object,
	// or a Text related object as a subject.
	
	//method called when the user wants to create a new text.
	@RequestMapping(value = "/writeText", params = "!textId")
	public ModelAndView writeText(@RequestParam("id") String id) {
		ModelAndView mv = new ModelAndView("writeText");
		mv.addObject("ID", -1);
		mv.addObject("INTRO", new ArrayList<String>());
		mv.addObject("CORPUS", new ArrayList<String>());
		mv.addObject("CONC", new ArrayList<String>()); 
		mv.addObject("U_ID", id);
		return mv;
	}
	
	//method called when the user wants to modify an old text.
	@RequestMapping(value = "/writeText", params = "textId")
	public ModelAndView writeText(@RequestParam("userId") String userId,
			@RequestParam("textId") String textId) 
					throws AccountNotFoundException, 
					MongoException, InvalidIdException {
			Text t = serviceText.getText(textId);
			ModelAndView mv = new ModelAndView("writeText");
			if (t.isAuthor(serviceUser.getUser(userId))) {
				mv.addObject("ID", textId);
				mv.addObject("TITLE", t.getTitle());
				
				String[] macroSections = ControllerUtils.getMacroSectionsAsString(t);
				
				mv.addObject("INTRO", macroSections[0]);
				mv.addObject("CORPUS", macroSections[1]);
				mv.addObject("CONC", macroSections[2]);
				
	 
				mv.addObject("U_ID", userId);
			}
			else {
				mv = new ModelAndView("home");
				mv.addObject("ERROR", "Non sei l'autore di questo testo");
				return mv = ControllerUtils.setupHome(mv);
			}
			return mv;
	}
	
	//method called to update an old text or save a new text into the db.
	@RequestMapping("/saveText")
	public ModelAndView textSent(
			@RequestParam("text_id") String id, 
			@RequestParam("title") String title, 
			@RequestParam("intro") String intro, 
			@RequestParam("corpus") String corpus, 
			@RequestParam("conc") String conc,
			@RequestParam("author") String userId
			) throws AccessDeniedException {
		ModelAndView mv = null;
		try {
			id = serviceText.saveText(id, title, intro, corpus, conc, userId);
			mv = textLoad(id, userId);

			return mv;
		}
		catch(AccessDeniedException ade) {
			throw ade; //if the user trying to change the text's content isn't the author, they should be kicked from the page.
		}
		catch(Exception e) {
			try {
				title = new String(title.getBytes("ISO-8859-1"), "UTF-8");
		        intro = new String(intro.getBytes("ISO-8859-1"), "UTF-8");
		        corpus = new String(corpus.getBytes("ISO-8859-1"), "UTF-8");
		        conc = new String(conc.getBytes("ISO-8859-1"), "UTF-8");
		    } catch (UnsupportedEncodingException e1) {
		        e1.printStackTrace();
		    }
			mv = new ModelAndView("writeText");
			mv.addObject("ID", id);
			mv.addObject("TITLE", title);
			mv.addObject("U_ID", userId);
			mv.addObject("INTRO", intro);
			mv.addObject("CORPUS", corpus);
			mv.addObject("CONC", conc);
			mv.addObject("ERROR", "Errore nel salvataggio del testo, per favore riprovare \n" +
			 "Se non sono stati fatti cambiamenti, allora tornare alla pagina precedente");
			return mv;
		}
		
	}
	
	
	//this method is called when someone wants to read a text.
	@RequestMapping(value = "/text", params = "!content")
	public ModelAndView textLoad(
			@RequestParam("textId") String textId,
			@RequestParam("userId") String userId) 
					throws AccountNotFoundException, InvalidIdException {
		Text t = serviceText.getText(textId);
		ObjectMapper mp = new ObjectMapper();
		ModelAndView mv = new ModelAndView("readText");
		User u = null;
		try {
			u = serviceUser.getUser(
					userId);
	
			mv.addObject("IS_AUTHOR", t.isAuthor(u));
			mv.addObject("IS_ADMIN", u.isAdmin());
			mv.addObject("U_NAME", u.getName());
			mv.addObject("U_ID", userId);
		}
		catch(AccountNotFoundException | InvalidIdException e) {
			mv.addObject("IS_AUTHOR", false);
			mv.addObject("IS_ADMIN", false);
			mv.addObject("U_ID", null);
		}
		
		mv.addObject("TITLE", t.getTitle());
		mv.addObject("ID", textId);

		mv.addObject("IS_PRIVATE", t.isPrivate());

		
		try {
			String[] macroSections = ControllerUtils.getMacroSectionsAsString(t);
			
			mv.addObject("INTRO", macroSections[0]);
			mv.addObject("CORPUS", macroSections[1]);
			mv.addObject("CONC", macroSections[2]);
			mv.addObject("COMMENTS", mp.writeValueAsString(
					ControllerUtils.formatComments(
					t.getComments(), userId)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	
	@RequestMapping("/deleteText")
	public ModelAndView delete(
			@RequestParam("textId") String textId,
			@RequestParam("id") String userId) 
					throws AccountNotFoundException, 
					MongoException, 
					AccessDeniedException, 
					TextNotDeletedException, InvalidIdException {
		ModelAndView mv = new ModelAndView("home");
		Text t = serviceText.getText(textId);
		serviceText.delete(serviceUser.getUser(userId), t);
		mv = ControllerUtils.setupHome(mv);
		return mv;
	}
	
	
	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex){
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Non hai i permessi per effettuare questa azione");
		return mv;
	}
	
	@ExceptionHandler(TextNotDeletedException.class)
	public ModelAndView handleTextNotDeletedException(TextNotDeletedException ex){
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Impossibile cancellare il testo");
		return mv;
	}
	

}
