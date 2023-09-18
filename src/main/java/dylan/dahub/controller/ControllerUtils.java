package dylan.dahub.controller;

import javafx.scene.control.Label;

import java.util.ArrayList;

public class ControllerUtils {

    public static void showErrorLabel(String text, Label errorLabel) {
        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }

    public static void hideErrorLabels(ArrayList<Label> labels) {
        for (Label label : labels) {
            label.setVisible(false);
        }
    }
}
