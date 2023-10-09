package dylan.dahub.service;

import dylan.dahub.exception.InvalidPostException;
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
                con.close();
            } else {
                throw new InvalidUserException("Username does not exist");
            }

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

    public static User update(User user) throws InvalidUserException {
        String query = String.format("UPDATE %s SET user_name = '%s', " +
                        "first_name = '%s', " +
                        "last_name = '%s', " +
                        "password = '%s', " +
                        "VIP = %d " +
                        "WHERE id = '%d'",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(),
                user.getPassword(), user.getVIP(), user.getID());

        try (Connection con = DatabaseUtils.getConnection()){

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
            Logger.alertError(String.format("Failed to update user: %s", e.getMessage()));
            throw new InvalidUserException("");
        }
    }

    public static User getRandomUser() throws InvalidUserException {
        String query = String.format("SELECT * FROM %s ORDER BY RANDOM() LIMIT 1", TABLE_NAME);

        return get(query);
    }

}
