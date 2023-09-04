package dylan.dahub.controller;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class StartupController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        StageManager.switchScene(event, FxmlView.LOGIN);
    }
    @FXML
    protected void onRegisterButtonClick() {
        welcomeText.setText("Registered!");
    }

}