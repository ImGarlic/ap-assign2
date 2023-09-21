package dylan.dahub.controller;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfirmationModalController {

    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    private Label label;

    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }

    @FXML
    public void setText(String text) {
        label.setText(text);
    }
}
