package dylan.dahub.controller.main;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.post.PostController;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MenuController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
    private boolean showOnlyUserPosts = true;

    @FXML
    private Label welcomeText, tooltip1, tooltip2;
    @FXML
    private Button profileButton, graphDataButton, bulkImportButton, removeButton;
    @FXML
    private Tooltip tt1, tt2;
    @FXML
    private ListView<Post> mainPostView;
    @FXML
    private ToggleGroup sortOptions;

    @FXML
    private void initialize() throws InvalidPostException {
        welcomeText.setText("Welcome, " + activeUser.getFirstName() + " " + activeUser.getLastName());
        tt1.setShowDelay(Duration.millis(200));
        tt2.setShowDelay(Duration.millis(200));
        checkVIPStatus();
        generatePostList();
        startToggleListener();
    }

    @FXML
    protected void onLogoutButtonClick() {
        ActiveUser.clearInstance();
        stageManager.switchScene(FxmlView.STARTUP);
    }

    @FXML
    protected void onViewButtonClick() {

    }

    @FXML
    protected void onAddButtonClick() throws InvalidPostException {

        PostManager.put(activeUser, new Post(10,  "A567VF", "Check out this epic film.",1000, 1587,  LocalDateTime.parse("01/06/2023 02:20", DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))));
        PostManager.put(activeUser, new Post(37221,  "3827F2","Are we into Christmas month already?!", 526, 25, LocalDateTime.parse("15/11/2022 11:30", DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))));
        PostManager.put(activeUser, new Post(382, "What a miracle!", "38726I", 2775, 13589, LocalDateTime.parse("12/02/2023 06:18", DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))));
        PostManager.put(activeUser, new Post(36778,  "1258XE", "Fantastic day today. Congratulations to all winners.",230, 1214, LocalDateTime.parse("06/06/2023 09:00", DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))));
    }


    @FXML
    protected void onRemoveButtonClick() {
        mainPostView.getItems().remove(mainPostView.getSelectionModel().getSelectedItem());
        ObservableList<Post> updatedList = mainPostView.getItems();
        mainPostView.setItems(updatedList);
    }

    @FXML
    protected void onProfileButtonClick() {
        stageManager.switchScene(FxmlView.PROFILE);
    }

    @FXML
    protected void onLoadMoreButtonClick() {
        mainPostView.getItems();
    }

    private void checkVIPStatus() {
        String VIP_PROFILE_IMAGE = "image/VIP_profile.png";
        if (activeUser.isVIP()) {
            enableVIPButtons();
            try {
                String image = Objects.requireNonNull(DataAnalyticsHub.class.getResource(VIP_PROFILE_IMAGE)).toExternalForm();
                profileButton.setStyle("/*noinspection CssUnknownTarget*/-fx-background-image: url('" + image + "');");

            } catch (NullPointerException e) {
                System.out.println("VIP profile image not found in resources");
            }
        }
    }

    private void enableVIPButtons() {
        graphDataButton.setDisable(false);
        bulkImportButton.setDisable(false);
        tooltip1.setDisable(true);
        tooltip2.setDisable(true);
    }

    private void generatePostList() {
        mainPostView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Post> call(ListView<Post> postListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Post item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setGraphic(createPostGraphic(item));
                        } else {
                            setGraphic(null);
                            setText("");
                        }
                    }
                };
            }
        });

        refreshMainPostList("date_time", showOnlyUserPosts);
    }

    private AnchorPane createPostGraphic(Post post) {
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

    private void startToggleListener() {
        sortOptions.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            RadioButton rb = (RadioButton) sortOptions.getSelectedToggle();

            if (rb.getText().equals("Date")) {
                refreshMainPostList("date_time", showOnlyUserPosts);
            } else if (rb.getText().equals("Likes")) {
                refreshMainPostList("likes", showOnlyUserPosts);
            } else if (rb.getText().equals("Shares")) {
                refreshMainPostList("shares", showOnlyUserPosts);
            }
        });
    }

    private void refreshMainPostList(String sort, boolean allUsers) {
        try {
            ArrayList<Post> collection = PostManager.getMulti(5, sort, activeUser.getID(), allUsers, 0);
            ObservableList<Post> sample = FXCollections.observableArrayList();
            sample.addAll(collection);
            mainPostView.setItems(sample);
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to generate post list: " + e.getMessage());
        }
    }

}
