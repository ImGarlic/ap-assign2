package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.model.Range;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DashboardController {
    private boolean showOnlyUserPosts = false;
    private String sortOrder = "DESC";

    @FXML
    private Label welcomeText, totalPostsCount, myPostsCount;

    @FXML
    private void initialize() {
        ActiveUser activeUser = ActiveUser.getInstance();
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());

        setPostCounts();
    }


    private void setPostCounts() {
        try {
            totalPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), false, "", new Range(0,0))));
            myPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), true, "", new Range(0,0))));
        } catch (InvalidPostException e) {
            Logger.alertError("Couldn't get post count: " + e.getMessage());
        }

    }

}
