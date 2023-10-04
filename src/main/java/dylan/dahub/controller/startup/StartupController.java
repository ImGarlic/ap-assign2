package dylan.dahub.controller.startup;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

public class StartupController {

    @FXML
    protected void onLoginButtonClick() {
        StageManager.getInstance().switchScene(FxmlView.LOGIN);
    }
    @FXML
    protected void onRegisterButtonClick() {
        StageManager.getInstance().switchScene(FxmlView.REGISTER);
    }

}