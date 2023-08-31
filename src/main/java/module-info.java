module dylan.dahub {
    requires javafx.controls;
    requires javafx.fxml;


    opens dylan.dahub to javafx.fxml;
    exports dylan.dahub;
}