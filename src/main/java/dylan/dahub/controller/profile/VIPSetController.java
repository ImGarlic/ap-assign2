package dylan.dahub.controller.profile;

import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.UserManagement;
import dylan.dahub.view.ErrorDisplay;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;

public class VIPSetController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
    private int CHANGE_VIP_STATUS = 1;
    @FXML
    private Label label1, label2;

    @FXML
    protected void initialize() {
        if(activeUser.isVIP()) {
            CHANGE_VIP_STATUS = 0;
            label1.setText("Would you like to cancel");
            label2.setText("your VIP subscription?");
        }
    }

    @FXML
    protected void onOKButtonClick() throws IOException {
        updateVIPStatus();
    }

    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }

    private void updateVIPStatus() {
        activeUser.setVIP(CHANGE_VIP_STATUS);
        try {
            UserManagement.updateUser(activeUser);
            stageManager.closeModal();
            stageManager.displayModal(FxmlView.VIP_CONFIRM, false);
        } catch (SQLException | IOException e) {
            stageManager.closeModal();
            activeUser.setVIP(Math.abs(CHANGE_VIP_STATUS - 1));
            String message = String.format("Failed to update user: %s", e.getMessage());
            ErrorDisplay.alertError(message);
        }
    }

}
