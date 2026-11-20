package it.unicam.cs.mpgc.rpg129518.gui;

import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.model.combat.Ability;
import it.unicam.cs.mpgc.rpg129518.model.combat.AbilityCatalog;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import it.unicam.cs.mpgc.rpg129518.gui.graphics.Sprites;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/** Schermata iniziale: nuova partita (nome + classe), continua, esci. */
public class MainMenuView extends VBox {

    public MainMenuView(Game game, Runnable onGameStarted) {
        setAlignment(Pos.CENTER);
        setSpacing(14);
        setPadding(new Insets(30));
        getStyleClass().add("menu-root");

        Label title = new Label("DUNGEON BUSTERS");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 44));
        title.getStyleClass().add("title");

        TextField nameField = new TextField("Eroe");
        nameField.setMaxWidth(260);
        nameField.setPromptText("Nome del personaggio");

        ComboBox<PlayerClass> classBox = new ComboBox<>(FXCollections.observableArrayList(PlayerClass.values()));
        classBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PlayerClass playerClass) {
                return playerClass == null ? "" : playerClass.getDisplayName();
            }

            @Override
            public PlayerClass fromString(String string) {
                return null;
            }
        });
        classBox.getSelectionModel().selectFirst();

        Canvas preview = new Canvas(360, 192);
        GraphicsContext previewContext = preview.getGraphicsContext2D();
        AnimationTimer previewTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                previewContext.clearRect(0, 0, preview.getWidth(), preview.getHeight());
                Sprites.drawPreview(previewContext, classBox.getValue(), now / 1_000_000_000.0);
            }
        };
        previewTimer.start();

        Label info = new Label(describe(classBox.getValue()));
        classBox.valueProperty().addListener((obs, old, selected) -> info.setText(describe(selected)));

        Button newGame = new Button("Nuova partita");
        newGame.setOnAction(e -> {
            String name = nameField.getText().isBlank() ? "Eroe" : nameField.getText().trim();
            previewTimer.stop();
            game.newGame(name, classBox.getValue());
            onGameStarted.run();
        });

        Button continueGame = new Button("Continua");
        continueGame.setDisable(!game.hasSave(classBox.getValue()));
        classBox.valueProperty().addListener((obs, old, selected) ->
                continueGame.setDisable(!game.hasSave(selected)));
        continueGame.setOnAction(e -> {
            if (game.loadGame(classBox.getValue())) {
                previewTimer.stop();
                onGameStarted.run();
            } else {
                new Alert(Alert.AlertType.ERROR, "Impossibile caricare il salvataggio.").showAndWait();
            }
        });

        Button exit = new Button("Esci");
        exit.setOnAction(e -> {
            previewTimer.stop();
            Platform.exit();
        });

        for (Button button : new Button[]{newGame, continueGame, exit}) {
            button.setPrefWidth(260);
        }

        getChildren().addAll(title, new Label("Crea il tuo eroe"), preview, nameField, classBox, info,
                newGame, continueGame, exit);
    }

    private static String describe(PlayerClass playerClass) {
        Ability ability = AbilityCatalog.forClass(playerClass);
        return "PV " + playerClass.getBaseHp() + " | MP " + playerClass.getBaseMana()
                + " | ATT " + playerClass.getBaseAttack() + " | DIF " + playerClass.getBaseDefense()
                + "\nAbilità: " + ability.getName() + " (" + ability.getManaCost() + " MP)";
    }
}
