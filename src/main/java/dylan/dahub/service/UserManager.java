package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.User;
import dylan.dahub.view.Logger;

import java.sql.*;

public class UserManager {
    private static final String TABLE_NAME = "User";

    // Checks to see if a user exists with the given username
    public static boolean userExists(String username) {
        try {
            getFromUsername(username);
        } catch (InvalidUserException e) {
            return false;
        }
        return true;
    }

    // Gets a single user from the database from the given username
    public static User getFromUsername(String username) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE user_name='%s' COLLATE NOCASE LIMIT 1", TABLE_NAME, username);

        return get(query);
    }

    // Gets a single user from the database from the given ID
    public static User getFromID(int ID) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);

        return get(query);
    }

    private static User get(String query) throws InvalidUserException {
        User user;

        try (Connection con = DatabaseUtils.getConnection()){
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                user = new User(resultSet.getInt("id"), resultSet.getString("user_name"),
                        resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("password"), resultSet.getInt("VIP"));
            } else {
                throw new InvalidUserException("Username does not exist");
            }

            con.close();
            return user;
        } catch (SQLException e) {
            Logger.alertError("Failed to get user from database: " + e.getMessage());
            throw new InvalidUserException("");
        }
    }

    // Puts 1 single user into the database. User ID is auto-generated so the local ID makes no difference.
    public static User put(User user) throws InvalidUserException {
        String query = String.format("INSERT INTO %s VALUES (null, ?, ?, ?, ?, 0)", TABLE_NAME);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            if (userExists(user.getUserName())) {
                throw new InvalidUserException("Username already exists");
            }
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getPassword());

            stmt.executeUpdate();
            System.out.println("Added user");
            int generatedID = DatabaseUtils.getLastID(con);

            con.close();
            return getFromID(generatedID);
        } catch (SQLException e) {
            Logger.alertError(String.format("Failed to create user: %s", e.getMessage()));
            throw new InvalidUserException("");
        }
    }

    // Updates all parameters of the user.
    public static User update(User user) throws InvalidUserException {
        String query = String.format("UPDATE %s SET user_name = ?, " +
                        "first_name = ?, " +
                        "last_name = ?, " +
                        "password = ?, " +
                        "VIP = ? " +
                        "WHERE id = ?",
                TABLE_NAME);

        try (Connection con = DatabaseUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            if (userExists(user.getUserName()) &&
                    getFromUsername(user.getUserName()).getID() != user.getID()) {
                throw new InvalidUserException("Username already exists");
            }

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getPassword());
            stmt.setInt(5,user.getVIP());
            stmt.setInt(6, user.getID());
            stmt.executeUpdate();

            System.out.println("Updated user");
            con.close();
            return getFromID(user.getID());
        } catch (SQLException e) {
            Logger.alertError(String.format("Failed to update user: %s", e.getMessage()));
            throw new InvalidUserException("");
        }
    }

    // Delete the user. Since SQLite doesn't like foreign keys we need to set the pragma every time
    // to allow cascade delete of posts
    public static void delete(User user) throws InvalidUserException {
        String pragma = "PRAGMA foreign_keys = ON";
        String query = String.format("DELETE FROM %s WHERE id= '%s' ", TABLE_NAME, user.getID());

        try {
            Connection con = DatabaseUtils.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute(pragma);
            stmt.execute(query);
            con.close();
        } catch (SQLException e) {
            String message = String.format("Failed to delete user: %s", e.getMessage());
            throw new InvalidUserException(message);
        }
    }

    // Return a random user from the database.
    public static User getRandomUser() throws InvalidUserException {
        String query = String.format("SELECT * FROM %s ORDER BY RANDOM() LIMIT 1", TABLE_NAME);

        return get(query);
    }

}
