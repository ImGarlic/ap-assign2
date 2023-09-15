package dylan.dahub.model;

import dylan.dahub.service.PostManager;

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

    private Post(Post post) {
        this.ID = post.getID();
        this.author = post.getAuthor();
        this.content = post.getContent();
        this.likes = post.getLikes();
        this.shares = post.getShares();
        this.dateTime = post.getDateTime();
    }

    public String getDateTimeString() {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
    }

    public static String formatContent(String content) {
        StringBuilder formattedContent = new StringBuilder();
        int maxLength = 60;

        while(content.length() > 0){
            if(content.length() < maxLength){
                formattedContent.append(content);
                content = "";
            }
            else if(content.charAt(maxLength) == ' '){
                formattedContent.append(content.substring(0, maxLength + 1)).append("\n");
                content = content.substring(maxLength+1);
            }
            else{

                formattedContent.append(content.substring(0, content.lastIndexOf(' ', maxLength))).append("\n");
                content = content.substring(content.lastIndexOf(' ', maxLength) + 1);
            }
        }
        return formattedContent.toString();
    }

    public static String formatNumber(int number) {
        if (number > 9999 && number < 999999) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number > 999999 && number < 999999999) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number > 999999999) {
            return String.format("%.1fB", number / 1000000000.0);
        }
        return String.valueOf(number);
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
