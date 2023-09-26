package dylan.dahub;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.DatabaseUtils;
import dylan.dahub.service.PostManager;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws InvalidUserException, InvalidPostException {

        stageManager = StageManager.createInstance(stage);
        setInitialScene();
//        overflowDatabase();
    }

    // Use this to set the initial scene, very useful for functional testing.
    // This should be set to STARTUP for regular use.
    private void setInitialScene() throws InvalidUserException {
        ActiveUser.createInstance(UserManager.getFromUsername("d"));
        stageManager.switchScene(FxmlView.MAIN);
    }

    private void overflowDatabase() throws InvalidPostException, InvalidUserException {
        Random rand = new Random();
        ArrayList<String> contentList = new ArrayList<>(Arrays.asList("I hate mondays",
                "This is a pointless message about nothing purely designed to waste our time reading it. Your*", "What on earth", "Im Dylan",
                "purple people eater", "For generating random numbers within a range using Math.random(), follow the steps below:",
                "Declare the minimum value of the range", "Use the formula to generate values with the min and the max value inclusive.", "Cringe",
                "I love software engineering!", "Whats your fav movie?", "Note: This method can only be used if you need an integer or float random value", ":)",
                "Billionaires should not exist", "bruh", "What has happened to Star Wars these days?", "I can't believe this just happened to me: my cat has thrown up on the couch >:("));
        ArrayList<String> authorList = new ArrayList<>(Arrays.asList("Dylan", "Garlic", "Andre", "Wilson", "Keegan", "Jono", "David (RIP)", "Tom", "Josh"));
        ArrayList<Post> postlist = new ArrayList<>();

        System.out.println(contentList.size());
        System.out.println(authorList.size());

        for (int i = 0; i < 1000; i++) {
            String content = contentList.get(rand.nextInt(contentList.size() - 1));
            String author = authorList.get(rand.nextInt(authorList.size() - 1));
            int likes = rand.nextInt(0, 20000000);
            int shares = rand.nextInt(0, 20000000);
            Post post = new Post(1, author, content, likes, shares, LocalDateTime.now());
//            postlist.add(post);
            PostManager.put(UserManager.getRandomUser(), post);
            System.out.println(String.format("Post %d: Likes: %d, Shares: %d", i, likes, shares));
        }
//        PostManager.putMulti(UserManager.getRandomUser(), postlist);
    }
    @Override
    public void stop() throws SQLException {
//        String query = "DELETE FROM Post";
//        Connection con = DatabaseUtils.getConnection();
//        Statement stmt = con.createStatement();
//        stmt.executeUpdate(query);
        System.out.println("Quitting");
    }

    public static void main(String[] args) {
        launch();
    }
}