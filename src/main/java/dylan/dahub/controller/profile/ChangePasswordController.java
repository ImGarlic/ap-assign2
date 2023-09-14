package dylan.dahub.controller.profile;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.Authentication;
import dylan.dahub.service.UserManagement;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;


public class ChangePasswordController {
    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
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
        stageManager.switchScene(FxmlView.PROFILE);
    }
    @FXML
    protected void onUpdateButtonClick() {
        updatePassword();
    }

    @FXML
    private void updatePassword() {
        User updatedUser = new User(activeUser);
        hideErrors();
        if (validateInput() && passwordsMatch()) {
            try {
                updatedUser.setPassword(newPasswordField.getText());

                Authentication.authenticateUser(activeUser.getUserName(), oldPasswordField.getText());
                ActiveUser.updateInstance(UserManagement.updateUser(updatedUser));

                stageManager.displayModal(FxmlView.PROFILE_UPDATE_CONFIRM, true);
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
