package dylan.dahub.service;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.Post;
import dylan.dahub.model.Range;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

// Unfortunately I didn't have the time to set up proper database handling. So we have to completely
// setup/teardown the test database before/after each test, which takes a while. (Maybe auto-generated ID wasn't
// such a good idea...)
public class PostManagerTest {
    // Posts put into database:
    // (1, 'author1', 'content1', 0, 0, [LocalDateTime.MIN], 1)
    // (2, 'author2', 'content2', 0, 0, [LocalDateTime.MIN], 2)

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
    public void generatePostFromTextFields_Success() {
        Post post = new Post(1, "author", "content", 0, 0, LocalDateTime.MIN);

        Assertions.assertAll(() -> PostManager.generatePostFromTextFields(
                post.author(),
                post.content(),
                post.getDateTimeString(),
                String.valueOf(post.likes()),
                String.valueOf(post.shares())
        ));
    }

    @Test
    public void generatePostFromTextFields_Fail_InvalidDate() {
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> PostManager.generatePostFromTextFields(
                        "author",
                        "content",
                        "12/32/2000/12:30",
                        "0",
                        "0"
                ));
    }

    @Test
    public void getMulti_Success_All() throws InvalidPostException {
        Assertions.assertEquals(
                2,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "",
                                "timestamp",
                                "DESC",
                                false,
                                0
                        ).size()
        );
    }

    @Test
    public void getMulti_Success_Single() throws InvalidPostException {
        Assertions.assertEquals(
                1,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "",
                                "timestamp",
                                "DESC",
                                false,
                                0
                        ).get(0).ID()
        );
    }

    @Test
    public void getMulti_Success_OnlyCurrentUser() throws InvalidPostException {
        Assertions.assertEquals(
                1,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "",
                                "timestamp",
                                "DESC",
                                true,
                                0
                        ).size()
        );
    }

    @Test
    public void getPostCount_Success_All() throws InvalidPostException {
        Assertions.assertEquals(
                2,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getPostCount(
                                1,
                                false,
                                "likes",
                                new Range(0, Integer.MAX_VALUE)
                        )
        );
    }

    @Test
    public void getPostCount_Success_OnlyCurrentUser() throws InvalidPostException {
        Assertions.assertEquals(
                1,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getPostCount(
                                1,
                                true,
                                "likes",
                                new Range(0, Integer.MAX_VALUE)
                        )
        );
    }

    @Test
    public void getRandomPost_Success() throws InvalidPostException {
        Post post = new PostManager().withConnection(getTestDatabaseURL())
                .getRandomPost();
        Assertions.assertTrue(
                1 == post.ID() || 2 == post.ID()
        );
    }

    @Test
    public void put_Success() throws InvalidPostException {
        Post newPost = new Post(
                3,
                "author3",
                "content3",
                0,
                0,
                LocalDateTime.MIN
        );
        new PostManager().withConnection(getTestDatabaseURL()).put(0, newPost);

        Assertions.assertEquals(
                1,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "author3",
                                "timestamp",
                                "DESC",
                                false,
                                0
                        ).size()
        );
    }

    @Test
    public void putMulti_Success() throws InvalidPostException {
        Post newPost = new Post(
                3,
                "something unique to search on",
                "content3",
                0,
                0,
                LocalDateTime.MIN
        );
        Post newPost2 = new Post(
                4,
                "something unique to search on",
                "content4",
                0,
                0,
                LocalDateTime.MIN
        );
        new PostManager().withConnection(getTestDatabaseURL())
                .putMulti(0, new ArrayList<>(Arrays.asList(newPost, newPost2)));

        Assertions.assertEquals(
                2,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "something unique to search on",
                                "timestamp",
                                "DESC",
                                false,
                                0
                        ).size()
        );
    }

    @Test
    public void delete_Success() throws UserAuthenticationException, InvalidPostException {
        new PostManager().withConnection(getTestDatabaseURL()).delete(1, 1);

        Assertions.assertEquals(
                1,
                new PostManager().withConnection(getTestDatabaseURL())
                        .getMulti(
                                1,
                                10,
                                "",
                                "timestamp",
                                "DESC",
                                false,
                                0
                        ).size()
        );
    }

    @Test
    public void delete_Fail_IncorrectUser() {
        Assertions.assertThrows(
                UserAuthenticationException.class,
                () -> new PostManager().withConnection(getTestDatabaseURL()).delete(0, 1)
        );
    }

    @Test
    public void delete_Fail_PostNotExist() {
        Assertions.assertThrows(
                InvalidPostException.class,
                () -> new PostManager().withConnection(getTestDatabaseURL()).delete(1, 0)
        );
    }

    public static void setUpDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();
        Long timestamp = LocalDateTime.MIN.toEpochSecond(ZoneOffset.of("Z"));

        String userTableQuery = """
                create table Post
                (
                    id        INTEGER      not null
                        constraint key_name
                            primary key autoincrement,
                    author    text(0, 255) not null,
                    content   text(0, 255) not null,
                    likes     INTEGER      not null,
                    shares    INTEGER      not null,
                    timestamp INTEGER      not null,
                    user      INTEGER      not null
                        constraint FK_User_Id
                            references User
                            on delete cascade
                );""";

        String userQuery = String.format("INSERT INTO Post VALUES(1, 'author1', 'content1', 0, 0, %s, 1),"
                + "(2, 'author2', 'content2', 0, 0, %s, 2)", timestamp, timestamp);

        stmt.execute(userTableQuery);
        stmt.executeUpdate(userQuery);
    }

    public static void tearDownDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();

        String userQuery = "DROP TABLE Post";

        stmt.executeUpdate(userQuery);
    }

    public static String getTestDatabaseURL() {
        Path currentFilePath = Paths.get("");
        String dbString = currentFilePath.toAbsolutePath() + "/src/test/resources/dylan/dahub/database/dataAnalyticsTest";

        return "jdbc:sqlite:" + dbString;
    }
}
