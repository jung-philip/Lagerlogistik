package org.example.lager.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.lager.model.Storage;
import org.example.lager.repository.StorageRepository;

import java.util.List;

public class StoragesView extends VBox {

    private final StorageRepository storageRepository;
    private final Runnable onDataChanged;

    private final ObservableList<Storage> storageList = FXCollections.observableArrayList();
    private final TableView<Storage> table = new TableView<>(storageList);
    private final TextField editNameField = new TextField();
    private final TextField editLocationField = new TextField();
    private Storage selectedStorageForEdit;

    public StoragesView(StorageRepository storageRepository, Runnable onDataChanged) {
        this.storageRepository = storageRepository;
        this.onDataChanged = onDataChanged;

        setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");

        HBox header = UiTheme.createPageHeader("Storages");
        HBox createRow = createAddStorageForm();
        configureTable();
        HBox editBar = createEditStorageBar();

        VBox content = new VBox(14, createRow, table, editBar);
        content.setPadding(new Insets(20, 24, 20, 24));
        content.setStyle("-fx-background-color: " + UiTheme.MAIN_BG + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
    }

    public void setStorages(List<Storage> storages) {
        storageList.setAll(storages);
    }

    private HBox createAddStorageForm() {
        TextField newNameField = new TextField();
        newNameField.setPromptText("Storage Name");

        TextField newLocationField = new TextField();
        newLocationField.setPromptText("Location");

        Button addBtn = UiTheme.createPrimaryButton("Add Storage");
        addBtn.setOnAction(e -> {
            String name = newNameField.getText().trim();
            String location = newLocationField.getText().trim();
            if (name.isEmpty() || location.isEmpty()) {
                showAlert("Validation Error", "Please enter both name and location.");
                return;
            }
            Storage s = new Storage();
            s.setName(name);
            s.setLocation(location);
            if (storageRepository.insert(s)) {
                newNameField.clear();
                newLocationField.clear();
                onDataChanged.run();
            }
        });

        HBox row = new HBox(8, newNameField, newLocationField, addBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void configureTable() {
        table.setStyle("-fx-background-color: transparent; -fx-table-cell-border-color: transparent;");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Storage, Number> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(65);
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));

        TableColumn<Storage, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(220);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Storage, String> locCol = new TableColumn<>("Location");
        locCol.setPrefWidth(280);
        locCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));

        TableColumn<Storage, Void> editCol = new TableColumn<>("Action");
        editCol.setPrefWidth(110);
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = UiTheme.createPrimaryButton("Edit");

            {
                editBtn.setOnAction(e -> loadStorageIntoEditor(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, locCol, editCol);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadStorageIntoEditor(newVal);
            }
        });
    }

    private HBox createEditStorageBar() {
        editNameField.setPromptText("Name");
        editLocationField.setPromptText("Location");

        Button saveBtn = UiTheme.createPrimaryButton("Save Changes");
        saveBtn.setOnAction(e -> {
            if (selectedStorageForEdit == null) {
                showAlert("Selection", "Select a storage to edit.");
                return;
            }
            String name = editNameField.getText().trim();
            String location = editLocationField.getText().trim();
            if (name.isEmpty() || location.isEmpty()) {
                showAlert("Validation Error", "Name and location cannot be empty.");
                return;
            }
            selectedStorageForEdit.setName(name);
            selectedStorageForEdit.setLocation(location);
            if (storageRepository.update(selectedStorageForEdit)) {
                onDataChanged.run();
            }
        });

        Button deleteBtn = UiTheme.createDangerButton("Delete Storage");
        deleteBtn.setOnAction(e -> {
            if (selectedStorageForEdit == null) {
                showAlert("Selection", "Select a storage to delete.");
                return;
            }
            if (storageRepository.delete(selectedStorageForEdit.getId())) {
                selectedStorageForEdit = null;
                editNameField.clear();
                editLocationField.clear();
                onDataChanged.run();
            }
        });

        HBox bar = new HBox(8, new Label("Edit Storage:"), editNameField, editLocationField, saveBtn, deleteBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private void loadStorageIntoEditor(Storage storage) {
        this.selectedStorageForEdit = storage;
        editNameField.setText(storage.getName());
        editLocationField.setText(storage.getLocation());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}