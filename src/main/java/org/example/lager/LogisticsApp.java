package org.example.lager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.lager.model.Item;
import org.example.lager.model.Storage;
import org.example.lager.repository.ItemRepository;
import org.example.lager.repository.StorageRepository;
import org.example.lager.ui.ItemEditOverlay;
import org.example.lager.ui.ItemsView;
import org.example.lager.ui.OverviewView;
import org.example.lager.ui.SidebarView;
import org.example.lager.ui.StoragesView;
import org.example.lager.ui.UiTheme;

import java.util.List;

public class LogisticsApp extends Application {

    private final StorageRepository storageRepository = new StorageRepository();
    private final ItemRepository itemRepository = new ItemRepository();

    private final BorderPane layoutRoot = new BorderPane();
    private final SidebarView sidebarView = new SidebarView(this::navigateTo);
    private final OverviewView overviewView = new OverviewView();
    private final ItemsView itemsView = new ItemsView(itemRepository, this::refreshData, this::openItemOverlay);
    private final StoragesView storagesView = new StoragesView(storageRepository, this::refreshData);
    private final ItemEditOverlay itemEditOverlay = new ItemEditOverlay(itemRepository, this::refreshData);

    @Override
    public void start(Stage primaryStage) {
        Database.initialize();

        layoutRoot.setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");
        layoutRoot.setLeft(sidebarView);
        layoutRoot.setCenter(overviewView);

        StackPane sceneRoot = new StackPane(layoutRoot, itemEditOverlay);
        refreshData();

        Scene scene = new Scene(sceneRoot, 1080, 660);
        primaryStage.setTitle("Logistics Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void navigateTo(SidebarView.Page page) {
        itemEditOverlay.close();
        sidebarView.setActivePage(page);
        refreshData();

        switch (page) {
            case ITEMS -> layoutRoot.setCenter(itemsView);
            case STORAGES -> layoutRoot.setCenter(storagesView);
            case OVERVIEW -> layoutRoot.setCenter(overviewView);
        }
    }

    private void openItemOverlay(Item item, boolean focusStock) {
        itemEditOverlay.open(item, focusStock);
    }

    private void refreshData() {
        List<Storage> storages = storageRepository.findAll();
        List<Item> items = itemRepository.findAll();

        overviewView.updateStats(items, storages);
        itemsView.setData(items, storages);
        storagesView.setStorages(storages);
        itemEditOverlay.setStorages(storages);
    }
}