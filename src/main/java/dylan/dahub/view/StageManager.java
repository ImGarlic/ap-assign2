package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StageManager {

    private static StageManager INSTANCE;
    private static Stage rootStage;

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
        Scene scene = new Scene(root);
        rootStage.setTitle(view.getTitle());
        rootStage.setScene(scene);
        rootStage.show();
   }

}
