package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Objects;

public class MainFrameController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    private AnchorPane mainScreen, sidebar, outsideSpace;
    @FXML
    private Label tooltip1, tooltip2, usernameText;
    @FXML
    private Tooltip tt1, tt2;
    @FXML
    private ImageView profileImage;
    @FXML
    private Button graphDataButton, bulkImportButton;

    @FXML
    private void initialize() {
        updateProfileDetails();
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
        stageManager.setMainScreen(FxmlView.DASHBOARD);
    }

    @FXML
    protected void onProfileButtonClick() {
        hideSideBar();
        stageManager.setMainScreen(FxmlView.PROFILE);
    }

    @FXML
    protected void onDashboardButtonClick() {
        stageManager.setMainScreen(FxmlView.DASHBOARD);
    }

    @FXML
    protected void onSearchButtonClick() {
        stageManager.setMainScreen(FxmlView.POST_VIEW);
    }

    @FXML
    protected void onAddButtonClick() {
        stageManager.setMainScreen(FxmlView.POST_ADD);
    }

    @FXML
    protected void onGraphButtonClick() {
        stageManager.setMainScreen(FxmlView.GRAPH);
    }

    @FXML
    protected void onLogoutButtonClick() {
        ActiveUser.clearInstance();
        stageManager.switchScene(FxmlView.STARTUP);
    }

    public void updateProfileDetails() {
        checkVIPStatus();
        checkUserName();
    }

    private void checkUserName() {
        usernameText.setText(ActiveUser.getInstance().getUserName());
    }

    // Checks if the user is a VIP and displays the extra functions if they are.
    private void checkVIPStatus() {
        ActiveUser activeUser = ActiveUser.getInstance();
        String VIP_PROFILE_IMAGE_URL = "image/VIP_profile.png";
        String DEFAULT_PROFILE_IMAGE_URL = "image/default_profile.png";

        if (activeUser.isVIP()) {
            setVIPStatus(VIP_PROFILE_IMAGE_URL, true);
        } else {
            setVIPStatus(DEFAULT_PROFILE_IMAGE_URL, false);
        }
    }

    private void setVIPStatus(String imageURl, boolean enabled) {
        setVIPButtons(enabled);
        try {
            Image vipProfileImage = new Image(Objects.requireNonNull(
                    DataAnalyticsHub.class.getResourceAsStream(imageURl)));
            profileImage.setImage(vipProfileImage);

        } catch (NullPointerException e) {
            System.out.println("Profile image not found in resources: " + imageURl);
        }
    }

    private void setVIPButtons(boolean enabled) {
        graphDataButton.setDisable(!enabled);
        bulkImportButton.setDisable(!enabled);
        tooltip1.setDisable(enabled);
        tooltip2.setDisable(enabled);
    }

    private void showSideBar() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(200));
        slide.setNode(sidebar);
        slide.setToX(0);
        slide.play();

        mainScreen.setOpacity(0.8);
        mainScreen.setDisable(true);

        outsideSpace.setOnMouseClicked(event -> {
            hideSideBar();
        });
    }

    private void hideSideBar() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(200));
        slide.setNode(sidebar);
        slide.setToX(-210);
        slide.play();

        mainScreen.setOpacity(1);
        mainScreen.setDisable(false);

        outsideSpace.setOnMouseClicked(event -> {
        });
    }

    public void switchScreen(AnchorPane screen) {
        hideSideBar();
//        FadeTransition fadeIn = new FadeTransition(Duration.millis(50), mainScreen);
//        fadeIn.setFromValue(0);
//        fadeIn.setToValue(1);
//
//        FadeTransition fadeOut = new FadeTransition(Duration.millis(50), mainScreen);
//        fadeOut.setFromValue(1);
//        fadeOut.setToValue(0);
//        fadeOut.setOnFinished(event -> {
//            mainScreen.getChildren().clear();
//            mainScreen.getChildren().add(screen);
//            fadeIn.play();
//        });
//        fadeOut.play();

        mainScreen.getChildren().clear();
        mainScreen.getChildren().add(screen);
    }

}
