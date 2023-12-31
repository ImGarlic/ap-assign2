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
    @FXML
    private TextField userNameInput, firstNameInput, lastNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label userNameError, firstNameError, lastNameError, passwordError;

    @FXML
    protected void onBackButtonClick() {
        StageManager.getInstance().switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onRegisterButtonClick() {
        register();
    }

    @FXML
    protected void onEnter() {
        register();
    }

    // Attempts to register the user. Usernames are unique so register will fail if such username already exists
    private void register() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Arrays.asList(userNameError, firstNameError, lastNameError, passwordError)));
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(),
                lastNameInput.getText(), passwordInput.getText(), 0);

        if(validateInput(user)) {
            try {
                ActiveUser.createInstance(new UserManager().put(user));
                StageManager.getInstance().displayConfirmModal("Account created!");
                StageManager.getInstance().switchScene(FxmlView.MAIN);
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