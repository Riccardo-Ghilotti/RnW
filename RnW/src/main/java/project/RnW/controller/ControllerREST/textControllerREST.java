		package project.RnW.controller.ControllerREST;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoException;

import project.RnW.mappers.mapperComment.CommentNotFoundException;
import project.RnW.model.Comment;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceComment;
import project.RnW.service.serviceComment.CommentDeletionException;
import project.RnW.service.serviceComment.CommentUnsavedException;
import project.RnW.service.serviceReport;
import project.RnW.service.serviceText;
import project.RnW.service.serviceText.ChangeVisibilityException;
import project.RnW.service.serviceUser;
import project.RnW.service.serviceUser.InvalidIdException;
import project.RnW.service.serviceReport.ReportResolveException;
import project.RnW.service.serviceReport.ReportStoreException;

@RestController
	public class textControllerREST {
	
	//this class handles asyncronus REST calls made by the view. 
	//In particular, it focuses on operations that have a Text object as a subject
	//or some Text related object.
	
	//this method is called when someone wants to comment a text
		@RequestMapping(value = "/comment", params = "content")
		public ResponseEntity<String> comment(
				@RequestParam("textId") String textId,
				@RequestParam("userId") String userId,
				@RequestParam("content") String content) 
						throws MongoException, 
						CommentUnsavedException, InvalidIdException {
			    
			String commId = serviceComment.saveComment(userId, textId, content).toString();
			return new ResponseEntity<>("Commento aggiunto!#" + commId,
				HttpStatus.OK);
		}

		@RequestMapping("/reportText")
		public ResponseEntity<String> reportText(
				@RequestParam("idReported") String idReported,
				@RequestParam("report") String report,
				@RequestParam("idReporter") String idReporter) 
						throws 
						AccountNotFoundException, 
						ReportStoreException, InvalidIdException {
			Text t = serviceText.getText(idReported);
			User u = serviceUser.getUser(idReporter);
			serviceReport.sendReport(t, u, report);
			return new ResponseEntity<>("Segnalazione effettuata con successo",
					HttpStatus.OK);
		}
		
		
		//this method can be called to change the visibility of a text from public
		//to private or vice versa.
		@RequestMapping("/changeVisibility")
		public ResponseEntity<String> changeVisibility(
				@RequestParam("userId") String userId,
				@RequestParam("textId") String textId) 
						throws AccountNotFoundException, 
						MongoException, 
						ChangeVisibilityException, AccessDeniedException, 
						InvalidIdException {
			Text t = serviceText.getText(textId);
			serviceText.changeVisibility(t, serviceUser.getUser(userId));
			ResponseEntity<String> response = new ResponseEntity<>("Visibilità del testo cambiata!",
					HttpStatus.OK);
			return response;
		}
		
		
		
		//this method can be called to remove a report from the database.
		@RequestMapping("/resolveReport")
		public ResponseEntity<String> resolveReport(
				@RequestParam("reportId") String idReport) throws ReportResolveException, MongoException, InvalidIdException {
			serviceReport.resolveReport(idReport);
			return new ResponseEntity<>("Segnalazione risolta!",
					HttpStatus.OK);
		}
		
		
		
		@RequestMapping("/deleteComment")
		public ResponseEntity<String> deleteComment(
				@RequestParam("commentId") String commentId,
				@RequestParam("textId") String textId) 
						throws AccountNotFoundException, 
						MongoException, 
						CommentDeletionException, CommentNotFoundException, InvalidIdException {
			Comment c = serviceComment.getComment(
					textId,
					commentId);
			serviceComment.deleteComment(textId, c);
			return new ResponseEntity<>("Commento rimosso!",
					HttpStatus.OK);				
		}
		
		@ExceptionHandler(NoSuchElementException.class)
		public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex){
			return new ResponseEntity<>(
					"Impossibile trovare il testo",
					HttpStatus.NOT_FOUND);
		}
		
		@ExceptionHandler(ReportStoreException.class)
		public ResponseEntity<String> handleReportStoreException(ReportStoreException ex){
			return new ResponseEntity<>(
					"Impossibile salvare la segnalazione",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(ReportResolveException.class)
		public ResponseEntity<String> handleReportStoreException(ReportResolveException ex){
			return new ResponseEntity<>(
					"Impossibile risolvere la segnalazione",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(AccessDeniedException.class)
		public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex){
			return new ResponseEntity<>(
					"Non hai i permessi per compiere questa azione",
					HttpStatus.FORBIDDEN);
		}
		
		
		@ExceptionHandler(CommentUnsavedException.class)
		public ResponseEntity<String> handleCommentUnsavedException(CommentUnsavedException ex){
			return new ResponseEntity<>(
					"Il commento non è stato salvato, riprova",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		@ExceptionHandler(CommentDeletionException.class)
		public ResponseEntity<String> handleCommentDeletionException(CommentDeletionException ex){
			return new ResponseEntity<>(
					"Il commento non è stato cancellato, riprova",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		
		@ExceptionHandler(ChangeVisibilityException.class)
		public ResponseEntity<String> handleChangeVisibilityException(ChangeVisibilityException ex) {
			return new ResponseEntity<>(
					"Impossibile cambiare la visibilità del testo",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		@ExceptionHandler(CommentNotFoundException.class)
		public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex){
			return new ResponseEntity<>("Impossibile trovare il commento",
					HttpStatus.NOT_FOUND);
		}
}
