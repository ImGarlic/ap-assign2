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
    }, MENU {
        @Override
        String getTitle() {
            return "Menu";
        }

        @Override
        String getFxmlFile() {
            return "fxml/main/menu.fxml";
        }

        ;
    }, PROFILE {
        @Override
        String getTitle() {
            return "Profile";
        }

        @Override
        String getFxmlFile() {
            return "fxml/profile/profile.fxml";
        }
    }, PROFILE_UPDATE {
        @Override
        String getTitle() {
            return "Update Profile";
        }

        @Override
        String getFxmlFile() {
            return "fxml/profile/profile_update.fxml";
        }
    }, VIP_SET {
        @Override
        String getTitle() {
            return "VIP membership";
        }

        @Override
        String getFxmlFile() {
            return "fxml/profile/VIP_set.fxml";
        }
    }, VIP_CONFIRM {
        @Override
        String getTitle() {
            return "VIP membership";
        }

        @Override
        String getFxmlFile() {
            return "fxml/profile/VIP_confirm.fxml";
        }
    };


    abstract String getTitle();
    abstract String getFxmlFile();

}
