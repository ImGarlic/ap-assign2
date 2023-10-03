package dylan.dahub.controller.modal;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ModalConfirmController {
    @FXML
    private Label message;

    @FXML
    protected void onCloseButtonClick() {
        StageManager.getInstance().closeModal();
    }

    @FXML
    public void setMessage(String text) {
        message.setText(text);
    }
}
