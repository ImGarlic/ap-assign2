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
    opens dylan.dahub.controller.post to javafx.fxml;
    exports dylan.dahub.controller.post;
    exports dylan.dahub.model;
    exports dylan.dahub.controller;
    opens dylan.dahub.controller to javafx.fxml;
    exports dylan.dahub.controller.modal;
    opens dylan.dahub.controller.modal to javafx.fxml;
    exports dylan.dahub.service;
}