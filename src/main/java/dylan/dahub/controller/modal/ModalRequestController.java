package dylan.dahub.controller.modal;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

// A modal that gives the user a choice. Use getResponse() after displaying the modal to
// determine the user's response to the choice
public class ModalRequestController extends ModalController {

    private boolean response;

    @FXML
    private void onOKButtonClick() {
        response = true;
        StageManager.getInstance().closeModal();
    }

    public boolean getResponse() {
        return response;
    }

}
