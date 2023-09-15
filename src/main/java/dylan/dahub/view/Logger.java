package dylan.dahub.view;

import javafx.scene.control.Alert;

public class Logger {
    public static void alertError(String message) {
        System.out.println(message);
        Alert successAlert = new Alert(Alert.AlertType.ERROR);
        successAlert.setTitle("Error");
        successAlert.setHeaderText(message);
        successAlert.show();
    }
}
