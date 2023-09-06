package dylan.dahub.model;

import dylan.dahub.service.UserManagementService;

public class ActiveUser extends User {
    private static ActiveUser INSTANCE;

    private ActiveUser(int ID, String userName, String firstName, String lastName, String password) {
        super(ID, userName, firstName, lastName, password);
    }

    public static ActiveUser getInstance() {
        return INSTANCE;
    }

    public static ActiveUser createInstance(User user) {
        INSTANCE = new ActiveUser(user.getID(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getPassword());
        return INSTANCE;
    }

    public static void clearInstance() {
        INSTANCE = null;
    }
}
