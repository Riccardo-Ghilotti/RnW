package project.RnW.controller;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.RnW.model.Text;
import project.RnW.model.User;

@Controller
public class textController {
	
	@RequestMapping("/writeText")
	public ModelAndView writeText(@RequestParam("id") String id) {
		ModelAndView mv = new ModelAndView("writeText");
		mv.addObject("ID", -1);
		mv.addObject("INTRO", new ArrayList<String>());
		mv.addObject("CORPUS", new ArrayList<String>());
		mv.addObject("CONC", new ArrayList<String>()); 
		mv.addObject("U_ID", id);
		return mv;
	}
	
	@RequestMapping("/saveText")
	public ModelAndView textSent(
			@RequestParam("text_id") String id, 
			@RequestParam("title") String title, 
			@RequestParam("intro") String intro, 
			@RequestParam("corpus") String corpus, 
			@RequestParam("conc") String conc,
			@RequestParam("author") String userIdStr
			) {
		ObjectMapper mapper = new ObjectMapper();
		// TODO: CHANGE RETURNED PAGE
		ModelAndView mv = new ModelAndView("login");
		
		ArrayList<String> introList = null;
		ArrayList<String> corpusList = null;
		ArrayList<String> concList = null;
		try {
			introList = mapper.readValue(intro, ArrayList.class);
			corpusList = mapper.readValue(corpus, ArrayList.class);
			concList = mapper.readValue(conc, ArrayList.class);
		} catch (JsonProcessingException e) {
			System.out.println("Errore: " + e.toString());
		}
	
		ObjectId userId = null;
		if(ObjectId.isValid(userIdStr))
			userId = new ObjectId(userIdStr);
	
		if(id.equals("-1")) {
			if(new Text(title, introList, corpusList,
					concList, User.getUser(userId)).getId() == null) //check if this works
				//TODO: return error as alert to the new returned page
				System.out.println("Errore nel salvataggio del testo");
		}
		else {
			User u = User.getUser(userId);
			Text t = Text.getText(id);
			try {
				t.changeIntro(introList, u);
				t.changeCorpus(corpusList, u);
				t.changeConclusion(concList, u);
			} catch (AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mv;
		
	}
	
	@RequestMapping("/text")
	public ModelAndView textLoad(
			@RequestParam("id") String textId) {
		Text t = Text.getText(textId);
		ObjectMapper mp = new ObjectMapper();
		
		
		return null;
	}
	
	
}
