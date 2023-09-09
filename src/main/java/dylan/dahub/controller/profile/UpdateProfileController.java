package dylan.dahub.controller.profile;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.Authentication;
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

public class UpdateProfileController {

    private final StageManager stageManager = StageManager.getInstance();
    private ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private TextField userNameInput, firstNameInput, lastNameInput;
    @FXML
    private Label userNameError;

    @FXML
    protected void initialize() {
        userNameInput.setPromptText(activeUser.getUserName());
        firstNameInput.setPromptText(activeUser.getFirstName());
        lastNameInput.setPromptText(activeUser.getLastName());
        userNameError.setVisible(false);
    }

    @FXML
    protected void onEnter() {
        updateProfile();
    }
    @FXML
    protected void onBackButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.PROFILE);
    }
    @FXML
    protected void onUpdateButtonClick() {
        updateProfile();
    }

    private void updateProfile() {
        activeUser = ActiveUser.getInstance();
        User updatedUser = getNewUserValues();

        try {
            if (UserManagement.checkUserExists(userNameInput.getText()) && !userNameInput.getText().equals(activeUser.getUserName())) {
                userNameError.setText("Username already exists");
                userNameError.setVisible(true);
            } else {
                ActiveUser.updateInstance(UserManagement.updateUser(updatedUser));
            }
        } catch (SQLException e) {
            String message = String.format("Failed to update user: %s", e.getMessage());
            ErrorDisplay.alertError(message);
        }
    }

    private User getNewUserValues() {
        String newUserName = activeUser.getUserName();
        String newFirstName = activeUser.getFirstName();
        String newLastName = activeUser.getLastName();

        if (!userNameInput.getText().equals("")) {
            newUserName = userNameInput.getText();
        }
        if (!firstNameInput.getText().equals("")) {
            newFirstName = firstNameInput.getText();
        }
        if (!lastNameInput.getText().equals("")) {
            newLastName = lastNameInput.getText();
        }

        return new User(activeUser.getID(), newUserName,
                newFirstName, newLastName,
                activeUser.getPassword(), activeUser.getVIP());
    }
}
