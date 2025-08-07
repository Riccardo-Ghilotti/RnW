package project.RnW.model;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @Test
    public void testCommentConstructorsAndGetters() {
        ObjectId id = new ObjectId();
        User user = new User(new ObjectId(), "elvis", false);
        String content = "test";

        Comment comment = new Comment(id, user, content);

        assertEquals(id, comment.getId());
        assertEquals(user, comment.getUser());
        assertEquals(content, comment.getContent());
    }


}
