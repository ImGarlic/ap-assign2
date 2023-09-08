package dylan.dahub.controller.startup;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

import java.io.IOException;

public class StartupController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    protected void onLoginButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.LOGIN);
    }
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.REGISTER);
    }

}