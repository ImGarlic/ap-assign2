package dylan.dahub.service;

import dylan.dahub.exception.InvalidUserException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

// Unfortunately I didn't have the time to set up proper database handling. So we have to completely
// setup/teardown the test database before/after each test, which takes a while. (Maybe auto-generated ID wasn't
// such a good idea...)

public class UserManagerTest {
    // Users put into database:
    // (1, 'user1', 'firstname1', 'lastname1', 'password', 0)
    // (2, 'user2', 'firstname2', 'lastname2', 'password', 1)

    // Posts put into database:
    // (1, 'author1', 'content1', 0, 0, [LocalDateTime.MIN], 1)
    // (2, 'author2', 'content2', 0, 0, [LocalDateTime.MIN], 2)

    @BeforeEach
    public void setUp() {
        try {
            TestDatabaseUtils.setUpDatabase();
        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            TestDatabaseUtils.tearDownDatabase();
        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetFromID() {
        try {
            new UserManager().withConnection(TestDatabaseUtils.getTestDatabaseURL()).getFromID(1);
        } catch (InvalidUserException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetFromUsername() {
        try {
            new UserManager().withConnection(TestDatabaseUtils.getTestDatabaseURL()).getFromUsername("user1");
        } catch (InvalidUserException e) {
            Assertions.fail(e.getMessage());
        }
    }
}