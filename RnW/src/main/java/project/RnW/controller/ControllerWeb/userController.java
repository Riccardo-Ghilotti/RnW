package project.RnW.controller.ControllerWeb;


import javax.naming.directory.AttributeInUseException;
import javax.security.auth.login.AccountLockedException;
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
import project.RnW.mappers.mapperComment.CommentNotFoundException;
import project.RnW.model.User;
import project.RnW.service.serviceComment;
import project.RnW.service.serviceUser;
import project.RnW.service.serviceUser.DifferentPasswordsException;
import project.RnW.service.serviceUser.InvalidIdException;

@Controller
public class userController {
	
	//This class handles the same types of calls handled by textController.
	//However, the subject of said calls is a User or User related object.
	
	//this method handles user registration.
	@RequestMapping("/register")
	public ModelAndView register(@RequestParam("email") String mail,
			@RequestParam("username") String name,
			@RequestParam("password") String password,
			@RequestParam("rPassword") String rPassword) 
					throws AccountNotFoundException,
					AccountLockedException, MongoException, AttributeInUseException, 
					DifferentPasswordsException{
		
		ModelAndView mv = null;
		
		User u = serviceUser.register(mail, name, password, rPassword);
		mv = new ModelAndView("profile");
		mv.addObject("IS_OWNER", true);
		mv.addObject("IS_ADMIN", false);
		mv = ControllerUtils.setupUserPage(u, mv, true);
		
		return mv;
	}
	
	
	//this method loads the login page.
	@RequestMapping("/login")
	public ModelAndView loginLoad() {
		ModelAndView mv = new ModelAndView("login");
		return mv;
	}

	//this method allows users to log in using their credentials.
	@RequestMapping("/user")
	public ModelAndView login(
			@RequestParam(value = "email", required = true) String mail,
			@RequestParam(value = "password", required = true) String pw) 
					throws AccountNotFoundException {

		User u = null;
		ModelAndView mv = null;
		
		u = serviceUser.login(mail, pw);
		mv = new ModelAndView("profile");
		mv.addObject("IS_OWNER", true);
		mv.addObject("IS_ADMIN", u.isAdmin());
	
		return ControllerUtils.setupUserPage(u, mv, true);
		}
	
	

	//this method allows for users to check a user profiles.
	//whether it's theirs or some other user's, the returned object changes.
	@RequestMapping(value = "/user", params = "ownerId")
	public ModelAndView checkProfile(
			@RequestParam("userId") String userId,
			@RequestParam("ownerId") String ownerid) 
					throws AccountNotFoundException, InvalidIdException {
			
			
			User owner = serviceUser.getUser(ownerid);
			boolean isOwner = false;
			boolean isAdmin = false;
			try {
				User u = serviceUser.getUser(userId);
				isOwner = owner.isOwner(u);
				isAdmin = u.isAdmin();
			}catch(AccountNotFoundException | InvalidIdException e) {
				//This only triggers if the user isn't logged in
			}
			
			
			
			ModelAndView mv = new ModelAndView("profile");
			mv.addObject("IS_OWNER", isOwner);
			mv.addObject("IS_ADMIN", isAdmin);
			
			return ControllerUtils.setupUserPage(owner, mv, isOwner);
	}
	
	
	//this method loads the admin's control page, where they can see reports
	//and manage users.
	@RequestMapping("/adminView")
	public ModelAndView adminView(
			@RequestParam("userId") String userId) 
					throws AccountNotFoundException, 
					MongoException, InvalidIdException{
		ModelAndView mv = null;
		if(serviceUser.getUser(userId).isAdmin()) {
			mv = new ModelAndView("handleUsers");
			ObjectMapper mp = new ObjectMapper();
			try {
				mv.addObject("USERS", mp.writeValueAsString(
						ControllerUtils.getUsersIds()));
				mv.addObject("REPORTS", mp.writeValueAsString(
						ControllerUtils.getReports()));
			} catch (JsonProcessingException e) {
				//unless the code in getUsersIds and getReports gets modified, 
				//this cannot happen
				e.printStackTrace();
			}
		}
		else {
			mv = ControllerUtils.setupHome(new ModelAndView("home"));
			mv.addObject("ERROR", "Non hai accesso a questa parte dell'app");
		}
		return mv;
	}	
	
	
	//this method returns the user page of a commenter.
	@RequestMapping("/userComment")
	public ModelAndView userComment(
			@RequestParam("userId") String userId,
			@RequestParam("commentId") String commentId,
			@RequestParam("textId") String textId)
					throws AccountNotFoundException, MongoException, 
					CommentNotFoundException, InvalidIdException {
		
	    String ownerId =  serviceComment.returnOwnerId(textId, commentId);
	    ModelAndView mv = checkProfile(userId, ownerId.toString());
		return mv;
	}


	//this method returns the home page of the web application.
	@RequestMapping("/home")
	public ModelAndView goToHome() {
		return ControllerUtils.setupHome(new ModelAndView("home"));
	}


	
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex){
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Credenziali sbagliate");
		return mv;
	}
	
	@ExceptionHandler(AccountLockedException.class)
	public ModelAndView handleAccountLockedException(AccountLockedException ex){
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Errore nella creazione dell'utente");
		return mv;
	}
	
	@ExceptionHandler(AttributeInUseException.class)
	public ModelAndView handleAccountLockedException(AttributeInUseException ex){
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Mail già in uso");
		return mv;
	}
	
	
	@ExceptionHandler(DifferentPasswordsException.class)
	public ModelAndView handleDifferentPasswordsException(DifferentPasswordsException ex) {
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("ERROR", "Le due password inserite sono differenti");
		return mv;
	}
	
	
}
