package dylan.dahub.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:D:/Programs/IntelliJ IDEA 2022.2.1/Projects/dahub/src/main/resources/dylan/dahub/database/dataAnalytics";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

