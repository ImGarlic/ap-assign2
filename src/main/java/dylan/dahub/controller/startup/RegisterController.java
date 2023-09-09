package dylan.dahub.controller.startup;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManagement;
import dylan.dahub.view.ErrorDisplay;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

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
        attemptRegister();
    }

    @FXML
    protected void onEnter() throws IOException {
        attemptRegister();
    }

    private void attemptRegister() throws IOException {
        hideErrors();
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(),
                lastNameInput.getText(), passwordInput.getText(), 0);

        if(validateInput(user)) {
            try {
                ActiveUser.createInstance(UserManagement.putUser(user));
                stageManager.switchScene(FxmlView.MENU);
            } catch (SQLException e) {
                String message = String.format("Failed to create user: %s", e.getMessage());
                ErrorDisplay.alertError(message);
            }
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
        try {
            if(UserManagement.checkUserExists(user.getUserName())) {
                userNameError.setText("Username already exists");
                userNameError.setVisible(true);
                valid = false;
            }
        } catch (SQLException e) {
            String message = String.format("Failed to find user: %s", e.getMessage());
            ErrorDisplay.alertError(message);
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
}