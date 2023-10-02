package dylan.dahub.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {

    private final int ID;
    private final String author;
    private final String content;
    private final int likes;
    private final int shares;
    private final LocalDateTime dateTime;

    public Post(int ID, String author, String content, int likes, int shares, LocalDateTime dateTime) {
        this.ID = ID;
        this.author = author;
        this.content = content;
        this.likes = likes;
        this.shares = shares;
        this.dateTime = dateTime;

    }

    public String getDateTimeString() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    // Formats the number given to show the rounded representation. Used for likes and shares to be displayed
    // more cleanly. e.g. 14,765 likes would be shown as 14.8K likes.
    public static String formatNumber(int number) {
        try {
            if (number > 9999 && number < 999950) {
                return String.format("%.1fK", number / 1000.0);
            } else if (number >= 999950 && number < 999950000) {
                return String.format("%.1fM", number / 1000000.0);
            } else if (number >= 999950000) {
                return "1B+";
            }
            return String.valueOf(number);
        } catch (NumberFormatException e) {
            return "1B+";
        }
    }
    public int getID() {
        return ID;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
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
