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

import java.time.LocalDateTime;

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

    // Display the number of posts for each category on the home screen
    private void setPostCounts() {
        try {
            totalPostsCount.setText(String.valueOf(
                    new PostManager().getPostCount(
                            ActiveUser.getInstance().getID(), false, "likes", new Range(0,Integer.MAX_VALUE))
                    )
            );
            myPostsCount.setText(String.valueOf(
                    new PostManager().getPostCount(
                            ActiveUser.getInstance().getID(), true, "likes", new Range(0,Integer.MAX_VALUE))
                    )
            );
        } catch (InvalidPostException e) {
            Logger.alertError("Couldn't get post count: " + e.getMessage());
        }
    }

    // Starts infinitely cycling through randomly-selected posts from the database
    private void startPostDisplay() {
        changePost();
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(5),
                        event -> changePost()
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Changes the post display to the next randomly-selected post with a cool fade effect.
    private void changePost() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), postDisplay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), postDisplay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            try {
                Post post = new PostManager().getRandomPost();
                postDisplay.getChildren().clear();
                postDisplay.getChildren().add(ControllerUtils.createPostGraphic(post));
            } catch (InvalidPostException e) {
                Logger.alertError("Failed to create post graphic: " + e.getMessage());
            } catch (NullPointerException e) {
                Post defaultPost = new Post(0, "Missing", "Database seems to be empty!", 0, 0, LocalDateTime.now());
                postDisplay.getChildren().clear();
                postDisplay.getChildren().add(ControllerUtils.createPostGraphic(defaultPost));
            }
            fadeIn.play();
        });
        fadeOut.play();
    }
}
