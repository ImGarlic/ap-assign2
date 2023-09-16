package dylan.dahub.service;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.Post;
import dylan.dahub.model.User;
import dylan.dahub.view.Logger;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostManager {
    private static final String TABLE_NAME = "Post";

    public static Post getFromID(int ID) throws InvalidPostException {
        Post post;
        String query = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);

        try {
            Connection con = DatabaseUtil.getConnection();
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
            Logger.alertError(message);
        }

        throw new InvalidPostException("Something went wrong.");
    }

    public static Post put(User user, Post post) throws InvalidPostException {
        long timeStamp = createTimeStamp(post.getDateTime());

        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%d', '%d', '%d', '%d')",
                TABLE_NAME, post.getAuthor(), post.getContent(), post.getLikes(), post.getShares(), timeStamp, user.getID());

        try {
            Connection con = DatabaseUtil.getConnection();

            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Added post");
            int generatedID = DatabaseUtil.getLastID(con);
            con.close();

            return getFromID(generatedID);
        } catch (SQLException e) {
            String message = String.format("Failed to create post: %s", e.getMessage());
            Logger.alertError(message);
        }
        throw new InvalidPostException("Something went wrong.");
    }

    public static ArrayList<Post> getMulti(int count, String sort, int userID, boolean onlyCurrentUser, int offset) throws InvalidPostException {
        ArrayList<Post> collection = new ArrayList<Post>();
        String query;
        if(!onlyCurrentUser) {
            query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT '%d' OFFSET '%d' COLLATE NOCASE ", TABLE_NAME, sort, count, offset);
        } else {
            query = String.format("SELECT * FROM %s WHERE user='%d' ORDER BY %s DESC LIMIT '%d' OFFSET '%d' COLLATE NOCASE", TABLE_NAME, userID, sort, count, offset);
        }


        try {
            Connection con = DatabaseUtil.getConnection();
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
                collection.add(post);
            }

            for (Post post : collection) {
                System.out.println("post: " + post.getContent());
            }
            con.close();

            return collection;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            Logger.alertError(message);
        }

        throw new InvalidPostException("Something went wrong.");
    }


    private static long createTimeStamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("Z"));
    }

    private static LocalDateTime createDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.systemDefault());
    }
}
