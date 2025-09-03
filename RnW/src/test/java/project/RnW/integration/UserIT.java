package project.RnW.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.hash.Hashing;
import com.mongodb.MongoException;

import project.RnW.config.WebViewConfig;
import project.RnW.db.Database;
import project.RnW.mappers.mapperComment;
import project.RnW.mappers.mapperText;
import project.RnW.mappers.mapperUser;
import project.RnW.model.User;
import project.RnW.service.serviceUser;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WebViewConfig.class})
@WebAppConfiguration
public class UserIT {
	
	@Autowired
	private WebApplicationContext webAppContext;
	
	private MockMvc mockMvc;
	
	private ObjectId userId;
    private User authorTxt;
    private ObjectId textId;
    private ObjectId userId2;
    
    
    
    @BeforeEach
    public void setup() {
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webAppContext)
    			.build();
    	
    	userId = mapperUser.insert("test@example.com","name", Hashing.sha256()
				.hashString("test", StandardCharsets.UTF_8).toString(), false);

    	authorTxt = new User(userId, "name", false);
    	textId = mapperText.insert("title test",
    			new ArrayList<String>(), 
    			new ArrayList<String>(), 
    			new ArrayList<String>(), false, authorTxt);
    	userId2 = mapperUser.insert("test2@example.com","name", Hashing.sha256()
				.hashString("test", StandardCharsets.UTF_8).toString(), false);
    }
    
    
    @AfterEach
    public void cleanup() {
		mapperUser.delete(userId);
		mapperText.delete(textId);
		mapperUser.delete(userId2);
    }
	
	@Test
	public void testRegister() throws Exception {
		
		
		MvcResult result = mockMvc.perform(post("/register")
				.param("email", "testNew@example.com")
				.param("username", "test")
				.param("password", "test")
				.param("rPassword", "test"))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		
		ModelAndView userRegistered = result.getModelAndView();
		Map<String, Object> model = userRegistered.getModel();
		ObjectId userId = (ObjectId) model.get("ID");
		mapperUser.delete(userId);
		assertEquals(true, model.get("IS_OWNER"));
		assertEquals(false, model.get("IS_ADMIN"));
		assertEquals("test", model.get("NAME"));
		assertNotNull(model.get("ID"));
		assertTrue(model.get("TEXTS") == null || "null".equals(model.get("TEXTS"))); //Due to formatting it gives an error if assertNull is used
		
	}
	
	//if this test works, all MongoExceptions thrown calling an API in textController
	//and userController will be handled
	@Test
	public void testRegisterDBException() throws Exception {
		
		try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
			utilities.when(() ->mapperUser.insert("testNew@example.com", "test", Hashing.sha256()
					.hashString("test", StandardCharsets.UTF_8).toString(), false))
			.thenThrow(MongoException.class);	

			MvcResult result = mockMvc.perform(post("/register")
					.param("email", "testNew@example.com")
					.param("username", "test")
					.param("password", "test")
					.param("rPassword", "test"))
					.andExpect(status().isOk())
					.andExpect(view().name("login")).andReturn();
	    	
			ModelAndView userRegistered = result.getModelAndView();
			Map<String, Object> model = userRegistered.getModel();
			
			assertEquals("Errore del database", model.get("ERROR"));
		
		}
	}
	
	@Test
	public void testRegisterDifferentPwd() throws Exception {
		MvcResult result = mockMvc.perform(post("/register")
				.param("email", "testNew@example.com")
				.param("username", "test")
				.param("password", "test")
				.param("rPassword", "test-err"))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView userRegistered = result.getModelAndView();
		Map<String, Object> model = userRegistered.getModel();
		
		
		assertEquals("Le due password inserite sono differenti", model.get("ERROR"));
	}

	@Test
	public void testRegisterAlreadyExists() throws Exception {
		MvcResult result = mockMvc.perform(post("/register")
				.param("email", "test@example.com")
				.param("username", "test")
				.param("password", "test")
				.param("rPassword", "test"))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView userRegistered = result.getModelAndView();
		Map<String, Object> model = userRegistered.getModel();
		
		
		assertEquals("Mail già in uso", model.get("ERROR"));
	}

	@Test
	public void testLogin() throws Exception {
		MvcResult result = mockMvc.perform(post("/user")
				.param("email", "test@example.com")
				.param("password", "test"))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		
		
		assertEquals(true, model.get("IS_OWNER"));
		assertEquals(false, model.get("IS_ADMIN"));
	}
	
	@Test
	public void testLoginIncorrectPwd() throws Exception {
		MvcResult result = mockMvc.perform(post("/user")
				.param("email", "test@example.com")
				.param("password", "test-err"))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		
		mapperUser.delete(userId);
		assertEquals("Credenziali sbagliate", model.get("ERROR"));
	}
	
	@Test
	public void testCheckProfile() throws Exception {
		MvcResult result = mockMvc.perform(post("/user")
				.param("userId", userId.toString())
				.param("ownerId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
	
		assertEquals(true, model.get("IS_OWNER"));
		assertEquals(false, model.get("IS_ADMIN"));
		//model values that are assigned in stupUserPage were already checked in another test
		
	}


	@Test
	public void testCheckProfileDifferentUser() throws Exception {
		MvcResult result = mockMvc.perform(post("/user")
				.param("userId", userId2.toString())
				.param("ownerId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
	
		assertEquals(false, model.get("IS_OWNER"));
		assertEquals(false, model.get("IS_ADMIN"));
		//model values that are assigned in stupUserPage were already checked in another test
		
	}

	@Test
	public void testCheckProfileMalformedIdException() throws Exception{
		MvcResult result = mockMvc.perform(post("/user")
				.param("userId", userId.toString())
				.param("ownerId", "wrong id"))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
	
		assertEquals("La richiesta non è valida, id non idonei", model.get("ERROR"));
	}

	@Test
	public void testCheckProfileNotFoundException() throws Exception {
		MvcResult result = mockMvc.perform(post("/user")
				.param("userId", userId.toString())
				.param("ownerId", new ObjectId().toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		
		assertEquals("Impossibile trovare l'account", model.get("ERROR"));
	}


	@Test
	public void testAdminView() throws Exception {
		//the temporary creation of a dummy admin is made this way since, for
		//security reasons, it's not possible to create an admin through the app.
		Document docAdmin = new Document("name", "adminTemp")
				.append("mail", "amdinTemp@example.com")
				.append("password", "test")
				.append("admin", true);
		String adminId = Database.users.insertOne(docAdmin).getInsertedId().asObjectId().getValue().toString();
		try {
			MvcResult result = mockMvc.perform(post("/adminView")
					.param("userId", adminId))
					.andExpect(status().isOk())
					.andExpect(view().name("handleUsers")).andReturn();
			
			ModelAndView mv = result.getModelAndView();
			Map<String, Object> model = mv.getModel();
	
			assertNotNull(model.get("USERS"));
			assertNotNull(model.get("REPORTS"));
		}
		finally {
			mapperUser.delete(new ObjectId(adminId));
		}
	}
	
	
	@Test
	public void testAdminViewNotAdmin() throws Exception {
		
		MvcResult result = mockMvc.perform(post("/adminView")
				.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		
		assertEquals("Non hai accesso a questa parte dell'app", model.get("ERROR"));

	}
	
	
	@Test
	public void testAdminViewMissingUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/adminView")
				.param("userId", new ObjectId().toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("login")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		
		assertEquals(mv.getViewName(), "login");
		assertEquals("Impossibile trovare l'account", model.get("ERROR"));
	}
	
	@Test
	public void testUserComment() throws Exception {
		ObjectId comment = mapperComment.insert(userId, textId, "Test Commento");
		MvcResult result = mockMvc.perform(post("/userComment")
				.param("userId", userId.toString())
				.param("commentId", comment.toString())
				.param("textId", textId.toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		ModelAndView mv = result.getModelAndView();
		//if IS_OWNER is true, then the methods involved in the process return the correct object
		// and checkProfile has already been tested
		assertEquals(true, mv.getModel().get("IS_OWNER"));
	}
	
	@Test 
	public void testUserCommentNotOwner() throws Exception {
		ObjectId comment = mapperComment.insert(userId, textId, "Test Commento");
		
		MvcResult result = mockMvc.perform(post("/userComment")
				.param("userId", userId2.toString())
				.param("commentId", comment.toString())
				.param("textId", textId.toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("profile")).andReturn();
		
		ModelAndView mv = result.getModelAndView();
		Map<String, Object> model = mv.getModel();
		//same as above, but IS_OWNER must be false.
		assertEquals(model.get("IS_OWNER"), false);
		
	}


	@Test
	public void testDeleteUser() throws Exception {
		MvcResult result = mockMvc.perform(post("/deleteUser")
				.param("userId", userId.toString()))
				.andExpect(status().isOk()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		assertEquals("Account cancellato!", responseContent);
		assertThrows(AccountNotFoundException.class,() -> serviceUser.getUser(userId));
	}
	
	@Test
	public void testDeleteUserMissingUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/deleteUser")
				.param("userId", new ObjectId().toString()))
				.andExpect(status().isNotFound()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		assertEquals("Impossibile trovare l'account", responseContent);
		
	}
	
	
	@Test
	public void testDeleteUserUserNotDeletedException() throws Exception {
		
		try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
			utilities.when(() -> mapperUser.getUser(userId))
			.thenReturn(new User(userId, "test", false));
			utilities.when(() -> mapperUser.delete(userId))
	        .thenReturn(false);
			
			MvcResult result = mockMvc.perform(post("/deleteUser")
					.param("userId", userId.toString()))
					.andExpect(status().isInternalServerError()).andReturn();
			
			String responseContent = result.getResponse().getContentAsString();
		
			assertEquals("Impossibile cancellare l'utente", responseContent);
		}
	}
	
	@Test
	public void testChangePassword() throws Exception {
		MvcResult result = mockMvc.perform(post("/changePwd")
				.param("userId", userId.toString())
				.param("pwd", "test")
				.param("newPwd", "test-new"))
				.andExpect(status().isOk()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		
		assertEquals("Password cambiata", responseContent);
		assertThrows(IllegalArgumentException.class,() -> 
			serviceUser.login("test@example.com", "test"));

	}
	
	@Test
	public void testChangePasswordWrongPwd() throws Exception {
		MvcResult result = mockMvc.perform(post("/changePwd")
				.param("userId", userId.toString())
				.param("pwd", "test-err")
				.param("newPwd", "test-new"))
				.andExpect(status().isForbidden()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		assertEquals("Password precedente non valida", responseContent);
		
	}
	
	@Test
	public void testChangePasswordMissingUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/changePwd")
				.param("userId", new ObjectId().toString())
				.param("pwd", "test")
				.param("newPwd", "test-new"))
				.andExpect(status().isNotFound()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		
		assertEquals("Impossibile trovare l'account", responseContent);
	}
	
	@Test
	public void testChangePasswordUnchangedException() throws Exception {
		
		try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
			utilities.when(() -> mapperUser.getUser(userId))
			.thenReturn(new User(userId, "test", false));
			utilities.when(() -> mapperUser.returnPassword(userId))
			.thenReturn(Hashing.sha256()
					.hashString("test", StandardCharsets.UTF_8).toString());
			utilities.when(() -> mapperUser.update(userId, null, Hashing.sha256()
					.hashString("test-new", StandardCharsets.UTF_8).toString()))
	        .thenReturn(false);
			
			MvcResult result = mockMvc.perform(post("/changePwd")
					.param("userId", userId.toString())
					.param("pwd", "test")
					.param("newPwd", "test-new"))
					.andExpect(status().isInternalServerError()).andReturn();
			
			String responseContent = result.getResponse().getContentAsString();
		
			assertEquals("Impossibile cambiare la password", responseContent);
		}
	}
	
	@Test
	public void testChangeName() throws Exception {
		MvcResult result = mockMvc.perform(post("/changeName")
				.param("userId", userId.toString())
				.param("newName", "test-new"))
				.andExpect(status().isOk()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		
		assertEquals("Nome utente cambiato!", responseContent);
		assertEquals(serviceUser.getUser(userId).getName(), "test-new");
	}
	
	@Test
	public void testChangeNameMissingUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/changeName")
				.param("userId", new ObjectId().toString())
				.param("newName", "test-new"))
				.andExpect(status().isNotFound()).andReturn();
		
		String responseContent = result.getResponse().getContentAsString();
		
		
		assertEquals("Impossibile trovare l'account", responseContent);
	}
	
	@Test
	public void testChangeNameUnchangedException() throws Exception {
		
		try (MockedStatic<mapperUser> utilities = Mockito.mockStatic(mapperUser.class)) {
			utilities.when(() -> mapperUser.getUser(userId))
			.thenReturn(new User(userId, "test", false));
			utilities.when(() -> mapperUser.update(userId, "test", null))
	        .thenReturn(false);
			
			MvcResult result = mockMvc.perform(post("/changeName")
					.param("userId", userId.toString())
					.param("newName", "test"))
					.andExpect(status().isInternalServerError()).andReturn();
			
			String responseContent = result.getResponse().getContentAsString();
		
			assertEquals("Impossibile cambiare il nome", responseContent);
		}
	}
}
