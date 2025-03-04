package project.RnW.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import project.RnW.model.User;

@Controller

public class userController {
	
	@RequestMapping("/register")
	public ModelAndView register(@RequestParam("username") String name,
			@RequestParam("password") String password, @RequestParam("rPassword") String rPassword){
		
		ModelAndView mv = null;
		
		if(password.equals(rPassword)) {
			if(User.insert(name, password, false) == -1) {
				mv = new ModelAndView("login");
				mv.addObject("ERROR", "Nome già preso");
			}
			else {
				mv = new ModelAndView("profile");
				mv.addObject("NAME", name);
			}
		}
		else {
			mv = new ModelAndView("login");
			mv.addObject("ERROR", "Le password non coincidono");
		}
		return mv;

	}
	
	@RequestMapping("/login")
	public ModelAndView loginLoad() {
		ModelAndView mv = new ModelAndView("login");
		return mv;
	}

	@RequestMapping("/user")
	public ModelAndView login(@RequestParam("username") String name,
			@RequestParam("password") String pw) {
		if(User.login(name, pw)) {
			ModelAndView mv = new ModelAndView("profile");
			mv.addObject("NAME", name);
			return mv;
			}
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Errore, credenziali sbagliate");
		return mv;
		}
	
	
}
