package org.example.lager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SidebarView extends VBox {

    public enum Page {
        OVERVIEW,
        ITEMS,
        STORAGES
    }

    private final Button overviewBtn;
    private final Button itemsBtn;
    private final Button storagesBtn;
    private Page activePage = Page.OVERVIEW;

    public SidebarView(Consumer<Page> onNavigate) {
        super(8);
        setPrefWidth(200);
        setPadding(new Insets(18, 14, 18, 14));
        setStyle("-fx-background-color: " + UiTheme.SIDEBAR_BG + ";");

        Label brandLabel = new Label("Logistics App");
        brandLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        HBox brandHeader = new HBox(brandLabel);
        brandHeader.setPadding(new Insets(0, 0, 14, 4));

        Label sectionLabel = new Label("LOGISTICS");
        sectionLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #bdbdbd;");
        sectionLabel.setPadding(new Insets(8, 0, 2, 6));

        overviewBtn = createNavButton("Overview", Page.OVERVIEW, onNavigate);
        itemsBtn = createNavButton("Items", Page.ITEMS, onNavigate);
        storagesBtn = createNavButton("Storages", Page.STORAGES, onNavigate);

        getChildren().addAll(brandHeader, sectionLabel, overviewBtn, itemsBtn, storagesBtn);
        setActivePage(Page.OVERVIEW);
    }

    public void setActivePage(Page page) {
        this.activePage = page;
        overviewBtn.setStyle(page == Page.OVERVIEW ? UiTheme.NAV_ACTIVE_STYLE : UiTheme.NAV_IDLE_STYLE);
        itemsBtn.setStyle(page == Page.ITEMS ? UiTheme.NAV_ACTIVE_STYLE : UiTheme.NAV_IDLE_STYLE);
        storagesBtn.setStyle(page == Page.STORAGES ? UiTheme.NAV_ACTIVE_STYLE : UiTheme.NAV_IDLE_STYLE);
    }

    private Button createNavButton(String text, Page targetPage, Consumer<Page> onNavigate) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(UiTheme.NAV_IDLE_STYLE);

        btn.setOnAction(e -> onNavigate.accept(targetPage));
        btn.setOnMouseEntered(e -> {
            if (activePage != targetPage) {
                btn.setStyle(UiTheme.NAV_HOVER_STYLE);
            }
        });
        btn.setOnMouseExited(e -> setActivePage(activePage));
        return btn;
    }
}