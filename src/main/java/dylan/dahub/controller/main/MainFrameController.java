package dylan.dahub.controller.main;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

// The main frame consists of the header, sidebar and the main screen. While logged in, the main frame is
// persisted to allow for access to the header/sidebar functions from all pages without having to
// reload. The main screen is where each separate page is loaded.
public class MainFrameController {

    @FXML
    private AnchorPane mainScreen, sidebar, outsideSpace;
    @FXML
    private Label tooltip1, tooltip2, usernameText;
    @FXML
    private Tooltip tt1, tt2;
    @FXML
    private ImageView profileImage;
    @FXML
    private Button graphDataButton, importButton;

    @FXML
    private void initialize() {
        updateProfileDetails();
        sidebar.setTranslateX(-210);

        tt1.setShowDelay(Duration.millis(200));
        tt2.setShowDelay(Duration.millis(200));
    }

    // Opens the sidebar with a translation animation.
    @FXML
    private void onSidebarClick() {
        if (sidebar.getTranslateX() == 0) {
            hideSideBar();
        } else {
            showSideBar();
        }
    }

    // Takes you to the dashboard from anywhere
    @FXML
    private void onHomeClick() {
        StageManager.getInstance().setMainScreen(FxmlView.DASHBOARD);
    }

    @FXML
    private void onProfileButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.PROFILE);
    }

    @FXML
    private void onDashboardButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.DASHBOARD);
    }

    @FXML
    private void onSearchButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.POST_VIEW);
    }

    @FXML
    private void onAddButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.POST_ADD);
    }

    @FXML
    private void onGraphButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.GRAPH);
    }

    @FXML
    private void onImportButtonClick() {
        StageManager.getInstance().setMainScreen(FxmlView.POST_IMPORT);
    }

    @FXML
    private void onLogoutButtonClick() {
        ActiveUser.clearInstance();
        StageManager.getInstance().switchScene(FxmlView.STARTUP);
    }

    public void updateProfileDetails() {
        checkVIPStatus();
        checkUserName();
    }

    private void checkUserName() {
        usernameText.setText(ActiveUser.getInstance().getUserName());
    }

    // Checks if the user is a VIP, displays the extra functions and changes the profile pic if they are.
    private void checkVIPStatus() {
        ActiveUser activeUser = ActiveUser.getInstance();
        String VIP_PROFILE_IMAGE_URL = "image/VIP_profile.png";
        String DEFAULT_PROFILE_IMAGE_URL = "image/default_profile.png";

        if (activeUser.isVIP()) {
            setVIPButtons(true);
            ControllerUtils.updateProfileImage(VIP_PROFILE_IMAGE_URL, profileImage);
        } else {
            setVIPButtons(false);
            ControllerUtils.updateProfileImage(DEFAULT_PROFILE_IMAGE_URL, profileImage);
        }
    }

    private void setVIPButtons(boolean enabled) {
        graphDataButton.setDisable(!enabled);
        importButton.setDisable(!enabled);
        tooltip1.setDisable(enabled);
        tooltip2.setDisable(enabled);
    }

    private void showSideBar() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(200));
        slide.setNode(sidebar);
        slide.setToX(0);
        slide.play();

        mainScreen.setDisable(true);

        outsideSpace.setOnMouseClicked(event -> hideSideBar());
    }

    private void hideSideBar() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(200));
        slide.setNode(sidebar);
        slide.setToX(-210);
        slide.play();

        mainScreen.setDisable(false);

        outsideSpace.setOnMouseClicked(event -> {});
    }

    // Should only be called by the StageManager, where the controller is persisted.
    // Changes the main screen of the frame, loading different pages within the main frame instance.
    public void switchScreen(AnchorPane screen) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), mainScreen);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), mainScreen);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            hideSideBar();

            mainScreen.getChildren().clear();
            mainScreen.getChildren().add(screen);
            fadeIn.play();
        });
        fadeOut.play();
    }

}
