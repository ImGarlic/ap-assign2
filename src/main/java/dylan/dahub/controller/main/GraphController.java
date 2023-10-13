package dylan.dahub.controller.main;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Range;
import dylan.dahub.service.PostManager;
import dylan.dahub.view.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class GraphController {

    @FXML
    private PieChart sharesChart, sharesChart1;

    // Immediately generate the charts. At the moment the database calls are fast enough that we can generate
    // the graphs on page load. (Would probably look nicer if we had a fancy loading animation with threads tho)
    @FXML
    private void initialize() {
        try {
            ActiveUser activeUser = ActiveUser.getInstance();
            ObservableList<PieChart.Data> allPostsData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("0 - 99", new PostManager().getPostCount(
                                    activeUser.getID(), false, "shares", new Range(0, 99))),
                            new PieChart.Data("100 - 999", new PostManager().getPostCount(
                                    activeUser.getID(), false, "shares", new Range(100, 999))),
                            new PieChart.Data("1000+", new PostManager().getPostCount(
                                    activeUser.getID(), false, "shares", new Range(1000, Integer.MAX_VALUE))));

            ObservableList<PieChart.Data> myPostsData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("0 - 99", new PostManager().getPostCount(
                                    activeUser.getID(), true, "shares", new Range(0, 99))),
                            new PieChart.Data("100 - 999", new PostManager().getPostCount(
                                    activeUser.getID(), true, "shares", new Range(100, 999))),
                            new PieChart.Data("1000+", new PostManager().getPostCount(
                                    activeUser.getID(), true, "shares", new Range(1000, Integer.MAX_VALUE))));


            sharesChart.setData(allPostsData);
            sharesChart.setLabelsVisible(false);
            sharesChart.setTitle("Distribution of shares for ALL posts");

            sharesChart1.setData(myPostsData);
            sharesChart1.setLabelsVisible(false);
            sharesChart1.setTitle("Distribution of shares for MY posts");
        } catch (InvalidPostException e) {
            Logger.alertError("Falied to generate graphs: " + e.getMessage());
        }
    }
}
