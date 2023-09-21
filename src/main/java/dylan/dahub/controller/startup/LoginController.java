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

    @FXML
    private void onRegisterClick() {
        stageManager.switchScene(FxmlView.REGISTER);
    }

    private void attemptLogin() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Arrays.asList(userNameError, passwordError)));

        if(validateInput()) {
            try {
                ActiveUser.createInstance(Authentication.authenticateUser(userNameInput.getText(), passwordInput.getText()));
                stageManager.switchScene(FxmlView.MAIN);
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