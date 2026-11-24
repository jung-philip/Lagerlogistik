package org.example.lager;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.example.lager.model.Item;

import java.sql.SQLException;

public class LogisticsApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        private final Database database = new Database();
        private final ObservableList<Item> items = FXCollections.observableArrayList();

        try {
            database.initialize();
            items.setAll(database.loadAllItems());
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
