package dylan.dahub;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.UserManagement;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws InvalidUserException {

        stageManager = StageManager.createInstance(stage);
        setInitialScene();
    }

    protected void setInitialScene() throws InvalidUserException {
        ActiveUser.createInstance(UserManagement.getUserFromUsername("Garlic"));
        stageManager.switchScene(FxmlView.PROFILE);
    }

    public static void main(String[] args) {
        launch();
    }
}