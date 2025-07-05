package project.RnW.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.mongodb.MongoException;

import project.RnW.model.User;
import project.RnW.service.serviceComment;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceUser;

@Controller
public class userController {
	
	@RequestMapping("/register")
	public ModelAndView register(@RequestParam("email") String mail,
			@RequestParam("username") String name,
			@RequestParam("password") String password,
			@RequestParam("rPassword") String rPassword){
		
		ModelAndView mv = null;
		
		try {
		User u = serviceUser.register(mail, name, password, rPassword);
		mv = new ModelAndView("profile");
		mv.addObject("IS_OWNER", true);
		mv.addObject("IS_ADMIN", false);
		mv = ControllerUtils.setupUserPage(u, mv);
		}
		catch(IllegalArgumentException e){
			mv = new ModelAndView("login");
			mv.addObject("ERROR", e.getMessage());
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
		User u = null;
		ModelAndView mv = null;
		try {
			u = serviceUser.login(mail, pw);
			mv = new ModelAndView("profile");
			mv.addObject("IS_OWNER", true);
			mv.addObject("IS_ADMIN", u.isAdmin());
		} catch (IllegalArgumentException e) {
			mv = new ModelAndView("login");
			mv.addObject("ERROR", e.getMessage());
			return mv;
		}
		
		return ControllerUtils.setupUserPage(u, mv);
		}
	
	

	@RequestMapping(value = "/user", params = "ownerId")
	public static ModelAndView checkProfile(
			@RequestParam("userId") String userId,
			@RequestParam("ownerId") String ownerid) {
		User owner = serviceUser.getUser( (ownerid));
		User u = serviceUser.getUser( (userId));
		boolean isOwner = owner.isOwner(u);
		ModelAndView mv = new ModelAndView("profile");
		mv.addObject("IS_OWNER", isOwner);
		mv.addObject("IS_ADMIN", u.isAdmin());
		return ControllerUtils.setupUserPage(owner, mv);
	}
	
	@RequestMapping("/deleteUser")
	public ResponseEntity<String> deleteUser(
			@RequestParam("userId") String userId) {
		User u = serviceUser.getUser( (userId));
		ResponseEntity<String> response = null;
		try {
			serviceUser.delete(u);
			response = new ResponseEntity<>("Account cancellato!",
					HttpStatus.OK);
		}catch(MongoException e) {
			response = new ResponseEntity<>(
					e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		return response;
	}
	

	@RequestMapping("/adminView")
	public static ModelAndView adminView(
			@RequestParam("userId") String userId){
		ModelAndView mv = null;
		if(serviceUser.getUser( (userId)).isAdmin()) {
			mv = new ModelAndView("handleUsers");
			ObjectMapper mp = new ObjectMapper();
			try {
				mv.addObject("USERS", mp.writeValueAsString(ControllerUtils.getUsersIds()));
				mv.addObject("REPORTS", mp.writeValueAsString(
						serviceReport.getReports()));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			mv = ControllerUtils.home();
			mv.addObject("ERROR", 2);
		}
		return mv;
	}	
	
	
	@RequestMapping("/changePwd")
	@ResponseBody
	public ResponseEntity<String> changePassword(
			@RequestParam("userId") String userId,
			@RequestParam("pwd") String pwd,
			@RequestParam("newPwd") String newPwd
			) {
		ResponseEntity<String> response = null;
		User u = serviceUser.getUser(userId);
		try {
			serviceUser.changePassword(pwd, u);
		}catch(IllegalArgumentException e) {
			response = new ResponseEntity<>(
					e.getMessage(),
					HttpStatus.FORBIDDEN);
		}catch(MongoException e) {
			response = new ResponseEntity<>(
					e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	
	@RequestMapping("/changeName")
	@ResponseBody
	public ResponseEntity<String> changeName(
			@RequestParam("userId") String userId,
			@RequestParam("newName") String newName
			) {
		ResponseEntity<String> response = null;
		User u = serviceUser.getUser( userId);
		try {
			serviceUser.changeName(newName, u);
			response = new ResponseEntity<>("Nome utente cambiato!",
					HttpStatus.OK);}
		catch(MongoException e) {
			response = new ResponseEntity<>(
					e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		return response;
	}
	
	@RequestMapping("/userComment")
	public ModelAndView userComment(
			@RequestParam("userId") String userId,
			@RequestParam("commentId") String commentId,
			@RequestParam("textId") String textId) {
		
	    String ownerId =  serviceComment.returnOwnerId(textId, commentId);
	    ModelAndView mv = userController.checkProfile(userId, ownerId.toString());
		return mv;
	}
	
	@RequestMapping("/home")
	public static ModelAndView goToHome() {
		return ControllerUtils.home();
	}
}
