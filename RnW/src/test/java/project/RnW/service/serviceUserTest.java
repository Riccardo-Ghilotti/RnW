package project.RnW.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.naming.directory.AttributeInUseException;
import javax.security.auth.login.AccountLockedException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;

import project.RnW.mappers.mapperComment;
import project.RnW.mappers.mapperReport;
import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.Text;
import project.RnW.model.User;
import project.RnW.service.serviceUser.DifferentPasswordsException;
import project.RnW.service.serviceUser.NameUnchangedException;
import project.RnW.service.serviceUser.PasswordUnchangedException;
import project.RnW.service.serviceUser.UserDeletionException;

public class serviceUserTest {
	
	
	@Test
	public void testCreateUserErrorAccountCreationException() {
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
        	
        	utilities.when(() -> mapperUser.insert(
        			"test@example.com", "test", "pwdtest", false))
        	.thenReturn(null);
        	
        	assertThrows(AccountLockedException.class, () ->
        	serviceUser.createUser("test@example.com", "test", "pwdtest", false));
        }
	}
	
	@Test
	public void testCreateUserEmailInUseException() {
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
        	
        	utilities.when(() -> mapperUser.insert(
        			"test@example.com", "test", Hashing.sha256()
					.hashString("pwdtest", StandardCharsets.UTF_8).toString(), false))
        	.thenThrow(new MongoException(11000, "Duplicate Key"));
        	
        	assertThrows(AttributeInUseException.class, () ->
        	serviceUser.createUser("test@example.com", "test", "pwdtest", false));
        }
	}
	
	@Test
	public void testRegisterDifferentPwd() {
		assertThrows(DifferentPasswordsException.class, () ->
				serviceUser.register(
						"test@example.com", "test", "ciao", "mondo"));
	}
	
	@Test
	public void testChangePasswordExceptionDifferentPwd() {
		ObjectId userId = new ObjectId();
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
        	
        	utilities.when(() -> mapperUser.returnPassword(userId))
        	.thenReturn("test-err");
        	
        	assertThrows(IllegalArgumentException.class, () ->
        	serviceUser.changePassword("test", "test-new", new User(
        												userId,
        												"test",
        												false)));
        }
	}
	
	@Test
	public void testChangePasswordErrorUnchangedPassword() {
		ObjectId userId = new ObjectId();
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
        	
        	utilities.when(() -> mapperUser.returnPassword(userId))
        	.thenReturn( Hashing.sha256()
    				.hashString("test", StandardCharsets.UTF_8).toString());
        	utilities.when(() -> mapperUser.update(userId,null, "test"))
        	.thenReturn(false);
        	
        	assertThrows(PasswordUnchangedException.class, () ->
        	serviceUser.changePassword("test", "test-new", new User(
        												userId,
        												"test",
        												false)));
        }
	}
	
	
	@Test
	public void testChangeNameErrorMongo() {
		ObjectId userId = new ObjectId();
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
        	
        	utilities.when(() -> mapperUser.update(userId,"test1", null))
        	.thenReturn(false);
        	
        	assertThrows(NameUnchangedException.class, () ->
        	serviceUser.changeName("test1", new User(userId,
													"test",
    												false)));
        }
	}
	
	@Test
	public void testDeleteErrorMongo() {
		ObjectId userId = new ObjectId();
        try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)
        		; MockedStatic<mapperText> utilitiesT = Mockito.mockStatic(mapperText.class)
        		; MockedStatic<mapperComment> utilitiesC = Mockito.mockStatic(mapperComment.class)
        		; MockedStatic<mapperReport> utilitiesR = Mockito.mockStatic(mapperReport.class)) {
        	
        	utilities.when(() -> mapperText.getAllTextsFromAuthor(userId, false))
        	.thenReturn(new ArrayList<Text>());
        	utilitiesR.when(() -> mapperReport.deleteReportsOfUser(userId) ).thenReturn(true);
        	utilities.when(() -> mapperUser.delete(userId))
        	.thenReturn(false);
        	
        	assertThrows(UserDeletionException.class, () ->
        	serviceUser.delete(new User(userId, "test", false)));
        }
	}
	
}