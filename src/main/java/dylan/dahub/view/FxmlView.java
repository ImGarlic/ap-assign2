package dylan.dahub.view;

import java.util.ResourceBundle;

public enum FxmlView {

    STARTUP {
        @Override
        String getTitle() {
            return "Data Analytics Hub";
        }

        @Override
        String getFxmlFile() {
            return "fxml/startup.fxml";
        }

    }, LOGIN {
        @Override
        String getTitle() {
            return "Data Analytics Hub";
        }

        @Override
        String getFxmlFile() {
            return "fxml/login.fxml";
        }
    };


    abstract String getTitle();
    abstract String getFxmlFile();

}
