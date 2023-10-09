package dylan.dahub.service;

import dylan.dahub.exception.InvalidDateException;
import dylan.dahub.exception.InvalidFileException;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.Post;
import dylan.dahub.model.Range;
import dylan.dahub.view.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.Scanner;

public class PostManager {
    private static final String TABLE_NAME = "Post";

    // Generate a post from UI text fields, used to ensure the post has the correct parameters before entering the database.
    // Post ID is auto-generated in the database, so the local ID doesn't matter until put.
    public static Post generatePostFromTextFields(String author, String content, String dateTimeString, String likeString, String shareString) throws InvalidPostException {
        try {
            LocalDateTime dateTime = Post.convertDateTime(dateTimeString);
            int likes = Integer.parseInt(likeString);
            int shares = Integer.parseInt(shareString);

            if (content.length() > 120) {
                throw new InvalidPostException("Invalid post: Content must be less than 120 characters");
            }

            return new Post(1, author, content, likes, shares, dateTime);
        } catch (NumberFormatException | InvalidDateException e) {
            throw new InvalidPostException("Invalid post: " + e.getMessage());
        }
    }

    // Gets multiple posts from the database based on SQL select queries.
    // sortType refers to the column to sort on, sortOrder is ASC/DESC.
    public static ArrayList<Post> getMulti(int userID, int count, String searchClause, String sortType, String sortOrder,
                                           boolean onlyCurrentUser, int offset) throws InvalidPostException {
        ArrayList<Post> collection = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE (author LIKE ? OR id LIKE ?) AND user LIKE ? " +
                                     "ORDER BY %s %s LIMIT ? OFFSET ? COLLATE NOCASE ",
                                     TABLE_NAME, sortType, sortOrder);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1,"%" + searchClause + "%");
            stmt.setString(2,"%" + searchClause + "%");
            if (onlyCurrentUser) {
                stmt.setInt(3, userID);
            } else {
                stmt.setString(3, "%");
            }
            stmt.setInt(4, count);
            stmt.setInt(5, offset);

            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()) {
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), createDateTime(resultSet.getLong("timestamp")));

                collection.add(post);
            }

            con.close();
            return collection;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }

    }

    // Gets the number of posts within a certain likes/shares range
    // sortType refers to the column to sort on
    public static int getPostCount(int userID, boolean onlyCurrentUser, String sortType, Range range) throws InvalidPostException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE user LIKE ? AND %s > ? AND %s < ?", TABLE_NAME, sortType, sortType);
        int count = 0;

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            if(onlyCurrentUser) {
                stmt.setInt(1, userID);
            } else {
                stmt.setString(1, "%");
            }
            stmt.setInt(2, range.lowerBound());
            stmt.setInt(3, range.upperBound());

            ResultSet resultSet = stmt.executeQuery();

            if(resultSet.next()) {
                count = resultSet.getInt(1);
            }

            con.close();
            return count;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            throw new InvalidPostException(message);
        }
    }

    // Gets a random post from the database
    public static Post getRandomPost() throws InvalidPostException, NullPointerException {
        String query = String.format("SELECT * FROM %s ORDER BY RANDOM() LIMIT 1", TABLE_NAME);

        Post post;
        try (Connection con = DatabaseUtils.getConnection();
             Statement stmt = con.createStatement()) {

            ResultSet resultSet = stmt.executeQuery(query);
            if(resultSet.next()) {
                post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), createDateTime(resultSet.getLong("timestamp")));
            } else {
                throw new NullPointerException();
            }

            con.close();
            return post;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            Logger.alertError(message);
            throw new InvalidPostException(message);
        }
    }

    // Puts 1 single post into the database. Post ID is auto-generated in the database so the local ID makes no difference.
    public static void put(int userID, Post post) throws InvalidPostException {
        String query = String.format("INSERT INTO %s VALUES (null, ?, ?, ?, ?, ?, ?)", TABLE_NAME);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            setPostToStatement(stmt, post, userID);
            stmt.executeUpdate();
            con.close();

            System.out.println("Added post");
        } catch (SQLException e) {
            String message = String.format("Failed to create post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    // Puts the given list into the database, capped to 1000
    public static void putMulti(int userID, ArrayList<Post> postList) throws InvalidPostException {
        String query = String.format("INSERT INTO %s VALUES (null, ?, ?, ?, ?, ?, ?)", TABLE_NAME);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            con.setAutoCommit(false);

            for (Post post : postList) {
                setPostToStatement(stmt, post, userID);
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
            con.close();

            System.out.println("Added posts");
        } catch (SQLException e) {
            String message = String.format("Failed to add post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    // Delete 1 post from the database. The reason we dont have a deleteMulti is since we have to check if the user
    // owns the post first, which could cause issues in a batch delete.
    public static void delete(int userID, int postID) throws InvalidPostException, UserAuthenticationException {
        String selectQuery = String.format("SELECT * FROM %s WHERE id = ? LIMIT 1", TABLE_NAME);
        String deleteQuery = String.format("DELETE FROM %s WHERE id= ? ", TABLE_NAME);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement selectStmt = con.prepareStatement(selectQuery);
             PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {

            selectStmt.setInt(1, postID);
            deleteStmt.setInt(1, postID);

            ResultSet resultSet = selectStmt.executeQuery();

            if(resultSet.next()) {
                int userIDFromPost = resultSet.getInt("user");
                if (userIDFromPost != userID) {
                    throw new UserAuthenticationException("You can only delete your own posts");
                }
            } else {
                throw new InvalidPostException("Post ID does not exist");
            }

            deleteStmt.executeUpdate();
            con.close();
            System.out.println("Deleted post");
        } catch (SQLException e) {
            String message = String.format("Failed to delete post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    // Reads a csv file to attempt to extract a list of posts from it. The file is considered invalid if
    // 1 or more rows has incorrect data
    public static ArrayList<Post> readCSV(File csv) throws InvalidFileException, NullPointerException {
        ArrayList<Post> postList = new ArrayList<>();
        int line = 1;

        try {
            if(!getExtension(csv.getName()).equals("csv")) {
                throw new InvalidFileException("File must be a CSV");
            }
            Scanner scanner = new Scanner(csv);
            scanner.nextLine(); // skip headers
            while (scanner.hasNextLine()) {
                line++;
                Post nextPost = Post.convertFromCSV(scanner.nextLine());
                postList.add(nextPost);
            }
            scanner.close();
        } catch (InvalidPostException e) {
            String message = String.format("File has incorrect data on line %d: %s", line, e.getMessage());
            throw new InvalidFileException(message);
        } catch (FileNotFoundException e) {
            throw new InvalidFileException(e.getMessage());
        }
        return postList;
    }

    private static void setPostToStatement(PreparedStatement stmt, Post post, int userID) throws SQLException {
        stmt.setString(1, post.author());
        stmt.setString(2, post.content());
        stmt.setInt(3, post.likes());
        stmt.setInt(4, post.shares());
        stmt.setLong(5, createTimeStamp(post.dateTime()));
        stmt.setInt(6, userID);
    }

    private static long createTimeStamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("Z"));
    }

    private static LocalDateTime createDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("Z"));
    }

    private static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }
}
