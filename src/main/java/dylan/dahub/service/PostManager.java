package dylan.dahub.service;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.Post;
import dylan.dahub.model.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PostManager {
    private static final String TABLE_NAME = "Post";

    // Generate a post from UI text fields, used to ensure the post has the correct parameters before entering the database.
    public static Post generatePostFromTextFields(String author, String content, String dateTimeString, String likeString, String shareString) throws InvalidPostException {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
            int likes = Integer.parseInt(likeString);
            int shares = Integer.parseInt(shareString);

            if (content.length() > 120) {
                throw new InvalidPostException("Invalid post: Content must be less than 120 characters");
            }

            return new Post(1, author, content, likes, shares, dateTime);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPostException("Invalid post: " + e.getMessage());
        }
    }

    // Gets 1 single post from the database from it's ID.
    public static Post getFromID(int ID) throws InvalidPostException {
        Post post;
        String query = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);

        try {
            Connection con = DatabaseUtils.getConnection();
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            if(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
                con.close();
            } else {
                throw new InvalidPostException("Post ID does not exist");
            }

            return post;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }
    }

    // Puts 1 single post into the database. Post ID is auto-generated so the local ID makes no difference.
    public static void put(User user, Post post) throws InvalidPostException {
        long timeStamp = createTimeStamp(post.getDateTime());

        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%d', '%d', '%d', '%d')",
                TABLE_NAME, post.getAuthor(), post.getContent(), post.getLikes(), post.getShares(), timeStamp, user.getID());

        try {
            Connection con = DatabaseUtils.getConnection();

            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Added post");
            int generatedID = DatabaseUtils.getLastID(con);
            con.close();

            getFromID(generatedID);
        } catch (SQLException e) {
            String message = String.format("Failed to create post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    // Gets multiple posts from the database based on SQL select queries.
    public static ArrayList<Post> getMulti(int count, String sort, int userID, boolean onlyCurrentUser, int offset) throws InvalidPostException {
        ArrayList<Post> collection = new ArrayList<>();
        String query;
        if(!onlyCurrentUser) {
            query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT '%d' OFFSET '%d' COLLATE NOCASE ", TABLE_NAME, sort, count, offset);
        } else {
            query = String.format("SELECT * FROM %s WHERE user='%d' ORDER BY %s DESC LIMIT '%d' OFFSET '%d' COLLATE NOCASE", TABLE_NAME, userID, sort, count, offset);
        }


        try {
            Connection con = DatabaseUtils.getConnection();
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
                collection.add(post);
            }

//            for (Post post : collection) {
//                System.out.println("post: " + post.getContent());
//            }
            con.close();

            return collection;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }

    }


    private static long createTimeStamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("Z"));
    }

    private static LocalDateTime createDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.systemDefault());
    }
}
