package dylan.dahub.controller.main;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.model.Range;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class DashboardController {
    @FXML
    private Label welcomeText, totalPostsCount, myPostsCount;
    @FXML
    private AnchorPane postDisplay;

    @FXML
    private void initialize() {
        ActiveUser activeUser = ActiveUser.getInstance();
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());

        setPostCounts();
        startPostDisplay();
    }

    private void setPostCounts() {
        try {
            totalPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), false, "", new Range(0,0))));
            myPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), true, "", new Range(0,0))));
        } catch (InvalidPostException e) {
            Logger.alertError("Couldn't get post count: " + e.getMessage());
        }
    }

    private void startPostDisplay() {
        changePost();
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(5),
                        event -> {
                            changePost();
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void changePost() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), postDisplay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), postDisplay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            try {
                Post post = PostManager.getRandomPost();
                postDisplay.getChildren().clear();
                postDisplay.getChildren().add(ControllerUtils.createPostGraphic(post));
            } catch (InvalidPostException e) {
                Logger.alertError("Failed to create post graphic: " + e.getMessage());
            }
            fadeIn.play();
        });
        fadeOut.play();
    }
}
