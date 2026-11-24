package org.example.lager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.lager.model.Item;
import org.example.lager.model.Storage;

import java.util.List;

public class OverviewView extends VBox {

    private final Label totalItemsLabel = new Label("0");
    private final Label totalStoragesLabel = new Label("0");
    private final Label belowMinLabel = new Label("0");

    public OverviewView() {
        setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");

        HBox header = UiTheme.createPageHeader("Overview");

        VBox itemsCard = createStatCard("Items", totalItemsLabel);
        VBox storagesCard = createStatCard("Storages", totalStoragesLabel);
        VBox belowMinCard = createStatCard("Below MinInventory", belowMinLabel);

        HBox cardsRow = new HBox(24, itemsCard, storagesCard, belowMinCard);
        cardsRow.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(24, cardsRow);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
    }

    public void updateStats(List<Item> items, List<Storage> storages) {
        long belowMinCount = items.stream()
                .filter(item -> item.getQuantity() < item.getMinInventory())
                .count();

        totalItemsLabel.setText(String.valueOf(items.size()));
        totalStoragesLabel.setText(String.valueOf(storages.size()));
        belowMinLabel.setText(String.valueOf(belowMinCount));

        if (belowMinCount > 0) {
            belowMinLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.DANGER_RED + ";");
        } else {
            belowMinLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: " + UiTheme.TEXT_DARK + ";");
        }
    }

    private VBox createStatCard(String title, Label countLabel) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + UiTheme.TEXT_MUTED + ";");

        countLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: " + UiTheme.TEXT_DARK + ";");

        VBox card = new VBox(8, titleLbl, countLabel);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setPrefWidth(235);
        card.setPrefHeight(115);
        card.setStyle(
                "-fx-background-color: " + UiTheme.SURFACE_WHITE + ";" +
                        "-fx-border-color: " + UiTheme.BORDER_DARK + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );
        return card;
    }
}