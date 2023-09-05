package dylan.dahub;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;
    @Override
    public void start(Stage stage) throws IOException {
        stageManager = StageManager.createInstance(stage);
        setInitialScene();
    }

    protected void setInitialScene() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }

    public static void main(String[] args) {
        launch();
    }
}