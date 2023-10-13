package dylan.dahub.controller.profile;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ProfileController {
    @FXML
    private Label username, fullname;
    @FXML
    private Button VIPButton;
    @FXML
    private ImageView profileImage;


    @FXML
    private void initialize() {
        ActiveUser activeUser = ActiveUser.getInstance();
        username.setText(activeUser.getUserName());
        fullname.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
        checkVIPStatus();
    }

    @FXML
    private void onVIPButtonClick() {
        updateVIPStatus();
    }

    @FXML
    private void onUpdateProfileButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.PROFILE_UPDATE);
    }

    @FXML
    private void onChangePasswordButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.CHANGE_PASSWORD);
    }

    @FXML
    private void onDeleteAccountClick() {
        deleteAccount();
    }


    // Asks the user if they wish to upgrade to / cancel vip
    private void updateVIPStatus() {
        String requestMessage, confirmMessage;
        User updatedUser = new User(ActiveUser.getInstance());

        if (ActiveUser.getInstance().isVIP()) {
            requestMessage = "Would you like to cancel you VIP subscription?";
            confirmMessage = "Successfully cancelled your subscription!";
            updatedUser.setVIP(0);
        } else {
            requestMessage = "Would you like to upgrade your membership to VIP?";
            confirmMessage = "Successfully upgraded to VIP!";
            updatedUser.setVIP(1);
        }

        if (StageManager.getInstance().displayRequestModal(requestMessage)) {
            try {
                ActiveUser.updateInstance(new UserManager().update(updatedUser));
                StageManager.getInstance().displayConfirmModal(confirmMessage);
            } catch (InvalidUserException e) {
                Logger.alertError("Failed to update VIP status: " + e.getMessage());
            }

        }
        checkVIPStatus();
        StageManager.getInstance().updateMainFrameProfileDetails();
    }

    // Checks if the user is a VIP, changes the VIP button text and the profile pic if they are.
    private void checkVIPStatus() {
        String VIP_PROFILE_IMAGE = "image/VIP_profile_upscaled.png";
        String DEFAULT_PROFILE_IMAGE = "image/default_profile_upscaled.png";

        if (ActiveUser.getInstance().isVIP()) {
            VIPButton.setText("Cancel VIP");
            ControllerUtils.updateProfileImage(VIP_PROFILE_IMAGE, profileImage);
        } else {
            VIPButton.setText("Upgrade to VIP");
            ControllerUtils.updateProfileImage(DEFAULT_PROFILE_IMAGE, profileImage);
        }
    }

    // Delete's the user's account. All the posts owned by said user will also be cascade deleted
    private void deleteAccount() {
        if (StageManager.getInstance().displayRequestModal("Deleting your account will also delete all your posts.\nAre you sure?")) {
            try {
                new UserManager().delete(ActiveUser.getInstance());
                StageManager.getInstance().displayConfirmModal("Account deleted.");
                ActiveUser.clearInstance();
                StageManager.getInstance().switchScene(FxmlView.STARTUP);
            } catch (InvalidUserException e) {
                Logger.alertError(e.getMessage());
            }
        }
    }
}

