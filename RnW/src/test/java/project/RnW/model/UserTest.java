package project.RnW.model;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        ObjectId id = new ObjectId();
        String name = "elvis";
        
        User user = new User(id, "elvis", true);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertTrue(user.isAdmin());
        }


    @Test
    public void testEquals() {
        ObjectId id = new ObjectId();
        User user1 = new User(id, "elvis", false);
        User user2 = new User(id, "elvis", false); 

        assertTrue(user1.equals(user2));
    }

    @Test
    public void testIsOwner() {
        User user = new User(new ObjectId(), "elvis", false);

        assertTrue(user.isOwner(user));
    }
    
}
