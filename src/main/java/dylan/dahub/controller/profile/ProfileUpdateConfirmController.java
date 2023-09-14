package dylan.dahub.controller.profile;

import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;

public class ProfileUpdateConfirmController {
    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }
}
