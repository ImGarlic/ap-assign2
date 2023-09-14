package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.User;
import dylan.dahub.view.ErrorDisplay;

import java.sql.*;

public class UserManagement {
    private static final String TABLE_NAME = "User";

    public static boolean userExists(String username) {
        try {
            getUserFromUsername(username);
        } catch (InvalidUserException e) {
            return false;
        }
        return true;
    }

    public static User getUserFromUsername(String username) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE user_name='%s' COLLATE NOCASE LIMIT 1", TABLE_NAME, username);

        return getUser(query);
    }

    public static User getUserFromID(int ID) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);

        return getUser(query);
    }

    private static User getUser(String query) throws InvalidUserException {
        User user = new User();

        try {
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            user = new User(resultSet.getInt("id"), resultSet.getString("user_name"),
                    resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getString("password"), resultSet.getInt("VIP"));
            con.close();

            if (user.getUserName() == null) {
                throw new InvalidUserException("Username does not exist");
            }

            return user;
        } catch (SQLException e) {
            String message = "Failed to get user from database: " + e.getMessage();
            System.out.println(message);
            ErrorDisplay.alertError(message);
        }

        return user;
    }

    public static User putUser(User user) throws InvalidUserException {
        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%s', '%s', 0)",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword());

        try {
            Connection con = DatabaseConnection.getConnection();

            if (userExists(user.getUserName())) {
                throw new InvalidUserException("Username already exists");
            }
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Added user");
            con.close();
            return getUserFromUsername(user.getUserName());
        } catch (SQLException e) {
            String message = String.format("Failed to create user: %s", e.getMessage());
            System.out.println(message);
            ErrorDisplay.alertError(message);
        }
        return user;
    }

    public static User updateUser(User user) throws InvalidUserException {
        String query = String.format("UPDATE %s SET user_name = '%s', " +
                        "first_name = '%s', " +
                        "last_name = '%s', " +
                        "password = '%s', " +
                        "VIP = %d " +
                        "WHERE id = '%d'",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(),
                user.getPassword(), user.getVIP(), user.getID());

        try {
            Connection con = DatabaseConnection.getConnection();

            if (userExists(user.getUserName()) &&
                    getUserFromUsername(user.getUserName()).getID() != user.getID()) {
                throw new InvalidUserException("Username already exists");
            }
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Updated user");
            con.close();
            return getUserFromID(user.getID());
        } catch (SQLException e) {
            String message = String.format("Failed to update user: %s", e.getMessage());
            System.out.println(message);
            ErrorDisplay.alertError(message);
        }
        return null;
    }
}
