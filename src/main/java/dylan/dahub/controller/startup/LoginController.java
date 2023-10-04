package dylan.dahub.controller.startup;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.Authentication;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;


public class LoginController {

    @FXML
    private TextField userNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label userNameError, passwordError;


    @FXML
    protected void onBackButtonClick() {
        StageManager.getInstance().switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onLoginButtonClick() {
        login();
    }

    @FXML
    protected void onEnter() {
        login();
    }

    @FXML
    private void onRegisterClick() {
        StageManager.getInstance().switchScene(FxmlView.REGISTER);
    }

    // Attempts to login. Validates the input for blanks and also authenticates username/password
    private void login() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Arrays.asList(userNameError, passwordError)));

        if(validateInput()) {
            try {
                ActiveUser.createInstance(Authentication.authenticateUser(userNameInput.getText(), passwordInput.getText()));
                StageManager.getInstance().switchScene(FxmlView.MAIN);
            } catch (UserAuthenticationException e) {
                ControllerUtils.showErrorLabel(e.getMessage(), userNameError);
            }
        }
    }

    // Checks textfields for any empty fields.
    private boolean validateInput() {
        boolean valid = true;
        if(userNameInput.getText().equals("")) {
            ControllerUtils.showErrorLabel("Username cannot be empty", userNameError);
            valid = false;
        }
        if(passwordInput.getText().equals("")) {
            ControllerUtils.showErrorLabel("Password cannot be empty", passwordError);
            valid = false;
        }
        return valid;
    }
}