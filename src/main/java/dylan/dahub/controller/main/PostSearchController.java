package dylan.dahub.controller.main;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Collections;

public class PostSearchController {

    @FXML
    private TextField searchBox;
    @FXML
    private AnchorPane postDisplay;
    @FXML
    private Label searchError;

    @FXML
    private void onSearch() {
        ControllerUtils.hideErrorLabels(new ArrayList<>(Collections.singletonList(searchError)));
        try {
            Post post = PostManager.getFromID(Integer.parseInt(searchBox.getText()));
            postDisplay.getChildren().add(ControllerUtils.createPostGraphic(post));
        } catch (InvalidPostException e) {
            ControllerUtils.showErrorLabel("That post doesn't exist!", searchError);
        } catch (NumberFormatException e) {
            ControllerUtils.showErrorLabel("Please enter a valid number", searchError);
        }
    }
}
