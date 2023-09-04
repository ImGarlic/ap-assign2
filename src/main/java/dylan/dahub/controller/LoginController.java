package dylan.dahub.controller;

import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onBackButtonClick(ActionEvent event) throws IOException {
        StageManager.switchScene(event, FxmlView.STARTUP);
    }
}