package dylan.dahub.model;

public class User {
    private int ID;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private int VIP;

    public User(int ID, String userName, String firstName, String lastName, String password, int VIP) {
        this.ID = ID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.VIP = VIP;

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

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
