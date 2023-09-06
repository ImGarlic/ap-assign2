package dylan.dahub.controller;

import dylan.dahub.model.User;
import dylan.dahub.service.UserManagementService;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {
    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    private TextField userNameInput;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private PasswordField passwordInput;


    @FXML
    protected void onBackButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }
    @FXML
    protected void onRegisterButtonClick() {
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(), lastNameInput.getText(), passwordInput.getText());
        UserManagementService.putUser(user);
    }

}