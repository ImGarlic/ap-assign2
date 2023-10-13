package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PostViewController {
    private Thread loadingThread;

    @FXML
    private Label selectedID;
    @FXML
    private Button loadMoreButton, deleteButton, exportButton;
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
        startListViewListener();
        startCheckBoxListener();
        startSearchBarListener();
        startChoiceBoxListener();
        startToggleListener();

        generatePostList();
    }

    @FXML
    private void onDeleteButtonClick() {
        deletePosts(mainPostView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void onExportButtonClick() {
        exportPosts(mainPostView.getSelectionModel().getSelectedItems());
    }

    @FXML
    protected void onLoadMoreButtonClick() {
        loadMoreIntoPostList();
    }


    // Attempts to delete the currently selected post on the list
    private void deletePosts(ObservableList<Post> selectedPosts) {
        if(StageManager.getInstance().displayRequestModal("Are you sure you want to delete this post?")) {
            try {
                for (Post post : selectedPosts) {
                    new PostManager().delete(
                            ActiveUser.getInstance().getID(), post.ID());
                }
                if (selectedPosts.size() > 1) {
                    StageManager.getInstance().displayConfirmModal(selectedPosts.size() + " posts successfully deleted.");
                } else {
                    StageManager.getInstance().displayConfirmModal("Post successfully deleted.");
                }

                refreshMainPostList();
            } catch (InvalidPostException | NumberFormatException e) {
                Logger.alertError("Failed to delete post: " + e.getMessage());
            } catch (UserAuthenticationException e) {
                StageManager.getInstance().displayConfirmModal("You can only delete your own posts from the database.");
            }
        }
    }

    // Attempts to export the currently selected posts to a csv of specified location
    private void exportPosts(ObservableList<Post> selectedPosts) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".csv",".csv"));

        File file = fileChooser.showSaveDialog(StageManager.getInstance().getRootStage());

        try (FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write("ID,author,content,likes,shares,date-time\n");
            for (Post post : selectedPosts) {
                fileWriter.write(post.toString() + "\n");
            }
            StageManager.getInstance().displayConfirmModal(selectedPosts.size() + " posts successfully exported to " + file.getName());
        } catch (NullPointerException e) {
            // Do nothing if no file
        } catch (IOException e) {
            Logger.alertError("Failed to export posts: " + e.getMessage());
        }

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

        mainPostView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshMainPostList();
    }

    // Refreshes the main post list to 10 posts based on the filter/search options.
    // Makes a call to the database for each call. Opens a separate thread to load the list after
    // the page has loaded.
    // To avoid multiple threads, the previous thread is killed if it exists.
    private void refreshMainPostList() {
        if(loadingThread != null && loadingThread.isAlive()) {
            loadingThread.interrupt();
        }
        mainPostView.getItems().clear();

        Task<ObservableList<Post>> listLoader = new Task<>() {
            {
                setOnRunning( workerStateEvent -> mainPostView.setPlaceholder(new ProgressIndicator()));
                setOnSucceeded( workerStateEvent -> {
                    mainPostView.setItems(getValue());
                    mainPostView.setPlaceholder(new Label("Nothing to show :("));
                });
                setOnFailed( workerStateEvent -> {
                    Logger.alertError("Failed to load list: " + getException().getMessage());
                    mainPostView.setPlaceholder(new Label("Something went wrong :("));
                });
            }

            @Override
            protected ObservableList<Post> call() throws InvalidPostException {
                try {
                    // Since the database call is actaully rather quick, this simulates some sense of loading.
                    Thread.sleep(500);

                    ArrayList<Post> collection = new PostManager().getMulti(
                            ActiveUser.getInstance().getID(),
                            10,
                            searchBar.getText(),
                            (String)sortOptions.getUserData(),
                            (String)sortOrderToggle.getUserData(),
                            (boolean)onlyUserPostsCheck.getUserData(),
                            0);

                    ObservableList<Post> newList = FXCollections.observableArrayList();
                    newList.addAll(collection);

                    return newList;
                } catch (InterruptedException e) {
                    // Only interrupts the sleep, so no need to do anything.
                }
                return FXCollections.observableArrayList();
            }
        };

        loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();

        loadMoreButton.setText("Load More");
        loadMoreButton.setDisable(false);
    }

    // Loads 10 more posts onto the main post list. If there's less than 10, loads them all and disables the button.
    private void loadMoreIntoPostList() {
        ObservableList<Post> currentList = mainPostView.getItems();
        int offset = currentList.size();

        try {
            ArrayList<Post> collection = new PostManager().getMulti(
                    ActiveUser.getInstance().getID(),
                    10,
                    searchBar.getText(),
                    (String)sortOptions.getUserData(),
                    (String)sortOrderToggle.getUserData(),
                    (boolean)onlyUserPostsCheck.getUserData(),
                    offset);

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

    // Starts a listener for the selected post on the list.
    private void startListViewListener() {
        mainPostView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Post>) observable -> {
            ObservableList<Post> selectedItems = mainPostView.getSelectionModel().getSelectedItems();

            if(selectedItems.size() == 1) {
                selectedID.setText("Selected ID: " + selectedItems.get(0).ID());
                deleteButton.setDisable(false);
                exportButton.setDisable(false);
            } else if(selectedItems.size() == 0) {
                selectedID.setText("Selected ID: ");
                deleteButton.setDisable(true);
                exportButton.setDisable(true);
            } else {
                selectedID.setText("Multiple selected");
                deleteButton.setDisable(false);
                exportButton.setDisable(false);
            }
        });
    }

    // Starts a listener to refresh the list as user is typing
    private void startSearchBarListener() {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> refreshMainPostList());
    }

    // Starts a listner to refresh the list on a selection of the sort options
    // Uses user data rather than the display text for the database interactions
    private void startChoiceBoxListener() {
        sortOptions.getItems().addAll("Date", "Likes", "Shares");
        // Sets default
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

    // Starts a listner to refresh the list on a selection for only the user posts
    private void startCheckBoxListener() {
        // Sets default
        onlyUserPostsCheck.setUserData(true);

        onlyUserPostsCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            onlyUserPostsCheck.setUserData(onlyUserPostsCheck.isSelected());
            refreshMainPostList();
        });
    }

    // Starts a listner to refresh the list on a switch of the sort order (asc/desc)
    private void startToggleListener() {
        // Sets default
        sortOrderToggle.setUserData("DESC");
        String ARROW_UP_IMAGE_URL = "image/arrow_up.png";
        String ARROW_DOWN_IMAGE_URL = "image/arrow_down.png";

        Image arrowUpIcon = new Image(Objects.requireNonNull(
                DataAnalyticsHub.class.getResourceAsStream(ARROW_UP_IMAGE_URL)));
        Image arrowDownIcon = new Image(Objects.requireNonNull(
                DataAnalyticsHub.class.getResourceAsStream(ARROW_DOWN_IMAGE_URL)));

        sortOrderToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (sortOrderToggle.isSelected()) {
                sortOrderToggle.setUserData("ASC");
                sortOrderIcon.setImage(arrowUpIcon);
            } else {
                sortOrderToggle.setUserData("DESC");
                sortOrderIcon.setImage(arrowDownIcon);
            }
            refreshMainPostList();
        });
    }

}
