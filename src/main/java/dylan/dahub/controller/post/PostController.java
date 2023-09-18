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
        content.setText(post.getContent());
        dateTime.setText(post.getDateTimeString());
        likes.setText(Post.formatNumber(post.getLikes()));
        shares.setText(Post.formatNumber(post.getShares()));
    }

    public void setAuthor(String text) {
        author.setText(text);
    }

    public void setContent(String text) {
        content.setText(text);
    }

    public void setDateTime(String text) {
        dateTime.setText(text);
    }

    public void setLikes(String text) {
        setNum(text, likes);
    }

    public void setShares(String text) {
        setNum(text, shares);
    }

    private void setNum(String text, Label label) {
        try {
            label.setText(Post.formatNumber(Integer.parseInt(text)));
        } catch (NumberFormatException e) {
            if (text.chars().allMatch( Character::isDigit ) && !text.equals("")) {
                label.setText("1B+");
            } else {
                label.setText("0");
            }
        }
    }
}
