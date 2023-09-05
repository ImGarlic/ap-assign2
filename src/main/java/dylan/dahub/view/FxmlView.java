package dylan.dahub.view;


public enum FxmlView {

    STARTUP {
        @Override
        String getTitle() {
            return "Data Analytics Hub";
        }

        @Override
        String getFxmlFile() {
            return "fxml/startup/startup.fxml";
        }

    }, LOGIN {
        @Override
        String getTitle() {
            return "Login";
        }

        @Override
        String getFxmlFile() {
            return "fxml/startup/login.fxml";
        }
    }, REGISTER {
        @Override
        String getTitle() {
            return "Register";
        }

        @Override
        String getFxmlFile() {
            return "fxml/startup/register.fxml";
        }
    };


    abstract String getTitle();
    abstract String getFxmlFile();

}
