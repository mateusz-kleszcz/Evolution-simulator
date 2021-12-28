package agh.ics.oop.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class App extends Application {

    private final GridPane gridPane = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start (Stage primaryStage) {
        Scene scene = new Scene(this.gridPane, 1200, 780);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Settings");
        primaryStage.show();
    }

    // create settings menu and add it to scene on application start
    @Override
    public void init() throws Exception {
        super.init();
        for (int i = 0; i < 2; i++) {
            ColumnConstraints column = new ColumnConstraints(600);
            gridPane.getColumnConstraints().add(column);
        }
        SettingsMenu settingsMenu = new SettingsMenu(gridPane);
        settingsMenu.createSettingsMenu();
    }


}
