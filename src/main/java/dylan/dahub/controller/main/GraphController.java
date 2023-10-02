package dylan.dahub.controller.main;

import dylan.dahub.exception.InvalidPostException;
import dylan.dahub.model.ActiveUser;
import dylan.dahub.model.Range;
import dylan.dahub.service.PostManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class GraphController {

    @FXML
    private PieChart sharesChart, sharesChart1;

    @FXML
    private void initialize() throws InvalidPostException {
        ActiveUser activeUser = ActiveUser.getInstance();
        ObservableList<PieChart.Data> allPostsData =
                FXCollections.observableArrayList(
                        new PieChart.Data("0 - 99", PostManager.getPostCount(activeUser.getID(), false, "shares", new Range(0, 99))),
                        new PieChart.Data("100 - 999", PostManager.getPostCount(activeUser.getID(), false, "shares", new Range(100, 999))),
                        new PieChart.Data("1000+", PostManager.getPostCount(activeUser.getID(), false, "shares", new Range(1000, Integer.MAX_VALUE))));

        ObservableList<PieChart.Data> myPostsData =
                FXCollections.observableArrayList(
                        new PieChart.Data("0 - 99", PostManager.getPostCount(activeUser.getID(), true, "shares", new Range(0, 99))),
                        new PieChart.Data("100 - 999", PostManager.getPostCount(activeUser.getID(), true, "shares", new Range(100, 999))),
                        new PieChart.Data("1000+", PostManager.getPostCount(activeUser.getID(), true, "shares", new Range(1000, Integer.MAX_VALUE))));


        sharesChart.setData(allPostsData);
        sharesChart.setLabelsVisible(false);
        sharesChart.setTitle("Distribution of shares for ALL posts");

        sharesChart1.setData(myPostsData);
        sharesChart1.setLabelsVisible(false);
        sharesChart1.setTitle("Distribution of shares for MY posts");
    }
}
