GitHub link : https://github.com/ImGarlic/ap-assign2

Welcome to Data Analytics Hub by Dylan Marsh (student number 3784998)

This project was built on Intellij IDEA using maven, so it would be easiest to either clone from github (tested and 
works) or simply paste the zipped files (not tested) into an Intellij project.
This project uses SQLite as a database, so you will need to have the SQLite3 JDBC driver installed to use the 
application. Follow this guide https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/

Java version: 17.0.8.7
JavaFX version: 20.0.1
Database: SQLite3 with JDBC driver version 3.43.0.0


TESTED INSTALL/RUN PATH: open Intellij IDEA, go file -> new -> project from version control and enter the URL
https://github.com/ImGarlic/ap-assign2.git
Find src/main/java/DataAnalyticsHub.java file and right click -> run.
You may need to reload the project with maven.

The application structure follows the MVC pattern, and looks somewhat like:

┌──────────────────┐                                     ┌───────────────────┐
│                  │                                     │                   │
│   Stage Manager  │         ┌──────────────────┐ ◄──────┤   Model/Service   │                      
│                  │ ◄───────┤                  ├──────► │                   │
└────────┬─────────┘         │    Controller    │        └──────┬────────────┘
         │              ┌────┤                  │               │   ▲
         ▼              │    └──────────────────┘               ▼   │
┌──────────────────┐    │             ▲                  ┌──────────┴────────┐
│                  │    │             │                  │                   │
│   FXML UI view   │ ◄──┘             │                  │     Database      │
│                  │              ┌───┴────┐             │                   │
└──────────────────┘              │  User  │             └───────────────────┘
                                  └────────┘


In particular, there exists a StageManager singleton object that handles any changes of scene/stage including modal
popups. This helps immensely with navigating the different pages of the program.
The service classes are those that interact directly with the database, whereas the models are mainly objects that 
represent "physical" objects such as a post, or a user. The ActiveUser is an extended user that is the singleton
instance of the currently logged in user.