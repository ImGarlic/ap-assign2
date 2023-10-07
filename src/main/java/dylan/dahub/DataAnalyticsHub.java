package dylan.dahub;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.exception.InvalidUserException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Post;
import dylan.dahub.service.PostManager;
import dylan.dahub.service.UserManager;
import dylan.dahub.view.FxmlView;
import dylan.dahub.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DataAnalyticsHub extends Application {
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws InvalidUserException {

        stageManager = StageManager.createInstance(stage);
        setInitialScene();
//        overflowDatabase();
    }

    // Use this to set the initial scene, very useful for functional testing.
    // This should be set to STARTUP for regular use.
    private void setInitialScene() throws InvalidUserException {
        ActiveUser.createInstance(UserManager.getFromUsername("ru"));
        stageManager.setMainScreen(FxmlView.POST_IMPORT);
    }

    private void overflowDatabase() throws InvalidPostException, InvalidUserException {
        Random rand = new Random();
        ArrayList<String> contentList = new ArrayList<>(Arrays.asList("I hate mondays",
                "This is a pointless message about nothing purely designed to waste our time reading it. Your*", "What on earth", "Im Dylan",
                "purple people eater", "For generating random numbers within a range using Math.random(), follow the steps below:",
                "Declare the minimum value of the range", "Use the formula to generate values with the min and the max value inclusive.", "Cringe",
                "I love software engineering!", "Whats your fav movie?", "Note: This method can only be used if you need an integer or float random value", ":)",
                "Billionaires should not exist", "bruh", "What has happened to Star Wars these days?", "I cant believe this just happened to me: my cat has thrown up on the couch >:(",
                "Come and meet us at Building 14 of RMIT.", "Check out this epic film.", "Are we into Christmas month already?!", "What a miracle!", "Fantastic day today. Congratulations to all winners."));
        ArrayList<String> authorList = new ArrayList<>(Arrays.asList("Dylan", "Garlic", "Andre", "Wilson", "Keegan", "Jono", "David (RIP)", "Tom", "Josh"));

        System.out.println(contentList.size());
        System.out.println(authorList.size());

        for (int i = 0; i < 1000; i++) {
            String content = contentList.get(rand.nextInt(contentList.size() - 1));
            String author = authorList.get(rand.nextInt(authorList.size() - 1));
            int likes = rand.nextInt(0, 2000);
            int shares = rand.nextInt(0, 2000);
            Post post = new Post(1, author, content, likes, shares, LocalDateTime.now());
            PostManager.put(UserManager.getRandomUser().getID(), post);
            System.out.printf("Post %d: Likes: %d, Shares: %d%n", i, likes, shares);
        }
    }
    @Override
    public void stop() {
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