package project.RnW.controller;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mongodb.MongoException;

import project.RnW.service.serviceUser.InvalidIdException;

@ControllerAdvice(basePackages = "project.RnW.controller.ControllerREST")
public class ControllerRESTHandler {
	//This class handles some common Exception thrown by the REST controllers.

	@ExceptionHandler(MongoException.class)
	public ResponseEntity<String> handleMongoException(MongoException ex) {
		return new ResponseEntity<>(
				"Errore nel database",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException ex) {
		return new ResponseEntity<>(
				"Impossibile trovare l'account",
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidIdException.class)
	public ResponseEntity<String> handleInvalidIdException(InvalidIdException e){
		return new ResponseEntity<>(
				"La richiesta è non valida, id non idonei",
				HttpStatus.BAD_REQUEST);
	}
	
}