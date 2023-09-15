package dylan.dahub;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.DatabaseUtil;
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

    protected void setInitialScene() throws InvalidUserException {
        ActiveUser.createInstance(UserManager.getFromUsername("Garlic"));
        stageManager.switchScene(FxmlView.MENU);
    }

    @Override
    public void stop() throws SQLException {
        String query = "DELETE FROM Post";
        Connection con = DatabaseUtil.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Quitting");
    }

    public static void main(String[] args) {
        launch();
    }
}