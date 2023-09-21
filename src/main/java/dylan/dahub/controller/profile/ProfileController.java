package dylan.dahub.controller.profile;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ProfileController {

    private final StageManager stageManager = StageManager.getInstance();
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
        stageManager.displayModal(FxmlView.VIP_SET, true, "");
        checkVIPStatus();
        stageManager.checkVipStatus();
    }

    @FXML
    protected void onUpdateProfileButtonClick() {
        stageManager.switchMainScreen(FxmlView.PROFILE_UPDATE);
    }

    @FXML
    protected void onChangePasswordButtonClick() {
        stageManager.switchMainScreen(FxmlView.CHANGE_PASSWORD);
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
