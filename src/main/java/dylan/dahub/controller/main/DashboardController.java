package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.*;
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
    private Button loadMoreButton;
    @FXML
    private ListView<Post> mainPostView;
    @FXML
    private ToggleGroup sortOptions;
    @FXML
    private CheckBox onlyUserPostsCheck;
    @FXML
    private ToggleButton sortOrderToggle;
    @FXML
    private ImageView sortOrderIcon;

    @FXML
    private void initialize() {
        ActiveUser activeUser = ActiveUser.getInstance();
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());

        setPostCounts();
        generatePostList();
        startRadioListener();
        startCheckBoxListener();
        startToggleListener();
    }


    @FXML
    protected void onLoadMoreButtonClick() {
        loadMoreIntoPostList();
    }



    // Generate the initial list of posts to view. Set to 5 posts.
    private void generatePostList() {
        mainPostView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Post> call(ListView<Post> postListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Post item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setGraphic(ControllerUtils.createPostGraphic(item));
                        } else {
                            setGraphic(null);
                            setText("");
                        }
                    }
                };
            }
        });

        refreshMainPostList("timestamp");
    }

    // Refreshes the main post list. Makes a call to the database based on the specific search queries.
    private void refreshMainPostList(String sortType) {
        try {
            ArrayList<Post> collection = PostManager.getMulti(10, sortType, sortOrder, ActiveUser.getInstance().getID(), showOnlyUserPosts, 0);
            ObservableList<Post> newList = FXCollections.observableArrayList();
            newList.addAll(collection);
            mainPostView.setItems(newList);
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to generate post list: " + e.getMessage());
        }
        loadMoreButton.setText("Load More");
        loadMoreButton.setDisable(false);
    }

    // Loads 5 more posts onto the main post list. If there's less than 5, loads them all and disables the button.
    private void loadMoreIntoPostList() {
        ObservableList<Post> currentList = mainPostView.getItems();
        String sortType = (String) sortOptions.getSelectedToggle().getUserData();
        int offset = currentList.size();
        try {
            ArrayList<Post> collection = PostManager.getMulti(10, sortType, sortOrder, ActiveUser.getInstance().getID(), showOnlyUserPosts, offset);
            if (collection.size() == 0) {
                showAllLoadedText();
                return;
            }
            currentList.addAll(collection);
            mainPostView.setItems(currentList);
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to generate post list: " + e.getMessage());
        }
    }

    private void setPostCounts() {
        try {
            totalPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), false)));
            myPostsCount.setText(String.valueOf(PostManager.getPostCount(ActiveUser.getInstance().getID(), true)));
        } catch (InvalidPostException e) {
            Logger.alertError("Couldn't get post count: " + e.getMessage());
        }

    }

    private void showAllLoadedText() {
        loadMoreButton.setText("All loaded");
        loadMoreButton.setDisable(true);
    }

    // Makes a call to the database every time a radio is selected. Each radio option has "user data" mapped
    // to the sorting types: "timestamp", "likes", or "shares".
    private void startRadioListener() {
        sortOptions.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            refreshMainPostList((String) sortOptions.getSelectedToggle().getUserData());
        });
    }

    private void startCheckBoxListener(){
        onlyUserPostsCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showOnlyUserPosts = onlyUserPostsCheck.isSelected();
            refreshMainPostList((String) sortOptions.getSelectedToggle().getUserData());
        });
    }

    private void startToggleListener() {
        String ARROW_UP_IMAGE_URL = "image/arrow_up.png";
        String ARROW_DOWN_IMAGE_URL = "image/arrow_down.png";

        Image arrowUpIcon = new Image(Objects.requireNonNull(
                DataAnalyticsHub.class.getResourceAsStream(ARROW_UP_IMAGE_URL)));
        Image arrowDownIcon = new Image(Objects.requireNonNull(
                DataAnalyticsHub.class.getResourceAsStream(ARROW_DOWN_IMAGE_URL)));

        sortOrderToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (sortOrderToggle.isSelected()) {
                sortOrder = "ASC";
                sortOrderIcon.setImage(arrowUpIcon);
            } else {
                sortOrder = "DESC";
                sortOrderIcon.setImage(arrowDownIcon);
            }
            refreshMainPostList((String) sortOptions.getSelectedToggle().getUserData());
        });
    }

}
