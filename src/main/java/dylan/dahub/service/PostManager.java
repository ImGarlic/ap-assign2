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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class PostManager {
    private static final String TABLE_NAME = "Post";

    // Generate a post from UI text fields, used to ensure the post has the correct parameters before entering the database.
    // Post ID is auto-generated in the database, so the local ID doesn't matter until put.
    public static Post generatePostFromTextFields(String author, String content, String dateTimeString, String likeString, String shareString) throws InvalidPostException {
        try {
            LocalDateTime dateTime = convertDateTime(dateTimeString);
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

    // Puts 1 single post into the database. Post ID is auto-generated in the database so the local ID makes no difference.
    public static void put(int userID, Post post) throws InvalidPostException {
        long timeStamp = createTimeStamp(post.dateTime());

        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%d', '%d', '%d', '%d')",
                TABLE_NAME, post.author(), post.content(), post.likes(), post.shares(), timeStamp, userID);

        try (Connection con = DatabaseUtils.getConnection()){


            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Added post");
        } catch (SQLException e) {
            String message = String.format("Failed to create post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    // Gets multiple posts from the database based on SQL select queries.
    // sortType refers to the column to sort on, sortOrder is ascending/descending.
    public static ArrayList<Post> getMulti(int userID, int count, String searchClause, String sortType, String sortOrder, boolean onlyCurrentUser, int offset) throws InvalidPostException {
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


    public static void putMulti(int userID, ArrayList<Post> postList) throws InvalidPostException {
        StringBuilder query = new StringBuilder(String.format("INSERT INTO %s VALUES ", TABLE_NAME));
        for (Post post : postList) {
            long timeStamp = createTimeStamp(post.dateTime());
            query.append(String.format("(null, '%s', '%s', '%d', '%d', '%d', '%d'),",
                    post.author(), post.content(), post.likes(), post.shares(), timeStamp, userID));
        }
        query.deleteCharAt(query.lastIndexOf(","));
        System.out.println(query);

        try (Connection con = DatabaseUtils.getConnection()){

            Statement stmt = con.createStatement();
            stmt.executeUpdate(query.toString());

            System.out.println("Added posts");
        } catch (SQLException e) {
            String message = String.format("Failed to add post: %s", e.getMessage());
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

    public static void delete(int userID, int ID) throws InvalidPostException, UserAuthenticationException {
        String selQuery = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);
        String delQuery = String.format("DELETE FROM %s WHERE id='%d'", TABLE_NAME, ID);

        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(selQuery);

            if(resultSet.next()) {
                int userIDFromPost = resultSet.getInt("user");
                if (userIDFromPost != userID) {
                    throw new UserAuthenticationException("You can only delete your own posts");
                }
            } else {
                throw new InvalidPostException("Post ID does not exist");
            }

            stmt.executeUpdate(delQuery);

            System.out.println("Deleted post");
        } catch (SQLException e) {
            String message = String.format("Failed to delete post: %s", e.getMessage());
            throw new InvalidPostException(message);
        }
    }

    public static Post getRandomPost() throws InvalidPostException {
        String query = String.format("SELECT * FROM %s ORDER BY RANDOM() LIMIT 1", TABLE_NAME);

        return getPost(query);
    }

    private static Post getPost(String query) throws InvalidPostException {
        Post post;
        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            if(resultSet.next()) {
                LocalDateTime dateTime = createDateTime(resultSet.getLong("timestamp"));
                post = new Post(resultSet.getInt("id"), resultSet.getString("author"),
                        resultSet.getString("content"), resultSet.getInt("likes"),
                        resultSet.getInt("shares"), dateTime);
            } else {
                throw new InvalidPostException("Post ID does not exist");
            }

            con.close();
            return post;
        } catch (SQLException e) {
            String message = "Failed to get post from database: " + e.getMessage();
            Logger.alertError(message);
            throw new InvalidPostException(message);
        }
    }

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
                Post nextPost = convertFromCSV(scanner.nextLine());
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


    // Converts a single comma-separated string to a post. Expects all post values to be present
    // and of the correct type, otherwise throws an InvalidPostException.
    public static Post convertFromCSV(String importedPost) throws InvalidPostException {
        List<String> postValues = new ArrayList<>();
        int likes, shares;
        String content, author, dateTime;

        try (Scanner rowScanner = new Scanner(importedPost)) {
            String COMMA_DELIMITER = ",";
            rowScanner.useDelimiter(COMMA_DELIMITER);

            while(rowScanner.hasNext()) {
                postValues.add(rowScanner.next());
            }
            author = postValues.get(0);
            content = postValues.get(1);
            likes = Integer.parseInt(postValues.get(2));
            shares = Integer.parseInt(postValues.get(3));
            dateTime = postValues.get(4);

            return new Post(0, author, content, likes, shares, convertDateTime(dateTime));
        } catch (IndexOutOfBoundsException | NumberFormatException | InvalidDateException e) {
            throw new InvalidPostException(e.getMessage());
        }
    }

    private static long createTimeStamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("Z"));
    }

    private static LocalDateTime createDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("Z"));
    }

    // Converts the date-time string into a LocalDateTime to ensure it is of the correct format.
    public static LocalDateTime convertDateTime(String dateString) throws InvalidDateException {
        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

        try {
            dateTime = LocalDateTime.parse(dateString, formatter);
            return dateTime;
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getMessage());
        }
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
