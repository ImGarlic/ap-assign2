package dylan.dahub.controller.profile;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManagement;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class VIPSetController {

    private final StageManager stageManager = StageManager.getInstance();
    private final ActiveUser activeUser = ActiveUser.getInstance();
    @FXML
    private Label label1, label2;

    @FXML
    protected void initialize() {
        if(activeUser.isVIP()) {
            label1.setText("Would you like to cancel");
            label2.setText("your VIP subscription?");
        }
    }

    @FXML
    protected void onOKButtonClick() {
        updateVIPStatus();
    }

    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }

    private void updateVIPStatus() {
        User updatedUser = new User(activeUser);

        if (activeUser.isVIP()) {
            updatedUser.setVIP(0);
        } else {
            updatedUser.setVIP(1);
        }

        try {
            ActiveUser.updateInstance(UserManagement.updateUser(updatedUser));
            stageManager.closeModal();
            stageManager.displayModal(FxmlView.VIP_CONFIRM, false);
        } catch (InvalidUserException e) {
            // Should never reach here, since only changing VIP status
            stageManager.closeModal();
        }
    }

}
