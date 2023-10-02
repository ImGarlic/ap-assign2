package dylan.dahub.controller.profile;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.Authentication;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;


public class ChangePasswordController {
    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    private PasswordField oldPasswordField, newPasswordField, confirmNewPasswordField;
    @FXML
    private Label oldPasswordError, newPasswordError;

    @FXML
    protected void onEnter() {
        updatePassword();
    }
    @FXML
    protected void onBackButtonClick() {
        stageManager.setMainScreen(FxmlView.PROFILE);
    }
    @FXML
    protected void onUpdateButtonClick() {
        updatePassword();
    }

    @FXML
    private void updatePassword() {
        User updatedUser = new User(ActiveUser.getInstance());
        hideErrors();
        if (validateInput() && passwordsMatch()) {
            try {
                updatedUser.setPassword(newPasswordField.getText());

                Authentication.authenticateUser(ActiveUser.getInstance().getUserName(), oldPasswordField.getText());
                ActiveUser.updateInstance(UserManager.update(updatedUser));

                stageManager.displayModal(FxmlView.MODAL_CONFIRM, true, "Password updated.");
            } catch (UserAuthenticationException | InvalidUserException e) {
                oldPasswordError.setText(e.getMessage());
                oldPasswordError.setVisible(true);
            }
        }
    }

    private boolean validateInput() {
        if(!newPasswordField.getText().equals("")) {
            return true;
        }

        newPasswordError.setText("Password cannot be empty");
        newPasswordError.setVisible(true);
        return false;
    }

    private boolean passwordsMatch() {
        if (newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
            return true;
        }

        newPasswordError.setText("Passwords do not match");
        newPasswordError.setVisible(true);
        return false;
    }

    private void hideErrors() {
        oldPasswordError.setVisible(false);
        newPasswordError.setVisible(false);
    }

}
