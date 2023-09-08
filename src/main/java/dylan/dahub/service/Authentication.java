package dylan.dahub.service;

import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.User;

import java.sql.SQLException;

public class Authentication {

    public static User authenticateLogin(String username, String password) throws UserAuthenticationException {
        try {
            User user = UserManagementService.getUser(username);
            if (user.getUserName() == null) {
                throw new UserAuthenticationException("Username does not exist");
            }
            if (!user.getPassword().equals(password)) {
                throw new UserAuthenticationException("Incorrect password");
            }

            System.out.printf("User %s logged in.\n", user.getUserName());
            return user;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

