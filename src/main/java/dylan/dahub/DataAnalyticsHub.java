package dylan.dahub;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.DatabaseConnection;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        printDB();
        stageManager = StageManager.createInstance(stage);
        setInitialScene();
    }

    protected void setInitialScene() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }

    private void printDB() throws SQLException {
        String username = "garlic";
        Connection con = DatabaseConnection.getConnection();
        String query = "INSERT INTO User VALUES (0, 'garlic', 'asfg', 'asdga', '1234')";
        PreparedStatement stmt = con.prepareStatement(query);

        try {
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.out.println("asfg");
        }
//        while (resultSet.next()) {
//            System.out.printf("Id: %s | First Name: %s | Last Name: %s\n",
//                    resultSet.getString("user_name"),
//                    resultSet.getString("first_name"), resultSet.getString("last_name"));
//        }
    }


    public static void main(String[] args) {
        launch();
    }
}