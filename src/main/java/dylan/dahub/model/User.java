package dylan.dahub.model;

public class User {
    private int ID, VIP;
    private String userName, firstName, lastName, password;

    public User(int ID, String userName, String firstName, String lastName, String password, int VIP) {
        this.ID = ID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.VIP = VIP;
    }

    public User() {
        // Empty constructor
    }

    public User(User user) {
        this.ID = user.getID();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.VIP = user.getVIP();
    }

    // DEBUG
    public static void printUser(User user) {
        if (user != null) {
            String printString = "ID: " + user.ID + " | Username: " + user.userName + " | First name: " + user.firstName +
                    " | Last name: " + user.lastName + " | Password: " + user.password + " | VIP: " + (user.VIP != 0);
            System.out.println(printString);
        }
    }

    public boolean isVIP() {
        return this.VIP > 0;
    }

    public int getID() {
        return ID;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVIP() {
        return VIP;
    }

    public void setVIP(int VIP) {
        this.VIP = VIP;
    }
}
