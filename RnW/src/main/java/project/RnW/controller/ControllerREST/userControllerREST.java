package project.RnW.controller.ControllerREST;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoException;

import project.RnW.model.User;
import project.RnW.service.serviceText.TextNotDeletedException;
import project.RnW.service.serviceUser;
import project.RnW.service.serviceUser.InvalidIdException;
import project.RnW.service.serviceUser.NameUnchangedException;
import project.RnW.service.serviceUser.PasswordUnchangedException;
import project.RnW.service.serviceUser.UserDeletionException;

@RestController
public class userControllerREST {
		
		//same as textControllerRest, but this time the subject is a User object,
		//or a User related object.
	
		//this method allows users to delete their accounts,
		//it also allows administrators to delete other users' accounts.
		@RequestMapping("/deleteUser")
		public ResponseEntity<String> deleteUser(
				@RequestParam("userId") String userId) 
						throws AccountNotFoundException, 
						MongoException, UserDeletionException, 
						TextNotDeletedException, InvalidIdException {
		
			ResponseEntity<String> response = null;
		
			User u = serviceUser.getUser(userId);
			serviceUser.delete(u);
			response = new ResponseEntity<>("Account cancellato!",
					HttpStatus.OK);
				
			return response;
		}


		//this method allows users to change their password.
		@RequestMapping("/changePwd")
		public ResponseEntity<String> changePassword(
				@RequestParam("userId") String userId,
				@RequestParam("pwd") String pwd,
				@RequestParam("newPwd") String newPwd
				) throws AccountNotFoundException, PasswordUnchangedException, 
							InvalidIdException {
			ResponseEntity<String> response = null;
			
			User u = serviceUser.getUser(userId);
			serviceUser.changePassword(pwd, newPwd, u);
			response = new ResponseEntity<>(
					"Password cambiata",
					HttpStatus.OK);
			
			return response;
		}
		
		//this method allows users to change their name.
		//this method is also called when admins want to change another user's name.
		@RequestMapping("/changeName")
		public ResponseEntity<String> changeName(
				@RequestParam("userId") String userId,
				@RequestParam("newName") String newName
				) throws AccountNotFoundException, MongoException, 
						NameUnchangedException, InvalidIdException {
			ResponseEntity<String> response = null;
			User u = serviceUser.getUser(userId);
			serviceUser.changeName(newName, u);
			response = new ResponseEntity<>("Nome utente cambiato!",
					HttpStatus.OK);
				
			return response;
		}
		
		
		@ExceptionHandler(NameUnchangedException.class)
		public ResponseEntity<String> handleNameUnchangedException(NameUnchangedException ex){
			return new ResponseEntity<>(
					"Impossibile cambiare il nome",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(PasswordUnchangedException.class)
		public ResponseEntity<String> handlePasswordUnchangedException(PasswordUnchangedException ex){
			return new ResponseEntity<>(
					"Impossibile cambiare la password",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		@ExceptionHandler(UserDeletionException.class)
		public ResponseEntity<String> handleUserDeletionException(UserDeletionException ex){
			return new ResponseEntity<>(
					"Impossibile cancellare l'utente",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(TextNotDeletedException.class)
		public ResponseEntity<String> handleTextNotDeletedException(TextNotDeletedException ex){
			return new ResponseEntity<>(
					"Impossibile cancellare uno dei testi dell'utente",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(IllegalArgumentException.class)
		public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
			return new ResponseEntity<>(
					"Password precedente non valida",
					HttpStatus.FORBIDDEN);
		}
}
