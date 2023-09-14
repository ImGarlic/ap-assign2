package dylan.dahub.controller.startup;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

public class StartupController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    protected void onLoginButtonClick() {
        stageManager.switchScene(FxmlView.LOGIN);
    }
    @FXML
    protected void onRegisterButtonClick() {
        stageManager.switchScene(FxmlView.REGISTER);
    }

}