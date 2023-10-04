package dylan.dahub.controller;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.model.Post;
import dylan.dahub.view.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

// Generic class for commonly-used methods in controller classes
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

    public static void clearTextFields(ArrayList<TextField> textfields) {
        for (TextField textfield : textfields) {
            textfield.setText("");
        }
    }

    // Creates the nicely-formatted graphic to display for a post.
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

    public static void updateProfileImage(String imageURl, ImageView profileImage) {
        try {
            Image vipProfileImage = new Image(Objects.requireNonNull(
                    DataAnalyticsHub.class.getResourceAsStream(imageURl)));
            profileImage.setImage(vipProfileImage);

        } catch (NullPointerException e) {
            System.out.println("Profile image not found in resources: " + imageURl);
        }
    }

}
