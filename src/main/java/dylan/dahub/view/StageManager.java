package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.ConfirmationModalController;
import dylan.dahub.controller.main.MainFrameController;
import dylan.dahub.controller.post.PostDeleteController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

// StageManager is the singleton instance of the javaFX stage. Use switchScene to easily switch between scenes
// on the stage. Apart from any modals, only one stage should exist at a time.
// Intrinsically linked to the FxmlView enum; any new scenes need to be included there to be used.

// The mainFrameController needs to be persisted in the StageManager instance for each the functions to be
// easily accessible globally from each scene.
public class StageManager {

    private static StageManager INSTANCE;
    private static Stage rootStage, modalStage;
    private static MainFrameController mainFrameController;

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

    // Switches the whole scene for the stage
   public void switchScene(FxmlView view) {
        try {
            FXMLLoader loader = new FXMLLoader(DataAnalyticsHub.class.getResource(view.getFxmlFile()));
            Parent root = loader.load();
            rootStage.setTitle(view.getTitle());
            rootStage.setScene(new Scene(root));
            rootStage.show();

            if (view == FxmlView.MAIN) {
                mainFrameController = loader.getController();
                switchMainScreen(FxmlView.DASHBOARD);
            }


        } catch (NullPointerException | IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   // Only switches the main screen section of the Main Frame. This persists the Main Frame instance,
   // keeping the siderbar and header available within all pages on the app
   public void switchMainScreen(FxmlView view) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DataAnalyticsHub.class.getResource(view.getFxmlFile()));
            AnchorPane screen = fxmlLoader.load();
            mainFrameController.switchScreen(screen);
            rootStage.setTitle(view.getTitle());
        } catch (IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   // Display a second stage to act as a custom modal. Only one modal may exist at a time, so if the previous modal
   // is not closed, it will simply be overridden.
   public void displayModal(FxmlView view, boolean wait, String text) {
        try {
            modalStage = new Stage();
            FXMLLoader fxmLloader = new FXMLLoader(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
            Parent root = fxmLloader.load();

            if(view == FxmlView.MODAL_CONFIRM) {
                ConfirmationModalController confirmationModalController = fxmLloader.getController();
                confirmationModalController.setText(text);
            }

            if(view == FxmlView.POST_DELETE) {
                PostDeleteController postDeleteController = fxmLloader.getController();
                postDeleteController.setID(text);
            }

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

   public void updateMainFrameProfileDetails() {
        mainFrameController.updateProfileDetails();
   }

}
