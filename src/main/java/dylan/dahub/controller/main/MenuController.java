package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;

public class MenuController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();

    @FXML
    private Label welcomeText;
    @FXML
    private Button profile;

    @FXML
    private void initialize() {
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());
        checkVIPStatus();
    }

    @FXML
    protected void onLogoutButtonClick() throws IOException {
        ActiveUser.clearInstance();
        stageManager.switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onViewButtonClick() throws IOException {

    }

    @FXML
    protected void onProfileButtonClick() throws IOException {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    private void checkVIPStatus() {
        if (activeUser.isVIP()) {
            String image = DataAnalyticsHub.class.getResource("image/VIP_profile.png").toExternalForm();
            profile.setStyle("-fx-background-image: url('" + image + "'); ");
        }
    }
}
