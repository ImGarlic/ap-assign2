package dylan.dahub.service;

import dylan.dahub.view.StageManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TestDatabaseUtils {

    public static void setUpDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();
        Long timestamp = LocalDateTime.MIN.toEpochSecond(ZoneOffset.of("Z"));

        String userTableQuery = "create table User\n" +
                "(\n" +
                "    id         INTEGER           not null\n" +
                "        constraint key_name\n" +
                "            primary key autoincrement,\n" +
                "    user_name  text(0, 255)      not null\n" +
                "        unique,\n" +
                "    first_name text(0, 255)      not null,\n" +
                "    last_name  text(0, 255)      not null,\n" +
                "    password   text(0, 255)      not null,\n" +
                "    VIP        INTEGER default 0 not null\n" +
                ");";
        String postTableQuery = "create table Post\n" +
                "(\n" +
                "    id        INTEGER      not null\n" +
                "        constraint key_name\n" +
                "            primary key autoincrement,\n" +
                "    author    text(0, 255) not null,\n" +
                "    content   text(0, 255) not null,\n" +
                "    likes     INTEGER      not null,\n" +
                "    shares    INTEGER      not null,\n" +
                "    timestamp INTEGER      not null,\n" +
                "    user      INTEGER      not null\n" +
                "        constraint FK_User_Id\n" +
                "            references User\n" +
                "            on delete cascade\n" +
                ");";

        String userQuery = "INSERT INTO User VALUES(null, 'user1', 'firstname1', 'lastname1', 'password', 0),"
                + "(null, 'user2', 'firstname2', 'lastname2', 'password', 1)";
        String postQuery = String.format("INSERT INTO Post VALUES(null, 'author1', 'content1', 0, 0, %d, 1)," +
                "(null, 'author2', 'content2', 0, 0, %d, 2)", timestamp, timestamp);

        stmt.execute(userTableQuery);
        stmt.execute(postTableQuery);
        stmt.executeUpdate(userQuery);
        stmt.executeUpdate(postQuery);
    }

    public static void tearDownDatabase() throws SQLException {
        Connection con = new Database(getTestDatabaseURL())
                .getConnection();
        Statement stmt = con.createStatement();

        String userQuery = "DROP TABLE User";
        String postQuery = "DROP TABLE Post";

        stmt.executeUpdate(userQuery);
        stmt.executeUpdate(postQuery);
    }

    public static String getTestDatabaseURL() {

        return "jdbc:sqlite:D:/Programs/IntelliJ IDEA 2022.2.1/Projects/dahub/src/test/resources/dylan/dahub/database/dataAnalyticsTest";
    }


}
