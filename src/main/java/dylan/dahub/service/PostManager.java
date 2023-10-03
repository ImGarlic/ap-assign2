package dylan.dahub.service;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.Post;
import dylan.dahub.model.Range;
import dylan.dahub.model.User;
import dylan.dahub.view.Logger;

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

        try (Connection con = DatabaseUtils.getConnection()){

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

        try (Connection con = DatabaseUtils.getConnection()){


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
    // sortType refers to the column to sort on, sortOrder is ascending/descending.
    public static ArrayList<Post> getMulti(int count, String sortType, String sortOrder, int userID, boolean onlyCurrentUser, int offset) throws InvalidPostException {
        ArrayList<Post> collection = new ArrayList<>();
        String query;
        if(!onlyCurrentUser) {
            query = String.format("SELECT * FROM %s ORDER BY %s %s LIMIT '%d' OFFSET '%d' COLLATE NOCASE ", TABLE_NAME, sortType, sortOrder, count, offset);
        } else {
            query = String.format("SELECT * FROM %s WHERE user='%d' ORDER BY %s %s LIMIT '%d' OFFSET '%d' COLLATE NOCASE", TABLE_NAME, userID, sortType, sortOrder, count, offset);
        }


        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
                collection.add(post);
            }

            con.close();

            return collection;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }

    }

    public static ArrayList<Post> getMultiWithSearch(int count, String searchClause, String sortType, String sortOrder, int userID, boolean onlyCurrentUser, int offset) throws InvalidPostException {
        ArrayList<Post> collection = new ArrayList<>();
        String query;
        if(!onlyCurrentUser) {
            query = String.format("SELECT * FROM %s WHERE author LIKE '%%%s%%' OR id LIKE '%%%s%%' ORDER BY %s %s LIMIT '%d' OFFSET '%d' COLLATE NOCASE ",
                    TABLE_NAME, searchClause, searchClause, sortType, sortOrder, count, offset);
        } else {
            query = String.format("SELECT * FROM %s WHERE (author LIKE '%%%s%%' OR id LIKE '%%%s%%') AND user='%d' ORDER BY %s %s LIMIT '%d' OFFSET '%d' COLLATE NOCASE ",
                    TABLE_NAME, searchClause, searchClause, userID, sortType, sortOrder, count, offset);
        }


        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
                collection.add(post);
            }

            con.close();

            return collection;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }

    }


    public static void putMulti(User user, ArrayList<Post> postList) throws InvalidPostException {

        StringBuilder query = new StringBuilder(String.format("INSERT INTO %s VALUES ", TABLE_NAME));
        for (Post post : postList) {
            long timeStamp = createTimeStamp(post.getDateTime());
            query.append(String.format("(null, '%s', '%s', '%d', '%d', '%d', '%d'),",
                    post.getAuthor(), post.getContent(), post.getLikes(), post.getShares(), timeStamp, user.getID()));
        }
        query.deleteCharAt(query.lastIndexOf(","));
        System.out.println(query);

        try (Connection con = DatabaseUtils.getConnection()){

            Statement stmt = con.createStatement();
            stmt.executeUpdate(query.toString());

            System.out.println("Added post");
            con.close();
        } catch (SQLException e) {
            String message = String.format("Failed to create post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    public static int getPostCount(int userID, boolean onlyCurrentUser, String sortType, Range range) throws InvalidPostException {
        String query = String.format("SELECT COUNT(*) FROM %s", TABLE_NAME);
        int count;
        if(onlyCurrentUser) {
            query += String.format(" WHERE user='%d'", userID);
            if (sortType.equals("likes")) {
                query += String.format(" AND likes<%d AND likes>%d", range.upperBound(), range.lowerBound());
            } else if (sortType.equals("shares")) {
                query += String.format(" AND shares<%d AND shares>%d", range.upperBound(), range.lowerBound());
            }
        } else {
            if (sortType.equals("likes")) {
                query += String.format(" WHERE likes<%d AND likes>%d", range.upperBound(), range.lowerBound());
            } else if (sortType.equals("shares")) {
                query += String.format(" WHERE shares<%d AND shares>%d", range.upperBound(), range.lowerBound());
            }
        }

        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            if(resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                return 0;
            }
            con.close();

            return count;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }
    }

    public static void delete(int ID, User user) throws InvalidPostException, UserAuthenticationException {
        String selQuery = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);
        String delQuery = String.format("DELETE FROM %s WHERE id='%d'", TABLE_NAME, ID);

        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(selQuery);

            if(resultSet.next()) {
                int userIDFromPost = resultSet.getInt("user");
                if (userIDFromPost != user.getID()) {
                    throw new UserAuthenticationException("You can only delete your own posts");
                }
            } else {
                throw new InvalidPostException("Post ID does not exist");
            }

            stmt.executeUpdate(delQuery);

            System.out.println("Deleted post");
            con.close();
        } catch (SQLException e) {
            String message = String.format("Failed to delete post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    public static Post getRandomPost() throws InvalidPostException {
        String query = String.format("SELECT * FROM %s ORDER BY RANDOM() LIMIT 1", TABLE_NAME);
        Post post;

        try (Connection con = DatabaseUtils.getConnection()){
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
            throw new InvalidPostException(message);
        }
    }

    private static long createTimeStamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("Z"));
    }

    private static LocalDateTime createDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("Z"));
    }
}
