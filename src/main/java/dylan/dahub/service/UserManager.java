package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.User;
import dylan.dahub.view.Logger;

import java.sql.*;

public class UserManager {
    private static final String TABLE_NAME = "User";

    public static boolean userExists(String username) {
        try {
            getFromUsername(username);
        } catch (InvalidUserException e) {
            return false;
        }
        return true;
    }

    public static User getFromUsername(String username) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE user_name='%s' COLLATE NOCASE LIMIT 1", TABLE_NAME, username);

        return getUser(query);
    }

    public static User getFromID(int ID) throws InvalidUserException {
        String query = String.format("SELECT * FROM %s WHERE id='%d' COLLATE NOCASE LIMIT 1", TABLE_NAME, ID);

        return getUser(query);
    }

    private static User getUser(String query) throws InvalidUserException {
        User user = new User();

        try {
            Connection con = DatabaseUtil.getConnection();
            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);
            if (resultSet.next()) {
                user = new User(resultSet.getInt("id"), resultSet.getString("user_name"),
                        resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("password"), resultSet.getInt("VIP"));
                con.close();
            } else {
                throw new InvalidUserException("Username does not exist");
            }

            return user;
        } catch (SQLException e) {
            String message = "Failed to get user from database: " + e.getMessage();
            Logger.alertError(message);
        }

        throw new InvalidUserException("Something went wrong.");
    }

    public static User put(User user) throws InvalidUserException {
        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%s', '%s', 0)",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword());

        try {
            Connection con = DatabaseUtil.getConnection();

            if (userExists(user.getUserName())) {
                throw new InvalidUserException("Username already exists");
            }
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Added user");
            int generatedID = DatabaseUtil.getLastID(con);
            con.close();

            return getFromID(generatedID);
        } catch (SQLException e) {
            String message = String.format("Failed to create user: %s", e.getMessage());
            Logger.alertError(message);
        }
        throw new InvalidUserException("Something went wrong.");
    }

    public static User update(User user) throws InvalidUserException {
        String query = String.format("UPDATE %s SET user_name = '%s', " +
                        "first_name = '%s', " +
                        "last_name = '%s', " +
                        "password = '%s', " +
                        "VIP = %d " +
                        "WHERE id = '%d'",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(),
                user.getPassword(), user.getVIP(), user.getID());

        try {
            Connection con = DatabaseUtil.getConnection();

            if (userExists(user.getUserName()) &&
                    getFromUsername(user.getUserName()).getID() != user.getID()) {
                throw new InvalidUserException("Username already exists");
            }
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Updated user");
            con.close();
            return getFromID(user.getID());
        } catch (SQLException e) {
            String message = String.format("Failed to update user: %s", e.getMessage());
            Logger.alertError(message);
        }
        throw new InvalidUserException("Something went wrong.");
    }

}