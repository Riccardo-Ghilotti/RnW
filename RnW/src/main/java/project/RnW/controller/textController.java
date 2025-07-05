package project.RnW.controller;

import java.nio.file.AccessDeniedException;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;


import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceComment;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceText;
import project.RnW.service.serviceUser;


@Controller
public class textController {
	
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
	
	@RequestMapping(value = "/writeText", params = "textId")
	public ModelAndView writeText(@RequestParam("userId") String userId,
			@RequestParam("textId") String textId) {
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
			return mv = ControllerUtils.home();
		}
		return mv;
	}
	
	@RequestMapping("/saveText")
	public ModelAndView textSent(
			@RequestParam("text_id") String id, 
			@RequestParam("title") String title, 
			@RequestParam("intro") String intro, 
			@RequestParam("corpus") String corpus, 
			@RequestParam("conc") String conc,
			@RequestParam("author") String userId
			) {
		ModelAndView mv = null;
		try {
			serviceText.saveText(id, title, intro, corpus, conc, userId);
			mv = textLoad(id, userId);
		}
		catch(MongoException | IllegalArgumentException e) {
			mv = new ModelAndView("writeText");
			mv.addObject("TITLE", title);
			mv.addObject("U_ID", userId);
			mv.addObject("INTRO", intro);
			mv.addObject("CORPUS", corpus);
			mv.addObject("CONC", conc);
			mv.addObject("ERROR", e.getMessage());
		} catch (AccessDeniedException e) {
			mv = new ModelAndView("home");
			mv.addObject("ERROR", e.getMessage());
			mv = ControllerUtils.home();
			return mv;
		}
		
		mv = textLoad(id, userId);

		return mv;
		
	}
	
	@RequestMapping(value = "/text", params = "!content")
	public ModelAndView textLoad(
			@RequestParam("textId") String textId,
			@RequestParam("userId") String userId) {
		Text t = serviceText.getText(textId);
		ObjectMapper mp = new ObjectMapper();
		ModelAndView mv = new ModelAndView("readText");
		User u = serviceUser.getUser(
				userId);
		if(u != null) {
			mv.addObject("IS_AUTHOR", t.isAuthor(u));
			mv.addObject("IS_ADMIN", u.isAdmin());
		}
		else
		{
			mv.addObject("IS_AUTHOR", false);
		}
		mv.addObject("TITLE", t.getTitle());
		mv.addObject("ID", textId);
		mv.addObject("U_ID", userId);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/comment", params = "content")
	public ResponseEntity<String> comment(
			@RequestParam("textId") String textId,
			@RequestParam("userId") String userId,
			@RequestParam("content") String content) {
		    
		if(serviceComment.saveComment(userId, 
				textId, content))
			return new ResponseEntity<>("Commento aggiunto!",
				HttpStatus.OK);
		else
			return new ResponseEntity<>(
					"Errore nel salvataggio del commento",
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	

	
	
	
	@RequestMapping("/deleteText")
	public ModelAndView delete(
			@RequestParam("textId") String textId,
			@RequestParam("id") String userId) {
		ModelAndView mv = null;
		mv = ControllerUtils.home();
		Text t = serviceText.getText(textId);
		try {
			serviceText.delete(serviceUser.getUser(userId), t);
		} catch (AccessDeniedException e) {
			mv.addObject("ERROR", 1);
		}
		return mv;
	}
	
	
	@RequestMapping("/reportText")
	@ResponseBody
	public ResponseEntity<String> reportText(
			@RequestParam("idReported") String idReported,
			@RequestParam("report") String report,
			@RequestParam("idReporter") String idReporter) {
		Text t = serviceText.getText(idReported);
		if (serviceReport.sendReport(t, idReporter, report))
			return new ResponseEntity<>("Segnalazione effettuata con successo",
					HttpStatus.OK);
		else
			return new ResponseEntity<>(
					"Errore nel salvataggio della segnalazione",
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping("/changeVisibility")
	@ResponseBody
	public ResponseEntity<String> changeVisibility(
			@RequestParam("userId") String userId,
			@RequestParam("textId") String textId) {
		Text t = serviceText.getText(textId); 
		ResponseEntity<String> response = null;
		try {
			serviceText.setPrivate(t, serviceUser.
					getUser(userId));
			response = new ResponseEntity<>("Visibilità del testo cambiata!",
					HttpStatus.OK);
		}
		catch(MongoException e) {
			response = new ResponseEntity<>(
					e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	
	@RequestMapping("/resolveReport")
	@ResponseBody
	public ResponseEntity<String> resolveReport(
			@RequestParam("reportId") String idReport) {
		if(serviceReport.resolveReport(idReport))
			return new ResponseEntity<>("Segnalazione risolta!",
					HttpStatus.OK);
		else
			return new ResponseEntity<>(
					"Errore nella risoluzione della segnalazione",
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	@RequestMapping("/deleteComment")
	public ResponseEntity<String> deleteComment(
			@RequestParam("commentId") String commentId,
			@RequestParam("textId") String textId) {
		Comment c = serviceComment.getComment(
				textId,
				commentId);
		try{
			serviceComment.deleteComment(textId, c);
			return new ResponseEntity<>("Commento rimosso!",
					HttpStatus.OK);
		}catch(MongoException e) {
			return new ResponseEntity<>(
					"Errore nella rimozione del commento",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}
