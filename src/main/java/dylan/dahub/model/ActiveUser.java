package dylan.dahub.model;


// ActiveUser is the singleton instance of the current user logged in. The instance should only be created
// on login/register and cleared on logout.
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

    public static void updateInstance(User user) {
        clearInstance();
        createInstance(user);
    }
}
