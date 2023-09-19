package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class MainFrameController {
    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private AnchorPane header, mainScreen, sidebar;
    @FXML
    private Label tooltip1, tooltip2;
    @FXML
    private Tooltip tt1, tt2;
    @FXML
    private ImageView profileImage;
    @FXML
    private Button graphDataButton, bulkImportButton;

    @FXML
    private void initialize() {
        checkVIPStatus();
        sidebar.setTranslateX(-210);

        tt1.setShowDelay(Duration.millis(200));
        tt2.setShowDelay(Duration.millis(200));
    }

    @FXML
    private void onSidebarClick() {
        if (sidebar.getTranslateX() == 0) {
            hideSideBar();
        } else {
            showSideBar();
        }
    }

    @FXML
    private void onHomeClick() {
        stageManager.switchMainScreen(FxmlView.MENU);
    }
    @FXML
    protected void onProfileButtonClick() {
        hideSideBar();
        stageManager.switchMainScreen(FxmlView.PROFILE);
    }


    @FXML
    protected void onLogoutButtonClick() {
        ActiveUser.clearInstance();
        stageManager.switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onViewButtonClick() {

    }

    @FXML
    protected void onAddButtonClick() {
        stageManager.switchMainScreen(FxmlView.ADD_POST);
    }


    @FXML
    protected void onRemoveButtonClick() {

    }

    // Checks if the user is a VIP and displays the extra functions if they are.
    private void checkVIPStatus() {
        String VIP_PROFILE_IMAGE_URL = "image/VIP_profile.png";
        if (activeUser.isVIP()) {
            enableVIPButtons();
            try {
                Image vipProfileImage = new Image(Objects.requireNonNull(DataAnalyticsHub.class.getResourceAsStream(VIP_PROFILE_IMAGE_URL)));
                profileImage.setImage(vipProfileImage);

            } catch (NullPointerException e) {
                System.out.println("VIP profile image not found in resources");
            }
        }
    }

    private void enableVIPButtons() {
        graphDataButton.setDisable(false);
        bulkImportButton.setDisable(false);
        tooltip1.setDisable(true);
        tooltip2.setDisable(true);
    }

    private void showSideBar() {
        sidebar.setTranslateX(0);
        mainScreen.setOpacity(0.8);
        mainScreen.setDisable(true);
    }

    private void hideSideBar() {
        sidebar.setTranslateX(-210);
        mainScreen.setOpacity(1);
        mainScreen.setDisable(false);
    }
    public void switchScreen(AnchorPane screen) {
        hideSideBar();
        mainScreen.getChildren().clear();
        mainScreen.getChildren().add(screen);
    }
}
