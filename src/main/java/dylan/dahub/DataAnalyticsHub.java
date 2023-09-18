package dylan.dahub;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.DatabaseUtils;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws InvalidUserException {

        stageManager = StageManager.createInstance(stage);
        setInitialScene();
    }

    // Use this to set the initial scene, very useful for functional testing.
    // This should be set to STARTUP for regular use.
    protected void setInitialScene() throws InvalidUserException {
        ActiveUser.createInstance(UserManager.getFromUsername("Garlic"));
        stageManager.switchScene(FxmlView.MENU);
    }

    @Override
    public void stop() throws SQLException {
        String query = "DELETE FROM Post";
        Connection con = DatabaseUtils.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Quitting");
    }

    public static void main(String[] args) {
        launch();
    }
}