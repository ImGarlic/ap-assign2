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

import java.io.IOException;
public class ProfileController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
    @FXML
    private Label username, fullname;
    @FXML
    private Button VIPButton;
    @FXML
    private ImageView profilePic;


    @FXML
    protected void initialize() {
        username.setText(activeUser.getUserName());
        fullname.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
        checkVIPStatus();
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.MENU);
    }

    @FXML
    protected void onVIPButtonClick() throws IOException {
        stageManager.displayModal(FxmlView.VIP_SET, true);
        checkVIPStatus();
    }

    @FXML
    protected void onUpdateProfileButtonClick() {
    }

    private void checkVIPStatus() {
        if (activeUser.isVIP()) {
            VIPButton.setText("Cancel VIP");
            changeProfilePic("image/VIP_profile_upscaled.png");
        } else {
            VIPButton.setText("Upgrade to VIP");
            changeProfilePic("image/default_profile_upscaled.png");
        }
    }

    private void changeProfilePic(String imageURL) {
        String VIPProfilePicURL = DataAnalyticsHub.class.getResource(imageURL).toString();
        Image image = new Image(VIPProfilePicURL);
        profilePic.setImage(image);
    }
}
