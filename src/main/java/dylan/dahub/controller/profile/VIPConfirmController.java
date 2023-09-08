package dylan.dahub.controller.profile;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class VIPConfirmController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
    @FXML
    private Label label1, label2;

    @FXML
    protected void initialize() {
        if(activeUser.isVIP()) {
            label1.setText("Successfully upgraded your");
            label2.setText("subscription to VIP!");
        }
    }

    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }

}
