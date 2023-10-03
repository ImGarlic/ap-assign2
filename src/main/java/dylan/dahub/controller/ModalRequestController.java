package dylan.dahub.controller;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ModalRequestController {

    private boolean response;

    @FXML
    private Label message;

    @FXML
    private void onOKButtonClick() {
        response = true;
        StageManager.getInstance().closeModal();
    }

    @FXML
    private void onCloseButtonClick() {
        response = false;
        StageManager.getInstance().closeModal();
    }

    public void setMessage(String text) {
        message.setText(text);
    }

    public boolean getResponse() {
        return response;
    }

}
