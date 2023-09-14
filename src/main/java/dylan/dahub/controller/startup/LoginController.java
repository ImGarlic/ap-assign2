package dylan.dahub.controller.startup;

import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.Authentication;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    private TextField userNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label userNameError, passwordError;

    @FXML
    private void initialize() {
    }

    @FXML
    protected void onBackButtonClick() {
        stageManager.switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onLoginButtonClick() {
        attemptLogin();
    }

    @FXML
    protected void onEnter() {
        attemptLogin();
    }

    private void attemptLogin() {
        hideErrors();

        if(validateInput()) {
            try {
                ActiveUser.createInstance(Authentication.authenticateUser(userNameInput.getText(), passwordInput.getText()));
                stageManager.switchScene(FxmlView.MENU);
            } catch (UserAuthenticationException e) {
                userNameError.setText(e.getMessage());
                userNameError.setVisible(true);
            }
        }
    }

    private void hideErrors() {
        userNameError.setVisible(false);
        passwordError.setVisible(false);
    }

    private boolean validateInput() {
        boolean valid = true;
        if(userNameInput.getText().equals("")) {
            userNameError.setText("Username cannot be empty");
            userNameError.setVisible(true);
            valid = false;
        }
        if(passwordInput.getText().equals("")) {
            passwordError.setText("Password cannot be empty");
            passwordError.setVisible(true);
            valid = false;
        }
        return valid;
    }
}