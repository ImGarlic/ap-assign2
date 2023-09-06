package dylan.dahub.service;

import dylan.dahub.model.User;

import java.sql.*;

public class UserManagementService {

    public static User getUser(String username) {
        User user = null;
        String TABLE_NAME = "User";
        String query = String.format( "SELECT * FROM %s WHERE user_name='%s'", TABLE_NAME, username);

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            ResultSet resultSet = stmt.executeQuery(query);
            while(resultSet.next()) {
                user = new User(resultSet.getInt("ID"),
                        resultSet.getString("user_name"), resultSet.getString("first_name"),
                        resultSet.getString("last_name"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        User.printUser(user);
        return user;
    }

    public static User putUser(User user) {
        String TABLE_NAME = "User";
        String query = String.format("INSERT INTO %s VALUES (%d, '%s', '%s', '%s', '%s')",
                TABLE_NAME, null, user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword());
        System.out.println(query);

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Added user");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return getUser(user.getUserName());
    }
}
