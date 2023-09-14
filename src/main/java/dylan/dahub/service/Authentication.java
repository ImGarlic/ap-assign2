package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.User;

public class Authentication {

    public static User authenticateUser(String username, String password) throws UserAuthenticationException {
        try {
            User user = UserManagement.getUserFromUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new UserAuthenticationException("Incorrect password");
            }

            System.out.printf("User %s authenticated.\n", user.getUserName());
            return user;
        } catch (InvalidUserException e) {
            throw new UserAuthenticationException(e.getMessage());
        }
    }
}

