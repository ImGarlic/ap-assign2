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
    }


    public static void main(String[] args) {
        launch();
    }
}