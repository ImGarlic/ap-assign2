package dylan.dahub.controller;

import dylan.dahub.model.User;
import dylan.dahub.service.UserManagementService;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {
    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    private TextField userNameInput, firstNameInput, lastNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label userNameError, firstNameError, lastNameError, passwordError;

    @FXML
    private void initialize() {
    }
    @FXML
    protected void onBackButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.STARTUP);
    }
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        hideErrors();
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(),
                lastNameInput.getText(), passwordInput.getText());

        if(validateInput(user)) {
            UserManagementService.putUser(user);
            alertSuccess();
            stageManager.switchScene(FxmlView.STARTUP);
        }
    }

    private void hideErrors() {
        userNameError.setVisible(false);
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        passwordError.setVisible(false);
    }
    private boolean validateInput(User user) {
        boolean valid = true;
        if(user.getUserName().equals("")) {
            userNameError.setText("Username cannot be empty");
            userNameError.setVisible(true);
            valid = false;
        }
        if(UserManagementService.getUser(user.getUserName()) != null) {
            userNameError.setText("Username already exists");
            userNameError.setVisible(true);
            valid = false;
        }
        if(user.getFirstName().equals("")) {
            firstNameError.setText("First name cannot be empty");
            firstNameError.setVisible(true);
            valid = false;
        }
        if(user.getLastName().equals("")) {
            lastNameError.setText("Last name cannot be empty");
            lastNameError.setVisible(true);
            valid = false;
        }
        if(user.getPassword().equals("")) {
            passwordError.setText("Password cannot be empty");
            passwordError.setVisible(true);
            valid = false;
        }
        return valid;
    }

    private void alertSuccess() {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Account Created");
        successAlert.setHeaderText("Account successfully created. Please login to use the app.");
        successAlert.show();
    }

}