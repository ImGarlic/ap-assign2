package dylan.dahub.controller.startup;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;

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
    protected void onBackButtonClick() {
        stageManager.switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onRegisterButtonClick() {
        attemptRegister();
    }

    @FXML
    protected void onEnter() {
        attemptRegister();
    }

    private void attemptRegister() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Arrays.asList(userNameError, firstNameError, lastNameError, passwordError)));
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(),
                lastNameInput.getText(), passwordInput.getText(), 0);

        if(validateInput(user)) {
            try {
                ActiveUser.createInstance(UserManager.put(user));
                stageManager.switchScene(FxmlView.MENU);
            } catch (InvalidUserException e) {
                ControllerUtils.showErrorLabel(e.getMessage(), userNameError);
            }
        }
    }

    // Checks textfields for any empty fields.
    private boolean validateInput(User user) {
        boolean valid = true;
        if(user.getUserName().equals("")) {
            ControllerUtils.showErrorLabel("Username cannot be empty", userNameError);
            valid = false;
        }

        if(user.getFirstName().equals("")) {
            ControllerUtils.showErrorLabel("First name cannot be empty", firstNameError);
            valid = false;
        }
        if(user.getLastName().equals("")) {
            ControllerUtils.showErrorLabel("Last name cannot be empty", lastNameError);
            valid = false;
        }
        if(user.getPassword().equals("")) {
            ControllerUtils.showErrorLabel("Password cannot be empty", passwordError);
            valid = false;
        }
        return valid;
    }
}