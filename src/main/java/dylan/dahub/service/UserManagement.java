package dylan.dahub.service;

import dylan.dahub.model.User;

import java.sql.*;

public class UserManagement {
    private static final String TABLE_NAME = "User";

    public static boolean checkUserExists(String username) throws SQLException {
        return getUser(username).getUserName() != null;
    }

    public static User getUser(String username) throws SQLException {
        User user;
        String query = String.format("SELECT * FROM %s WHERE user_name='%s' COLLATE NOCASE LIMIT 1", TABLE_NAME, username);

        Connection con = DatabaseConnection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet resultSet = stmt.executeQuery(query);
        user = new User(resultSet.getInt("id"), resultSet.getString("user_name"),
                resultSet.getString("first_name"), resultSet.getString("last_name"),
                resultSet.getString("password"), resultSet.getInt("VIP"));
        con.close();
        return user;
    }

    public static User putUser(User user) throws SQLException {
        String query = String.format("INSERT INTO %s VALUES (null, '%s', '%s', '%s', '%s', 0)",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword());

        Connection con = DatabaseConnection.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);

        System.out.println("Added user");
        con.close();
        return getUser(user.getUserName());
    }

    public static User updateUser(User user) throws SQLException {
        String query = String.format("UPDATE %s SET user_name = '%s', " +
                        "first_name = '%s', " +
                        "last_name = '%s', " +
                        "password = '%s', " +
                        "VIP = %d " +
                        "WHERE user_name = '%s'",
                TABLE_NAME, user.getUserName(), user.getFirstName(), user.getLastName(),
                user.getPassword(), user.getVIP(), user.getUserName());

        Connection con = DatabaseConnection.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);

        System.out.println("Updated user");
        con.close();
        return getUser(user.getUserName());
    }
}