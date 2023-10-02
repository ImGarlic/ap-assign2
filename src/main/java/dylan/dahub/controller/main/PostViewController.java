package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Objects;

public class PostViewController {
    private boolean showOnlyUserPosts = false;
    private String sortOrder = "DESC";

    @FXML
    private Label selectedID;
    @FXML
    private Button loadMoreButton, deleteButton;
    @FXML
    private ListView<Post> mainPostView;
    @FXML
    private CheckBox onlyUserPostsCheck;
    @FXML
    private TextField searchBar;
    @FXML
    private ComboBox<String> sortOptions;
    @FXML
    private ToggleButton sortOrderToggle;
    @FXML
    private ImageView sortOrderIcon;

    @FXML
    private void initialize() {
        generatePostList();
        startListViewListener();
        startCheckBoxListener();
        startSearchBarListener();
        startChoiceBoxListener();
        startToggleListener();
    }

    @FXML
    private void onDeleteButtonClick() {

    }

    @FXML
    protected void onLoadMoreButtonClick() {
        loadMoreIntoPostList();
    }

    // Generate the initial list of posts to view. Set to 10 posts.
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

        refreshMainPostList();
    }

    // Refreshes the main post list. Makes a call to the database based on the specific search queries.
    private void refreshMainPostList() {
        try {
            ArrayList<Post> collection = PostManager.getMultiWithSearch(10, searchBar.getText(), (String)sortOptions.getUserData(), sortOrder, ActiveUser.getInstance().getID(), showOnlyUserPosts, 0);

            ObservableList<Post> newList = FXCollections.observableArrayList();
            newList.addAll(collection);
            mainPostView.setItems(newList);
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to generate post list: " + e.getMessage());
        }
        loadMoreButton.setText("Load More");
        loadMoreButton.setDisable(false);
    }

    // Loads 5 more posts onto the main post list. If there's less than 10, loads them all and disables the button.
    private void loadMoreIntoPostList() {
        ObservableList<Post> currentList = mainPostView.getItems();
        int offset = currentList.size();
        try {
            ArrayList<Post> collection = PostManager.getMultiWithSearch(10, searchBar.getText(), (String)sortOptions.getUserData(), sortOrder, ActiveUser.getInstance().getID(), showOnlyUserPosts, offset);
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

    private void showAllLoadedText() {
        loadMoreButton.setText("All loaded");
        loadMoreButton.setDisable(true);
    }

    private void startListViewListener() {
        mainPostView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                selectedID.setText("Selected ID: " + newValue.getID());
                deleteButton.setDisable(false);
            } else {
                selectedID.setText("Selected ID: ");
                deleteButton.setDisable(true);
            }
        });
    }

    private void startSearchBarListener() {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshMainPostList();
        });
    }

    private void startChoiceBoxListener() {
        sortOptions.getItems().addAll("Date", "Likes", "Shares");
        sortOptions.setUserData("timestamp");
        sortOptions.getSelectionModel().selectFirst();

        sortOptions.setOnAction(event -> {
            String val = sortOptions.getValue();
            switch (val) {
                case "Date" -> sortOptions.setUserData("timestamp");
                case "Likes" -> sortOptions.setUserData("likes");
                case "Shares" -> sortOptions.setUserData("shares");
            }
            refreshMainPostList();
        });
    }

    private void startCheckBoxListener() {
        onlyUserPostsCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showOnlyUserPosts = onlyUserPostsCheck.isSelected();
            refreshMainPostList();
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
            refreshMainPostList();
        });
    }

}
