package dylan.dahub.model;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.Logger;


public class ActiveUser extends User {
    private static ActiveUser INSTANCE;

    private ActiveUser(int ID, String userName, String firstName, String lastName, String password, int VIP) {
        super(ID, userName, firstName, lastName, password, VIP);
    }

    public static ActiveUser getInstance() {
        return INSTANCE;
    }

    public static void createInstance(User user) {
        INSTANCE = new ActiveUser(user.getID(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getVIP());
        User.printUser(INSTANCE);
    }

    public static void clearInstance() {
        INSTANCE = null;
    }

    public static void resetInstance() {
        try {
            User userFromDatabase = UserManager.getFromID(INSTANCE.getID());
            clearInstance();
            createInstance(userFromDatabase);
        } catch (InvalidUserException e) {
            String message = "Failed to reset current user. Exiting. Error: " + e.getMessage();
            Logger.alertError(message);
            System.exit(1);
        }
    }

    public static void updateInstance(User user) {
        clearInstance();
        createInstance(user);
    }
}
