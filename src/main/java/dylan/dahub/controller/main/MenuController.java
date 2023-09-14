package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.Objects;

public class MenuController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private Label welcomeText, tooltip1, tooltip2;
    @FXML
    private Button profileButton, graphDataButton, bulkImportButton;
    @FXML
    private Tooltip tt1, tt2;

    @FXML
    private void initialize() {
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());
        tt1.setShowDelay(Duration.millis(200));
        tt2.setShowDelay(Duration.millis(200));
        checkVIPStatus();
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
    protected void onProfileButtonClick() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    private void checkVIPStatus() {
        String VIP_PROFILE_IMAGE = "image/VIP_profile.png";
        if (activeUser.isVIP()) {
            enableVIPButtons();
            try {
                String image = Objects.requireNonNull(DataAnalyticsHub.class.getResource(VIP_PROFILE_IMAGE)).toExternalForm();
                profileButton.setStyle("/*noinspection CssUnknownTarget*/-fx-background-image: url('" + image + "');");

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

}
