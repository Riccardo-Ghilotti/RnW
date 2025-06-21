package project.RnW.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import project.RnW.model.Text;
import project.RnW.model.User;

@Controller
public class userController {
	
	@RequestMapping("/register")
	public ModelAndView register(@RequestParam("email") String mail,
			@RequestParam("username") String name,
			@RequestParam("password") String password,
			@RequestParam("rPassword") String rPassword){
		
		ModelAndView mv = null;
		
		if(password.equals(rPassword)) {
			User u = new User(mail, name, password, false);
			if(u.getId().equals("None")) {
				mv = new ModelAndView("login");
				mv.addObject("ERROR", "Nome già preso");
			}
			else {
				mv = new ModelAndView("profile");
				mv.addObject("NAME", name);
				if(!Text.getAllTextsFromAuthor(u).isEmpty()) {
					ObjectMapper mp = new ObjectMapper();
					String texts;
					try {
						texts = mp.writeValueAsString(
								Text.getAllTextsFromAuthor(u));
						mv.addObject("TEXTS", texts);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					}
				else
					mv.addObject("TEXTS", 
							"'Non hai ancora scritto nessun testo'");
			}
		}
		else {
			mv = new ModelAndView("login");
			mv.addObject("ERROR", "'Le password non coincidono'");
		}
		return mv;
	}
	
	@RequestMapping("/login")
	public ModelAndView loginLoad() {
		ModelAndView mv = new ModelAndView("login");
		return mv;
	}

	@RequestMapping("/user")
	public ModelAndView login(
			@RequestParam(value = "email", required = true) String mail,
			@RequestParam(value = "password", required = true) String pw) {
		pw = Hashing.sha256().hashString(pw, StandardCharsets.UTF_8).toString();
		User u = User.login(mail, pw);
		if(u != null) {
			ModelAndView mv = new ModelAndView("profile");
			mv.addObject("NAME", u.getName());
			mv.addObject("ID", u.getId());
			ArrayList<String[]> prova = Text.getAllTextsFromAuthor(u);
			System.out.println(prova.get(1)[1]);
			String json_id_title = "[";
			if(!Text.getAllTextsFromAuthor(u).isEmpty()) {
				for(String[] id_title : Text.getAllTextsFromAuthor(u)) {
					json_id_title += "{\"id\" : \"" + id_title[0] + "\",\"title\": \""
							+ id_title[1]
							+ "\"},";
				}
				json_id_title = 
						json_id_title.substring(0,json_id_title.length() - 1) 
							+ "]";
				mv.addObject("TEXTS", json_id_title);
			}
			else
				mv.addObject("TEXTS", 
						"'Non hai ancora scritto nessun testo'");
			return mv;
			}
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Errore, credenziali sbagliate");
		return mv;
		}
}
