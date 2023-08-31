package dylan.dahub.controllers;

import dylan.dahub.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StartupController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onLoginButtonClick() {
        welcomeText.setText("Logged in!");
    }
    @FXML
    protected void onRegisterButtonClick() {
        welcomeText.setText("Registered!");
    }

}