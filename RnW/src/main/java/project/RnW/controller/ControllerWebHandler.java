package project.RnW.controller;

import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.MongoException;

import project.RnW.service.serviceUser.InvalidIdException;

@ControllerAdvice(basePackages = "project.RnW.controller.ControllerWeb")
public class ControllerWebHandler {
	
	//This class handles exceptions that are common to controllers.

	@ExceptionHandler(MongoException.class)
	public ModelAndView handleMongoException(MongoException ex) {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("ERROR", "Errore del database");
		mv = ControllerUtils.setupHome(mv);
		return mv;
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ModelAndView handleAccountNotFoundException(AccountNotFoundException ex) {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("ERROR", "Impossibile trovare l'account");
		mv = ControllerUtils.setupHome(mv);
		return mv;
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ModelAndView handleNoSuchElementException(NoSuchElementException ex) {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("ERROR", "Impossibile trovare il testo");
		mv = ControllerUtils.setupHome(mv);
		return mv;
	}
	
	@ExceptionHandler(InvalidIdException.class)
	public ModelAndView handleArgumentException(InvalidIdException ex){
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("ERROR", "La richiesta non è valida, id non idonei");
		mv = ControllerUtils.setupHome(mv);
		return mv;
	}
	
	
}
