package it.unicam.cs.mpgc.rpg129518.gui;

import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/** Finestra del negozio: elenca gli oggetti acquistabili e delega l'acquisto a {@link Game}. */
public class ShopDialog extends Dialog<Void> {

    public ShopDialog(Game game) {
        setTitle("Negozio del villaggio");

        Label goldLabel = new Label();
        updateGold(goldLabel, game);

        ListView<Item> list = new ListView<>(FXCollections.observableArrayList(game.getShopItems()));
        list.setPrefHeight(320);
        list.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getName() + " (" + item.getDetails() + ") - " + item.getValue() + " oro");
            }
        });

        Button buy = new Button("Compra");
        buy.setOnAction(e -> {
            Item selected = list.getSelectionModel().getSelectedItem();
            if (selected != null) {
                game.buy(selected.getId());
                updateGold(goldLabel, game);
            }
        });

        getDialogPane().setContent(new VBox(8, goldLabel, list, buy));
        getDialogPane().setPrefWidth(460);
        getDialogPane().getStylesheets().add(ShopDialog.class.getResource("/style.css").toExternalForm());
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    private static void updateGold(Label label, Game game) {
        label.setText("Il tuo oro: " + game.getPlayer().getGold());
    }
}
