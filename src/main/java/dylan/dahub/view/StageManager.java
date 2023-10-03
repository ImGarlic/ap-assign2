package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import dylan.dahub.controller.modal.ModalConfirmController;
import dylan.dahub.controller.modal.ModalRequestController;
import dylan.dahub.controller.main.MainFrameController;
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

// The mainFrameController needs to be persisted in the StageManager instance for each of the functions to be
// easily accessible globally from each main screen.
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

            // When first switching to the main frame (on login/register), we need to intitalize the controller instance
            // and set the main screen to the dashboard.
            if (view == FxmlView.MAIN) {
                mainFrameController = loader.getController();
                setMainScreen(FxmlView.DASHBOARD);
            }


        } catch (NullPointerException | IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   // Only switches the main screen section of the Main Frame. This persists the Main Frame instance,
   // keeping the sidebar and header available within all pages on the app
   public void setMainScreen(FxmlView view) {
        try {
            // This catches any usages of setMainScreen without first setting the main frame
            if(mainFrameController == null) {
                switchScene(FxmlView.MAIN);
            }
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
   public void displayConfirmModal(String message) {
       FxmlView view = FxmlView.MODAL_CONFIRM;
        try {
            modalStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
            Parent root = fxmlLoader.load();
            ModalConfirmController modalConfirmController = fxmlLoader.getController();

            modalConfirmController.setMessage(message);

            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(rootStage);
            modalStage.showAndWait();

        } catch (NullPointerException | IOException e) {
            String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
            Logger.alertError(errMsg);
        }
   }

   public boolean displayRequestModal(String message) {
        FxmlView view = FxmlView.MODAL_REQUEST;
       try {
           modalStage = new Stage();
           FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
           Parent root = fxmlLoader.load();
           ModalRequestController modalRequestController = fxmlLoader.getController();

           modalRequestController.setMessage(message);

           modalStage.setScene(new Scene(root));
           modalStage.initModality(Modality.WINDOW_MODAL);
           modalStage.initOwner(rootStage);

           modalStage.showAndWait();

           return modalRequestController.getResponse();
       } catch (NullPointerException | IOException e) {
           String errMsg = String.format("Failed to navigate scene: %s\n", e.getMessage());
           Logger.alertError(errMsg);
       }
       return false;
   }

   public void closeModal() {
        modalStage.close();
   }

   public void updateMainFrameProfileDetails() {
        mainFrameController.updateProfileDetails();
   }

}
