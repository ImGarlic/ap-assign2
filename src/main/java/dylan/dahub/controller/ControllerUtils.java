package dylan.dahub.controller;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.model.Post;
import dylan.dahub.view.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;

public class ControllerUtils {

    public static void showErrorLabel(String text, Label errorLabel) {
        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }

    public static void hideErrorLabels(ArrayList<Label> labels) {
        for (Label label : labels) {
            label.setVisible(false);
        }
    }

    // Creates the nicely-formatted graphic to display each post in the main post list.
    public static AnchorPane createPostGraphic(Post post) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DataAnalyticsHub.class.getResource("fxml/post/post.fxml"));
            AnchorPane graphic = fxmlLoader.load();
            PostController postController = fxmlLoader.getController();
            postController.setPost(post);
            return graphic;
        } catch (IOException e) {
            Logger.alertError("Failed to generate post graphic: " + e.getMessage());
        }
        return null;
    }

}
