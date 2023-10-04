package dylan.dahub.controller.profile;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;


public class ProfileUpdateController {

    @FXML
    private TextField userNameInput, firstNameInput, lastNameInput;
    @FXML
    private Label userNameError;

    @FXML
    protected void initialize() {
        userNameError.setVisible(false);
        setPromptTexts();
    }

    @FXML
    protected void onEnter() {
        updateProfile();
    }
    @FXML
    protected void onBackButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.PROFILE);
    }
    @FXML
    protected void onUpdateButtonClick() {
        updateProfile();
    }

    // Attempts to update the user's profile, also updates the prompt texts to match the new values.
    // If a field is left blank, then it is not updated.
    private void updateProfile() {
        User updatedUser = getNewUserValues();

        try {
            ActiveUser.updateInstance(UserManager.update(updatedUser));
            StageManager.getInstance().displayConfirmModal("Profile details updated.");
            ControllerUtils.clearTextFields(new ArrayList<>(Arrays.asList(userNameInput, firstNameInput, lastNameInput)));
            setPromptTexts();
        } catch (InvalidUserException e) {
            userNameError.setText(e.getMessage());
            userNameError.setVisible(true);
        }
    }

    // Grabs the new values from the text fields
    private User getNewUserValues() {
        ActiveUser activeUser = ActiveUser.getInstance();
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

    private void setPromptTexts() {
        ActiveUser activeUser = ActiveUser.getInstance();
        userNameInput.setPromptText(activeUser.getUserName());
        firstNameInput.setPromptText(activeUser.getFirstName());
        lastNameInput.setPromptText(activeUser.getLastName());
    }

}
