package dylan.dahub.controller.profile;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.exception.InvalidPostException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ProfileController {
    @FXML
    private Label username, fullname;
    @FXML
    private Button VIPButton;
    @FXML
    private ImageView profilePic;


    @FXML
    protected void initialize() {
        ActiveUser activeUser = ActiveUser.getInstance();
        username.setText(activeUser.getUserName());
        fullname.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
        checkVIPStatus();
    }

    @FXML
    protected void onVIPButtonClick() {
        String requestMessage, confirmMessage;
        User updatedUser = ActiveUser.getInstance();
        if(ActiveUser.getInstance().isVIP()) {
            requestMessage = "Would you like to cancel you VIP subscription?";
            confirmMessage = "Successfully cancelled your subscription!";
            updatedUser.setVIP(0);
        } else {
            requestMessage = "Would you like to upgrade your membership to VIP?";
            confirmMessage = "Successfully upgraded to VIP!";
            updatedUser.setVIP(1);
        }

        if(StageManager.getInstance().displayRequestModal(requestMessage)) {
            try {
                ActiveUser.updateInstance(UserManager.update(updatedUser));
                StageManager.getInstance().displayConfirmModal(confirmMessage);
            } catch (InvalidUserException e) {
                Logger.alertError("Failed to update VIP status: " + e.getMessage());
            }

        }
        checkVIPStatus();
        StageManager.getInstance().updateMainFrameProfileDetails();
    }

    @FXML
    protected void onUpdateProfileButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.PROFILE_UPDATE);
    }

    @FXML
    protected void onChangePasswordButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.CHANGE_PASSWORD);
    }

    private void checkVIPStatus() {
        String VIP_PROFILE_IMAGE = "image/VIP_profile_upscaled.png";
        String DEFAULT_PROFILE_IMAGE = "image/default_profile_upscaled.png";

        if (ActiveUser.getInstance().isVIP()) {
            VIPButton.setText("Cancel VIP");
            changeProfilePic(VIP_PROFILE_IMAGE);
        } else {
            VIPButton.setText("Upgrade to VIP");
            changeProfilePic(DEFAULT_PROFILE_IMAGE);
        }
    }

    private void changeProfilePic(String imageURL) {
        try {
            String VIPProfilePicURL = Objects.requireNonNull(DataAnalyticsHub.class.getResource(imageURL)).toString();
            Image image = new Image(VIPProfilePicURL);
            profilePic.setImage(image);
        } catch (NullPointerException e) {
            System.out.println("Failed to update profile image");
        }
    }
}
