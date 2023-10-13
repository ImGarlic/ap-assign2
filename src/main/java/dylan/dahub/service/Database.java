package dylan.dahub.service;

import java.sql.*;

public class Database {
    private final String DB_URL;

    public Database(String URL) {
        this.DB_URL = URL;
    }

    // Connect to the database specified in DB_URL
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Returns the ID of the last entry into the database.
    public static int getLastID(Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT last_insert_rowid();");
        ResultSet generatedKeys = stmt.executeQuery();
        return generatedKeys.getInt(1);
    }

    public static String getDatabaseURL() {
        return "jdbc:sqlite:D:/Programs/IntelliJ IDEA 2022.2.1/Projects/dahub/src/main/resources/dylan/dahub/database/dataAnalytics";
    }
}

