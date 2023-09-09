package dylan.dahub.model;

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
}
