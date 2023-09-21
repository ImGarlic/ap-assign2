package dylan.dahub.controller.profile;

import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.User;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class VIPSetController {

    private final StageManager stageManager = StageManager.getInstance();
    @FXML
    private Label label1;

    @FXML
    protected void initialize() {
        if(ActiveUser.getInstance().isVIP()) {
            label1.setText("Would you like to cancel your VIP subscription?");
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
        User updatedUser = new User(ActiveUser.getInstance());
        String confirmationText;

        if (ActiveUser.getInstance().isVIP()) {
            updatedUser.setVIP(0);
            confirmationText = "Successfully cancelled your subscription!";
        } else {
            updatedUser.setVIP(1);
            confirmationText = "Successfully upgraded to VIP!";
        }

        try {
            ActiveUser.updateInstance(UserManager.update(updatedUser));
            stageManager.closeModal();
            stageManager.displayModal(FxmlView.MODAL_CONFIRM, false, confirmationText);
        } catch (InvalidUserException e) {
            // Should never reach here, since only changing VIP status
            stageManager.closeModal();
        }
    }

}
