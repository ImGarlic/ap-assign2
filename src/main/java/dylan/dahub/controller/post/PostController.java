package dylan.dahub.controller.post;

import dylan.dahub.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class PostController extends AnchorPane {

    @FXML
    private Label author, content, dateTime, likes, shares;

    public void setPost(Post post) {
        author.setText(post.getAuthor());
        content.setText(Post.formatContent(post.getContent()));
        dateTime.setText(post.getDateTimeString());
        likes.setText(Post.formatNumber(post.getLikes()));
        shares.setText(Post.formatNumber(post.getShares()));
    }

}
