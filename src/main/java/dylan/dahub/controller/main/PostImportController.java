package dylan.dahub.controller.main;

import dylan.dahub.controller.ControllerUtils;
import dylan.dahub.exception.InvalidFileException;
import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PostImportController {
    @FXML
    private Label fileName, fileError;
    @FXML
    private Button importButton;
    private ArrayList<Post> postList;

    @FXML
    private void onSelectFileButtonClick() {
        selectFile();
    }

    @FXML
    private void onImportButtonClick() {
        importPosts();
    }

    // Opens the OS file selector
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageManager.getInstance().getRootStage());
        ControllerUtils.hideErrorLabels(new ArrayList<>(Collections.singletonList(fileError)));

        try {
            fileName.setText(selectedFile.getName());
            postList = PostManager.readCSV(selectedFile);

            importButton.setDisable(false);
        } catch (InvalidFileException e) {
            ControllerUtils.showErrorLabel(e.getMessage(), fileError);
        } catch (NullPointerException e) {
            // If no file is selected, do nothing
        }
    }

    private void importPosts() {
        try {
            new PostManager().putMulti(ActiveUser.getInstance().getID(), postList);
            StageManager.getInstance().displayConfirmModal("Posts added successfully!");

            importButton.setDisable(true);
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to import posts: " + e.getMessage());
        }
    }
}
