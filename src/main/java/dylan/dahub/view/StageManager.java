package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StageManager {

    private static StageManager INSTANCE;
    private static Stage rootStage;
    private static Stage modalStage;

    private StageManager(Stage stage) {
        rootStage = stage;
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

   public void switchScene(FxmlView view) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
        rootStage.setTitle(view.getTitle());
        rootStage.setScene(new Scene(root));
        rootStage.show();
   }

   public void displayModal(FxmlView view, boolean wait) throws IOException {
       modalStage = new Stage();
       Parent root = FXMLLoader.load(Objects.requireNonNull(DataAnalyticsHub.class.getResource(view.getFxmlFile())));
       modalStage.setTitle(view.getTitle());
       modalStage.setScene(new Scene(root));
       modalStage.initModality(Modality.WINDOW_MODAL);
       modalStage.initOwner(rootStage);
       if(wait) {
           modalStage.showAndWait();
       } else {
           modalStage.show();
       }
   }

   public void closeModal() {
        modalStage.close();
   }

}
