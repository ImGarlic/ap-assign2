package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.User;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// Unfortunately I didn't have the time to set up proper database handling. So we have to completely
// setup/teardown the test database before/after each test, which takes a while. (Maybe auto-generated ID wasn't
// such a good idea...)
public class UserManagerTest {
    // Users put into database:
    // (1, 'user1', 'firstname1', 'lastname1', 'password', 0)
    // (2, 'user2', 'firstname2', 'lastname2', 'password', 1)

    @BeforeEach
    public void setUp() {
        try {
            setUpDatabase();
        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            tearDownDatabase();
        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void userExists_Success() {
        Assertions.assertTrue(
                new UserManager().withConnection(getTestDatabaseURL())
                        .userExists("user1"));
    }

    @Test
    public void userExists_fail() {
        Assertions.assertFalse(
                new UserManager().withConnection(getTestDatabaseURL())
                        .userExists("not a username"));
    }

    @Test
    public void getFromID_Success() throws InvalidUserException {
        Assertions.assertEquals(
                "user1",
                new UserManager().withConnection(getTestDatabaseURL())
                        .getFromID(1).getUserName()
        );
    }

    @Test
    public void getFromID_Fail() {
        Assertions.assertThrows(
                InvalidUserException.class,
                () -> new UserManager().withConnection(getTestDatabaseURL())
                        .getFromID(0)
        );
    }

    @Test
    public void getFromUsername_Success() throws InvalidUserException {
        Assertions.assertEquals(
                1,
                new UserManager().withConnection(getTestDatabaseURL())
                        .getFromUsername("user1").getID()
        );
    }

    @Test
    public void getFromUsername_Fail() {
        Assertions.assertThrows(
                InvalidUserException.class,
                () -> new UserManager().withConnection(getTestDatabaseURL())
                        .getFromUsername("not a username")
        );
    }

    @Test
    public void put_Success() throws InvalidUserException {
        User user = new User(
                3,
                "user3",
                "firstname2",
                "lastname2",
                "password",
                0);

        Assertions.assertEquals(
                user.getUserName(),
                new UserManager().withConnection(getTestDatabaseURL()).
                        put(user).getUserName()
                );
    }

    @Test
    public void put_Fail_UsernameExists() {
        User user = new User(
                1,
                "user1",
                "firstname1",
                "lastname1",
                "password",
                0);

        Assertions.assertThrows(
                InvalidUserException.class,
                () -> new UserManager().withConnection(getTestDatabaseURL())
                        .put(user));
    }

    @Test
    public void update_Success() throws InvalidUserException {
        User newUserDetails = new User(
                1,
                "new username",
                "new firstname",
                "new lastname",
                "new password",
                1);

        User updatedUser = new UserManager().withConnection(getTestDatabaseURL()).update(newUserDetails);
        Assertions.assertEquals(newUserDetails.getUserName(), updatedUser.getUserName());
        Assertions.assertEquals(newUserDetails.getFirstName(), updatedUser.getFirstName());
        Assertions.assertEquals(newUserDetails.getLastName(), updatedUser.getLastName());
        Assertions.assertEquals(newUserDetails.getPassword(), updatedUser.getPassword());
        Assertions.assertEquals(newUserDetails.getVIP(), updatedUser.getVIP());
    }

    @Test
    public void update_Fail_UsernameExists() {
        User newUserDetails = new User(
                1,
                "user2",
                "new firstname",
                "new lastname",
                "new password",
                1);

        Assertions.assertThrows(
                InvalidUserException.class,
                () -> new UserManager().withConnection(getTestDatabaseURL())
                        .update(newUserDetails));
    }

    @Test
    public void delete_Success() throws InvalidUserException {
        new UserManager().withConnection(getTestDatabaseURL())
                .delete(1);

        Assertions.assertThrows(
                InvalidUserException.class,
                () -> new UserManager().withConnection(getTestDatabaseURL())
                        .getFromID(1)
        );
    }

    @Test
    public void getRandomUser_Success() throws InvalidUserException {
        User user = new UserManager().withConnection(getTestDatabaseURL())
                .getRandomUser();
        Assertions.assertTrue(
                "user1".equals(user.getUserName()) || "user2".equals(user.getUserName())
        );
    }

    public static void setUpDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();

        String userTableQuery = """
                create table User
                (
                    id         INTEGER           not null
                        constraint key_name
                            primary key autoincrement,
                    user_name  text(0, 255)      not null
                        unique,
                    first_name text(0, 255)      not null,
                    last_name  text(0, 255)      not null,
                    password   text(0, 255)      not null,
                    VIP        INTEGER default 0 not null
                );""";

        String userQuery = "INSERT INTO User VALUES(1, 'user1', 'firstname1', 'lastname1', 'password', 0),"
                + "(2, 'user2', 'firstname2', 'lastname2', 'password', 1)";

        stmt.execute(userTableQuery);
        stmt.executeUpdate(userQuery);
    }

    public static void tearDownDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();

        String userQuery = "DROP TABLE User";

        stmt.executeUpdate(userQuery);
    }

    public static String getTestDatabaseURL() {
        Path currentFilePath = Paths.get("");
        String dbString = currentFilePath.toAbsolutePath() + "/src/test/resources/dylan/dahub/database/dataAnalyticsTest";

        return "jdbc:sqlite:" + dbString;
    }
}