package dylan.dahub.controller.modal;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// Basic modal that displays a message with a close button
public class ModalController {
    @FXML
    private Label message;

    @FXML
    private void onCloseButtonClick() {
        StageManager.getInstance().closeModal();
    }

    public void setMessage(String text) {
        message.setText(text);
    }
}
