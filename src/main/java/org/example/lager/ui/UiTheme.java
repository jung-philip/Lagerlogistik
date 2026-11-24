package org.example.lager.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.example.lager.model.Storage;

public final class UiTheme {

    public static final String SIDEBAR_BG = "#424242";
    public static final String SIDEBAR_HOVER = "#525252";
    public static final String ACCENT_SLATE = "#7d9098";
    public static final String MAIN_BG = "#efefef";
    public static final String SURFACE_WHITE = "#ffffff";
    public static final String BORDER_DARK = "#333333";
    public static final String TEXT_DARK = "#222222";
    public static final String TEXT_MUTED = "#666666";
    public static final String DANGER_RED = "#d32f2f";

    public static final String NAV_IDLE_STYLE =
            "-fx-background-color: transparent;" +
                    "-fx-text-fill: #e0e0e0;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8 12 8 12;" +
                    "-fx-cursor: hand;";

    public static final String NAV_HOVER_STYLE =
            "-fx-background-color: " + SIDEBAR_HOVER + ";" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8 12 8 12;" +
                    "-fx-cursor: hand;";

    public static final String NAV_ACTIVE_STYLE =
            "-fx-background-color: " + ACCENT_SLATE + ";" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8 12 8 12;" +
                    "-fx-cursor: hand;";

    public static final String PRIMARY_BTN_STYLE =
            "-fx-background-color: " + ACCENT_SLATE + ";" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 6 14 6 14;" +
                    "-fx-cursor: hand;";

    public static final String DANGER_BTN_STYLE =
            "-fx-background-color: " + DANGER_RED + ";" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 6 14 6 14;" +
                    "-fx-cursor: hand;";

    private UiTheme() {
    }

    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(PRIMARY_BTN_STYLE);
        return button;
    }

    public static Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setStyle(DANGER_BTN_STYLE);
        return button;
    }

    public static HBox createPageHeader(String titleText) {
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle(
                "-fx-background-color: " + SURFACE_WHITE + ";" +
                        "-fx-border-color: #d8d8d8;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return header;
    }

    public static ComboBox<Storage> createStorageComboBox(ObservableList<Storage> storages) {
        ComboBox<Storage> combo = new ComboBox<>(storages);
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Storage s) {
                return s == null ? "" : s.getName() + " (" + s.getLocation() + ")";
            }

            @Override
            public Storage fromString(String string) {
                return null;
            }
        });
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Storage s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + " (" + s.getLocation() + ")");
            }
        });
        return combo;
    }
}