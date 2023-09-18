package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

// StageManager is the singleton instance of the javaFX stage. Use switchScene to easily switch between scenes
// on the stage. Apart from any modals, only one stage should exist at a time.
// Intrinsically linked to the FxmlView enum; any new scenes need to be included there to be used.
public class StageManager {

    private static StageManager INSTANCE;
    private static Stage rootStage;
    private static Stage modalStage;

    private StageManager(Stage stage) {
        rootStage = stage;
        rootStage.setResizable(false);
    }

    public static StageManager getInstance() {
        return INSTANCE;
    }

    public static StageManager createInstance(Stage stage) {
        if (INSTANCE == null) {
            INSTANCE = new StageManager(stage);
        }
        return INSTANCE;
    }

   public void switchScene(FxmlView view) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
            rootStage.setTitle(view.getTitle());
            rootStage.setScene(new Scene(root));
            rootStage.show();
        } catch (NullPointerException | IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   // Display a second stage to act as a custom modal. Only one modal may exist at a time, so if the previous modal
   // is not closed, it will simply be overridden.
   public void displayModal(FxmlView view, boolean wait) {
        try {
            modalStage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
            modalStage.setTitle(view.getTitle());
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(rootStage);
            if (wait) {
                modalStage.showAndWait();
            } else {
                modalStage.show();
            }
        } catch (NullPointerException | IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   public void closeModal() {
        modalStage.close();
   }

}
