package dylan.dahub.service;

import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Gets the db in relative resource folder. If you wish to use a different path, use the second return statement
    // with FILEPATH as your own filepath
    public static String getDatabaseURL() {
        Path currentFilePath = Paths.get("");

        return "jdbc:sqlite:" + currentFilePath.toAbsolutePath() + "/src/main/resources/dylan/dahub/database/dataAnalytics";
        // return FILEPATH
    }
}

