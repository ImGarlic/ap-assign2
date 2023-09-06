package dylan.dahub.controller;

import dylan.dahub.model.ActiveUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {

    private final ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private Label welcomeText;

    @FXML
    private void initialize() {
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());
    }
}
