package dylan.dahub.controller;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MenuController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private Label welcomeText;

    @FXML
    private void initialize() {
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());
    }

    @FXML
    protected void onLogoutButtonClick() throws IOException {
        ActiveUser.clearInstance();
        stageManager.switchScene(FxmlView.STARTUP);
    }
}
