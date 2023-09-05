package dylan.dahub.controller;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

import java.io.IOException;

public class RegisterController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    protected void onBackButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }

}