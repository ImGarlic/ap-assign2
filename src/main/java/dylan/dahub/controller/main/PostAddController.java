package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;

public class PostAddController {
    private final StageManager stageManager = StageManager.getInstance();

    @FXML
    private AnchorPane postDisplay;
    @FXML
    private TextField author, dateTime, likes, shares;
    @FXML
    private TextArea content;
    @FXML
    private Label authorError, contentError, dateTimeError, likesError, sharesError;
    @FXML
    private PostController postController;

    @FXML
    private void initialize() {
        addListeners();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DataAnalyticsHub.class.getResource("fxml/post/post.fxml"));
            AnchorPane graphic = fxmlLoader.load();
            postController = fxmlLoader.getController();
            setDefaultPost();
            postDisplay.getChildren().add(graphic);
        } catch (IOException e) {
            Logger.alertError("Failed to generate post graphic: " + e.getMessage());
        }
    }

    @FXML
    protected void onAddButtonClick() {
        addPost();
    }

    private void addPost() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Arrays.asList(authorError, contentError, likesError, sharesError, dateTimeError)));
        if (validateInput()) {
            try {
                Post post = PostManager.generatePostFromTextFields(author.getText(), content.getText(), dateTime.getText(), likes.getText(), shares.getText());
                PostManager.put(ActiveUser.getInstance(), post);
                stageManager.displayConfirmModal("Post added to the collection.");
                clearTextFields();
                setDefaultPost();
            } catch (InvalidPostException e) {
                Logger.alertError("Couldn't add post: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        boolean valid = true;
        if (author.getText().equals("")) {
            ControllerUtils.showErrorLabel("Author cannot be empty", authorError);
            valid = false;
        }
        if (content.getLength() > 120) {
            ControllerUtils.showErrorLabel("Content must be <100 characters", contentError);
            return false;
        } else if (content.getText().equals("")) {
            ControllerUtils.showErrorLabel("Content cannot be empty", contentError);
            valid = false;
        }
        if (isNotNumeric(likes.getText())) {
            ControllerUtils.showErrorLabel("Please enter a valid number", likesError);
            valid = false;
        }
        if (isNotNumeric(shares.getText())) {
            ControllerUtils.showErrorLabel("Please enter a valid number", sharesError);
            valid = false;
        }
        if (!isValidDate(dateTime.getText())) {
            ControllerUtils.showErrorLabel("Use dd/MM/yyyy hh:mm", dateTimeError);
            valid = false;
        }
        return valid;
    }

    private void addListeners() {
        author.textProperty().addListener((observable, oldValue, newValue) -> {
            postController.setAuthor(newValue);
        });
        content.textProperty().addListener((observable, oldValue, newValue) -> {
            postController.setContent(newValue);
        });
        dateTime.textProperty().addListener((observable, oldValue, newValue) -> {
            postController.setDateTime(newValue);
        });
        likes.textProperty().addListener((observable, oldValue, newValue) -> {
            postController.setLikes(newValue);
        });
        shares.textProperty().addListener((observable, oldValue, newValue) -> {
            postController.setShares(newValue);
        });
    }

    private void clearTextFields() {
        author.setText("");
        content.setText("");
        dateTime.setText("");
        likes.setText("");
        shares.setText("");
    }

    private void setDefaultPost() {
        Post post = new Post(1, "author", "Start typing...", 0, 0, LocalDateTime.now());
        postController.setPost(post);
    }

    private boolean isNotNumeric(String numString) {
        try {
            Integer.parseInt(numString);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDateTime.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
