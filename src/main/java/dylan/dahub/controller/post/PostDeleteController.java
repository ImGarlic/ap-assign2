package dylan.dahub.controller.post;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.UserAuthenticationException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.Logger;
import dylan.dahub.view.StageManager;
import javafx.fxml.FXML;


public class PostDeleteController {

    private final StageManager stageManager = StageManager.getInstance();
    private int postID;

    @FXML
    protected void onOKButtonClick() {
        try {
            PostManager.delete(postID, ActiveUser.getInstance());
            stageManager.closeModal();
            stageManager.displayModal(FxmlView.MODAL_CONFIRM, false, "Post deleted successfully!");
        } catch (InvalidPostException e) {
            Logger.alertError("Failed to delete post: " + e.getMessage());
        } catch (UserAuthenticationException e) {
            stageManager.closeModal();
            stageManager.displayModal(FxmlView.MODAL_CONFIRM, false, "Failed to delete post: " + e.getMessage());
        }
    }

    @FXML
    protected void onCloseButtonClick() {
        stageManager.closeModal();
    }

    public void setPostID(String ID) {
        try {
            postID = Integer.parseInt(ID);
        } catch (NumberFormatException e) {
            Logger.alertError("Failed to parse post ID: " + e.getMessage());
        }

    }

}
