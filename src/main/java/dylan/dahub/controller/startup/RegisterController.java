package dylan.dahub.controller.startup;

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
        hideErrors();
        User user = new User(0, userNameInput.getText(), firstNameInput.getText(),
                lastNameInput.getText(), passwordInput.getText(), 0);

        if(validateInput(user)) {
            try {
                ActiveUser.createInstance(UserManager.put(user));
                stageManager.switchScene(FxmlView.MENU);
            } catch (InvalidUserException e) {
                userNameError.setText(e.getMessage());
                userNameError.setVisible(true);
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