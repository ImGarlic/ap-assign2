package dylan.dahub.service;

import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.User;
import dylan.dahub.view.ErrorDisplay;

import java.sql.SQLException;

public class Authentication {

    public static User authenticateLogin(String username, String password) throws UserAuthenticationException {
        try {
            User user = UserManagement.getUser(username);
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
            ErrorDisplay.alertError("Failed to authenticate user: " + e.getMessage());
        }
        throw new UserAuthenticationException("Failed to authenticate user");
    }
}

