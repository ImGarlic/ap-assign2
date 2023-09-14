module dylan.dahub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens dylan.dahub to javafx.fxml;
    exports dylan.dahub;
    exports dylan.dahub.controller.profile;
    opens dylan.dahub.controller.profile to javafx.fxml;
    exports dylan.dahub.controller.startup;
    opens dylan.dahub.controller.startup to javafx.fxml;
    exports dylan.dahub.controller.main;
    opens dylan.dahub.controller.main to javafx.fxml;
    exports dylan.dahub.exception;
}