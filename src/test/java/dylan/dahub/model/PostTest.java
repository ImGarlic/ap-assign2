package dylan.dahub.model;

import dylan.dahub.exception.InvalidDateException;
import dylan.dahub.exception.InvalidPostException;
import javafx.geometry.Pos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PostTest {
    Post post = new Post(1, "author1", "content1", 0, 0, LocalDateTime.MIN);

    @Test
    public void getDateTimeString_Success() {
        Assertions.assertEquals(
                "01/01/-999999999 00:00",
                post.getDateTimeString()
        );
    }

    @Test
    public void formatNumber_Success_All() {
        Assertions.assertEquals(
                "17",
                Post.formatNumber(17)
        );
        Assertions.assertEquals(
                "13.4K",
                Post.formatNumber(13400)
        );
        Assertions.assertEquals(
                "1.3M",
                Post.formatNumber(1340000)
        );
        Assertions.assertEquals(
                "1B+",
                Post.formatNumber(Integer.MAX_VALUE)
        );
    }

    @Test
    public void toString_Success() {
        Assertions.assertEquals(
                "1,author1,content1,0,0,01/01/-999999999 00:00",
                post.toString()
        );
    }

    @Test
    public void convertFromCSV_Success() throws InvalidPostException {
        Post convertedPost = Post.convertFromCSV(
                "author1,content1,0,0,01/01/-999999999 00:00"
        );
        Assertions.assertEquals(
                "author1",
                convertedPost.author()
        );
        Assertions.assertEquals(
                "content1",
                convertedPost.content()
        );
        Assertions.assertEquals(
                0,
                convertedPost.likes()
        );
        Assertions.assertEquals(
                0,
                convertedPost.shares()
        );
        Assertions.assertEquals(
                LocalDateTime.MIN,
                convertedPost.dateTime()
        );
    }

    @Test
    public void convertFromCSV_Fail_All() {
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> Post.convertFromCSV("invalid number of arguments")
        );
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> Post.convertFromCSV("author1,content1,0,0,invalid date")
        );
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> Post.convertFromCSV("author1,content1,invalid likes,0,01/01/-999999999 00:00")
        );
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> Post.convertFromCSV("author1,content1,0,invalid shares,01/32/-999999999 00:00")
        );
    }

    @Test
    public void convertDateTime_Success() throws InvalidDateException {
        Assertions.assertEquals(
                LocalDateTime.MIN,
                Post.convertDateTime("01/01/-999999999 00:00")
        );
    }

    @Test
    public void convertDateTime_Fail() {
        Assertions.assertThrows(
                InvalidDateException.class,
                () -> Post.convertDateTime("not a valid date")
        );
    }
}
