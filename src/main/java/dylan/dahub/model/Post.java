package dylan.dahub.model;

import java.time.LocalDateTime;

public class Post {

    private int ID;
    private String content;
    private String author;
    private int likes;
    private int shares;
    private LocalDateTime dateTime;


    public int getID() {
        return ID;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getLikes() {
        return likes;
    }

    public int getShares() {
        return shares;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
