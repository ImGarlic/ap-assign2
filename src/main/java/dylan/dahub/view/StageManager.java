package dylan.dahub.view;

import dylan.dahub.DataAnalyticsHub;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {

   public static void switchScene(ActionEvent event, FxmlView view) throws IOException {
       Parent root = FXMLLoader.load(DataAnalyticsHub.class.getResource(view.getFxmlFile()));
       Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
   }

}
