package dylan.dahub.view;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginPage {

    public void displayLogin(Stage stage) {
        stage.setScene(new Scene(new Button("Back"), 320, 240));
        stage.show();
    }
}
