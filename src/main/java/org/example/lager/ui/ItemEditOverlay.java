package org.example.lager.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.lager.model.Item;
import org.example.lager.model.Storage;
import org.example.lager.repository.ItemRepository;

import java.util.List;

public class ItemEditOverlay extends StackPane {

    private final ItemRepository itemRepository;
    private final Runnable onSaved;

    private final ObservableList<Storage> storageList = FXCollections.observableArrayList();
    private final ComboBox<Storage> storageCombo = UiTheme.createStorageComboBox(storageList);

    public ItemEditOverlay(ItemRepository itemRepository, Runnable onSaved) {
        this.itemRepository = itemRepository;
        this.onSaved = onSaved;

        setVisible(false);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        setOnMouseClicked(e -> {
            if (e.getTarget() == this) {
                close();
            }
        });
    }

    public void setStorages(List<Storage> storages) {
        storageList.setAll(storages);
    }

    public void open(Item item, boolean focusStock) {
        if (item == null) {
            return;
        }

        Label titleLabel = new Label("Edit Item #" + item.getId());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.TEXT_DARK + ";");

        Label nameLbl = new Label("Name");
        nameLbl.setStyle("-fx-text-fill: " + UiTheme.TEXT_MUTED + "; -fx-font-size: 12px;");
        TextField nameInput = new TextField(item.getName());

        Label stockLbl = new Label("Stock (Quantity)");
        stockLbl.setStyle("-fx-text-fill: " + UiTheme.TEXT_MUTED + "; -fx-font-size: 12px;");
        TextField quantityInput = new TextField(String.valueOf(item.getQuantity()));
        quantityInput.setPrefWidth(110);

        Button minusBtn = new Button("-1");
        Button plusBtn = new Button("+1");
        minusBtn.setOnAction(e -> quantityInput.setText(String.valueOf(Math.max(0, parseIntOrZero(quantityInput.getText()) - 1))));
        plusBtn.setOnAction(e -> quantityInput.setText(String.valueOf(parseIntOrZero(quantityInput.getText()) + 1)));

        HBox stockRow = new HBox(8, quantityInput, minusBtn, plusBtn);
        stockRow.setAlignment(Pos.CENTER_LEFT);

        Label minLbl = new Label("MinInventory");
        minLbl.setStyle("-fx-text-fill: " + UiTheme.TEXT_MUTED + "; -fx-font-size: 12px;");
        TextField minInput = new TextField(String.valueOf(item.getMinInventory()));

        Label storageLbl = new Label("Storage");
        storageLbl.setStyle("-fx-text-fill: " + UiTheme.TEXT_MUTED + "; -fx-font-size: 12px;");
        storageCombo.setValue(null);
        if (item.getStorage() != null) {
            storageList.stream()
                    .filter(s -> s.getId() == item.getStorage().getId())
                    .findFirst()
                    .ifPresent(storageCombo::setValue);
        }

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> close());

        Button saveBtn = UiTheme.createPrimaryButton("Save");
        saveBtn.setOnAction(e -> {
            try {
                String updatedName = nameInput.getText().trim();
                if (updatedName.isEmpty()) {
                    showAlert("Validation Error", "Name cannot be empty.");
                    return;
                }
                int updatedQuantity = Integer.parseInt(quantityInput.getText().trim());
                int updatedMin = Integer.parseInt(minInput.getText().trim());

                item.setName(updatedName);
                item.setQuantity(updatedQuantity);
                item.setMinInventory(updatedMin);
                item.setStorage(storageCombo.getValue());

                if (itemRepository.update(item)) {
                    close();
                    onSaved.run();
                } else {
                    showAlert("Database Error", "Could not update item.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Stock and MinInventory must be valid integers.");
            }
        });

        HBox buttonsRow = new HBox(10, cancelBtn, saveBtn);
        buttonsRow.setAlignment(Pos.CENTER_RIGHT);

        VBox modalCard = new VBox(
                10,
                titleLabel,
                nameLbl, nameInput,
                stockLbl, stockRow,
                minLbl, minInput,
                storageLbl, storageCombo,
                buttonsRow
        );
        modalCard.setMaxWidth(420);
        modalCard.setPadding(new Insets(22));
        modalCard.setStyle(
                "-fx-background-color: " + UiTheme.SURFACE_WHITE + ";" +
                        "-fx-border-color: " + UiTheme.BORDER_DARK + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );

        getChildren().setAll(modalCard);
        StackPane.setAlignment(modalCard, Pos.CENTER);
        setVisible(true);

        if (focusStock) {
            quantityInput.requestFocus();
            quantityInput.selectAll();
        }
    }

    public void close() {
        setVisible(false);
        getChildren().clear();
    }

    private int parseIntOrZero(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}