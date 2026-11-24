package org.example.lager.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.lager.model.Item;
import org.example.lager.model.Storage;
import org.example.lager.repository.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemsView extends VBox {

    private final ItemRepository itemRepository;
    private final Runnable onDataChanged;
    private final BiConsumer<Item, Boolean> onOpenItemOverlay;

    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final ObservableList<Storage> storageList = FXCollections.observableArrayList();
    private final Map<Integer, BooleanProperty> selectionMap = new HashMap<>();
    private final Label itemCountLabel = new Label("Total Items: 0");

    public ItemsView(ItemRepository itemRepository,
                     Runnable onDataChanged,
                     BiConsumer<Item, Boolean> onOpenItemOverlay) {
        this.itemRepository = itemRepository;
        this.onDataChanged = onDataChanged;
        this.onOpenItemOverlay = onOpenItemOverlay;

        setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");

        HBox header = UiTheme.createPageHeader("Items");

        itemCountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UiTheme.TEXT_DARK + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox infoBar = new HBox(12, spacer, itemCountLabel);
        infoBar.setAlignment(Pos.CENTER_LEFT);

        HBox addForm = createAddItemForm();
        TableView<Item> table = createItemTable();
        HBox bulkBar = createBulkActionsBar();

        VBox content = new VBox(14, infoBar, addForm, table, bulkBar);
        content.setPadding(new Insets(20, 24, 20, 24));
        content.setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
    }

    public void setData(List<Item> items, List<Storage> storages) {
        itemList.setAll(items);
        storageList.setAll(storages);
        itemCountLabel.setText("Total Items: " + items.size());
    }

    private HBox createAddItemForm() {
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setPrefWidth(175);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(95);

        TextField minInvField = new TextField();
        minInvField.setPromptText("MinInventory");
        minInvField.setPrefWidth(115);

        ComboBox<Storage> storageCombo = UiTheme.createStorageComboBox(storageList);
        storageCombo.setPromptText("Storage");

        Button addBtn = UiTheme.createPrimaryButton("Add Item");
        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showAlert("Validation Error", "Please enter an item name.");
                    return;
                }
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int minInventory = Integer.parseInt(minInvField.getText().trim());

                Item item = new Item();
                item.setName(name);
                item.setQuantity(quantity);
                item.setMinInventory(minInventory);
                item.setStorage(storageCombo.getValue());

                if (itemRepository.insert(item)) {
                    nameField.clear();
                    quantityField.clear();
                    minInvField.clear();
                    storageCombo.getSelectionModel().clearSelection();
                    onDataChanged.run();
                } else {
                    showAlert("Database Error", "Could not insert item.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Quantity and MinInventory must be valid integers.");
            }
        });

        HBox form = new HBox(8, nameField, quantityField, minInvField, storageCombo, addBtn);
        form.setAlignment(Pos.CENTER_LEFT);
        return form;
    }

    private TableView<Item> createItemTable() {
        TableView<Item> table = new TableView<>(itemList);
        table.setEditable(true);
        table.setStyle("-fx-background-color: transparent; -fx-table-cell-border-color: transparent;");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Item, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setPrefWidth(65);
        selectCol.setCellValueFactory(c -> getSelectionProperty(c.getValue().getId()));
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        TableColumn<Item, Number> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Item, Number> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setPrefWidth(95);
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        qtyCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                Item current = getTableRow().getItem();
                setText(String.valueOf(value.intValue()));
                if (current.getQuantity() < current.getMinInventory()) {
                    setStyle("-fx-text-fill: " + UiTheme.DANGER_RED + "; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: " + UiTheme.TEXT_DARK + ";");
                }
            }
        });

        TableColumn<Item, Number> minCol = new TableColumn<>("MinInventory");
        minCol.setPrefWidth(115);
        minCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMinInventory()));

        TableColumn<Item, String> storageCol = new TableColumn<>("Storage");
        storageCol.setPrefWidth(210);
        storageCol.setCellValueFactory(c -> {
            Storage s = c.getValue().getStorage();
            String label = (s != null) ? s.getName() + " (" + s.getLocation() + ")" : "Unassigned";
            return new SimpleStringProperty(label);
        });

        TableColumn<Item, Void> editCol = new TableColumn<>("Edit");
        editCol.setPrefWidth(90);
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = UiTheme.createPrimaryButton("Edit");

            {
                editBtn.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    onOpenItemOverlay.accept(item, false);
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        table.getColumns().addAll(selectCol, idCol, nameCol, qtyCol, minCol, storageCol, editCol);

        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else if (!isSelected()) {
                        setStyle("-fx-background-color: " + UiTheme.MAIN_BG + "; -fx-border-color: #e2e2e2; -fx-border-width: 0 0 1 0;");
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    onOpenItemOverlay.accept(row.getItem(), true);
                }
            });
            return row;
        });

        return table;
    }

    private HBox createBulkActionsBar() {
        ComboBox<Storage> bulkStorageCombo = UiTheme.createStorageComboBox(storageList);
        bulkStorageCombo.setPromptText("New Storage");

        Button changeStorageBtn = UiTheme.createPrimaryButton("Change Storage");
        changeStorageBtn.setOnAction(e -> {
            List<Item> selected = getCheckedItems();
            if (selected.isEmpty()) {
                showAlert("Selection", "Please check at least one item.");
                return;
            }
            Storage targetStorage = bulkStorageCombo.getValue();
            for (Item item : selected) {
                item.setStorage(targetStorage);
                itemRepository.update(item);
            }
            clearChecks();
            onDataChanged.run();
        });

        Button deleteSelectedBtn = UiTheme.createDangerButton("Delete Selected");
        deleteSelectedBtn.setOnAction(e -> {
            List<Item> selected = getCheckedItems();
            if (selected.isEmpty()) {
                showAlert("Selection", "Please check at least one item.");
                return;
            }
            for (Item item : selected) {
                itemRepository.delete(item.getId());
            }
            clearChecks();
            onDataChanged.run();
        });

        HBox bar = new HBox(10, new Label("Selected items:"), bulkStorageCombo, changeStorageBtn, deleteSelectedBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private BooleanProperty getSelectionProperty(int itemId) {
        return selectionMap.computeIfAbsent(itemId, k -> new SimpleBooleanProperty(false));
    }

    private List<Item> getCheckedItems() {
        return itemList.stream()
                .filter(item -> getSelectionProperty(item.getId()).get())
                .toList();
    }

    private void clearChecks() {
        selectionMap.values().forEach(prop -> prop.set(false));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}