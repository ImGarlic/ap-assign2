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
    }, MAIN {
        @Override
        String getTitle() {
            return "Add a post";
        }

        @Override
        String getFxmlFile() {
            return "fxml/main/main_frame.fxml";
        }
    },DASHBOARD {
        @Override
        String getTitle() {
            return "Menu";
        }

        @Override
        String getFxmlFile() {
            return "fxml/main/dashboard.fxml";
        }
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
    }, CHANGE_PASSWORD {
        @Override
        String getTitle() {
            return "Change Password";
        }

        @Override
        String getFxmlFile() {
            return "fxml/profile/change_password.fxml";
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
    }, POST_ADD {
        @Override
        String getTitle() {
            return "Add a post";
        }

        @Override
        String getFxmlFile() {
            return "fxml/main/post_add.fxml";
        }
    },  POST_SEARCH {
        @Override
        String getTitle() {
            return "Find a post";
        }

        @Override
        String getFxmlFile() {
            return "fxml/main/post_search.fxml";
        }
    },MODAL_CONFIRM {
        @Override
        String getTitle() {
            return "Confirmed";
        }

        @Override
        String getFxmlFile() {
            return "fxml/modal_confirm.fxml";
        }
    };


    abstract String getTitle();
    abstract String getFxmlFile();

}
