package it.unicam.cs.mpgc.rpg129518.gui;

import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.engine.GameListener;
import it.unicam.cs.mpgc.rpg129518.engine.GamePhase;
import it.unicam.cs.mpgc.rpg129518.model.combat.Ability;
import it.unicam.cs.mpgc.rpg129518.model.combat.Combat;
import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.item.Equippable;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import it.unicam.cs.mpgc.rpg129518.model.item.ItemStack;
import it.unicam.cs.mpgc.rpg129518.model.item.Potion;
import it.unicam.cs.mpgc.rpg129518.model.world.Direction;
import it.unicam.cs.mpgc.rpg129518.model.world.DungeonRun;
import it.unicam.cs.mpgc.rpg129518.gui.graphics.MapView;
import it.unicam.cs.mpgc.rpg129518.gui.graphics.SceneModel;
import it.unicam.cs.mpgc.rpg129518.gui.graphics.ScenePanel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Vista principale: osserva {@link Game} e si ridisegna a ogni cambio di stato.
 * Non contiene regole di gioco: tasti e pulsanti delegano a metodi di {@link Game}.
 *
 * Comandi: WASD / frecce = movimento; in combattimento 1 attacco, 2 abilità, 3 difesa, 4 fuga;
 * H = pozione di cura, M = pozione di mana.
 */
public class GameView extends BorderPane implements GameListener {

    private final Game game;
    private final Runnable onExit;
    private String lastTheme = "village";
    private String actionSignature = "";
    private List<ItemStack> shownInventory = List.of();

    // pannello giocatore
    private final Label playerTitle = new Label();
    private final Label statsLabel = new Label();
    private final Label equipLabel = new Label();
    private final Label goldLabel = new Label();
    private final ProgressBar hpBar = bar("#c0392b");
    private final ProgressBar manaBar = bar("#2980b9");
    private final ProgressBar xpBar = bar("#f1c40f");
    private final Label hpText = new Label();
    private final Label manaText = new Label();
    private final Label xpText = new Label();

    // pannello centrale
    private final MapView mapView;
    private final ScenePanel battleView = new ScenePanel();
    private final Label locationLabel = new Label();
    private final Label monsterLabel = new Label();
    private final FlowPane actionPane = new FlowPane(10, 10);

    // pannello inventario
    private final ListView<ItemStack> inventoryList = new ListView<>();
    private final Button useButton = new Button("Usa / Equipaggia");
    private final Button sellButton = new Button("Vendi");

    private final TextArea logArea = new TextArea();

    // input da tastiera
    private final EventHandler<KeyEvent> pressedFilter = this::onKeyPressed;
    private final EventHandler<KeyEvent> releasedFilter = this::onKeyReleased;
    private final ChangeListener<Boolean> focusListener = (obs, was, focused) -> {
        if (!focused) {
            releaseAllKeys();
        }
    };
    private Window attachedWindow;

    public GameView(Game game, Runnable onExit) {
        this.game = game;
        this.onExit = onExit;
        this.mapView = new MapView(game);
        getStyleClass().add("game-root");
        setPadding(new Insets(10));

        setLeft(buildPlayerPanel());
        setCenter(buildCenterPanel());
        setRight(buildInventoryPanel());
        setBottom(buildLogPanel());

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                detach(oldScene);
            }
            if (newScene != null) {
                attach(newScene);
            }
        });

        game.addListener(this);
        refresh();
        onMessage("Che l'avventura abbia inizio!");
    }

    // ================= costruzione pannelli =================

    private Node buildPlayerPanel() {
        playerTitle.setFont(Font.font("Serif", FontWeight.BOLD, 20));
        playerTitle.getStyleClass().add("title");
        VBox box = new VBox(8, playerTitle, goldLabel, statsLabel, equipLabel, new Separator(),
                new Label("Punti ferita"), hpBar, hpText,
                new Label("Mana"), manaBar, manaText,
                new Label("Esperienza"), xpBar, xpText);
        box.getStyleClass().add("panel");
        box.setPrefWidth(230);
        return box;
    }

    private Node buildCenterPanel() {
        locationLabel.setFont(Font.font("Serif", FontWeight.BOLD, 22));
        locationLabel.getStyleClass().add("title");
        monsterLabel.setFont(Font.font(14));
        actionPane.setPrefWrapLength(600);

        StackPane views = new StackPane(mapView, battleView);
        VBox.setVgrow(views, Priority.ALWAYS);
        VBox box = new VBox(10, views, locationLabel, monsterLabel, actionPane);
        box.setPadding(new Insets(0, 14, 0, 14));
        return box;
    }

    private Node buildInventoryPanel() {
        inventoryList.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(ItemStack stack, boolean empty) {
                super.updateItem(stack, empty);
                if (empty || stack == null) {
                    setText(null);
                } else {
                    Item item = stack.item();
                    setText(item.getName() + " x" + stack.quantity() + "\n   " + item.getDetails());
                }
            }
        });
        inventoryList.setFocusTraversable(false);
        inventoryList.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> updateInventoryButtons());

        useButton.setMaxWidth(Double.MAX_VALUE);
        useButton.setFocusTraversable(false);
        useButton.setOnAction(e -> withSelectedItem(game::useItem));
        sellButton.setMaxWidth(Double.MAX_VALUE);
        sellButton.setFocusTraversable(false);
        sellButton.setOnAction(e -> withSelectedItem(game::sell));

        Label title = new Label("Zaino");
        title.getStyleClass().add("title");
        VBox box = new VBox(8, title, inventoryList, useButton, sellButton);
        box.getStyleClass().add("panel");
        box.setPrefWidth(250);
        VBox.setVgrow(inventoryList, Priority.ALWAYS);
        BorderPane.setMargin(box, new Insets(0, 0, 0, 0));
        return box;
    }

    private Node buildLogPanel() {
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(5);
        logArea.setFocusTraversable(false);
        BorderPane.setMargin(logArea, new Insets(10, 0, 0, 0));
        return logArea;
    }

    // ================= tastiera =================

    private void attach(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
        attachedWindow = scene.getWindow();
        if (attachedWindow != null) {
            attachedWindow.focusedProperty().addListener(focusListener);
        }
    }

    private void detach(Scene scene) {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
        if (attachedWindow != null) {
            attachedWindow.focusedProperty().removeListener(focusListener);
            attachedWindow = null;
        }
    }

    private void onKeyPressed(KeyEvent event) {
        Direction direction = directionFor(event.getCode());
        if (direction != null) {
            mapView.press(direction);
            event.consume();
            return;
        }
        if (event.isConsumed() || event.isControlDown() || event.isAltDown()) {
            return;
        }
        boolean handled = true;
        switch (event.getCode()) {
            case DIGIT1, NUMPAD1 -> game.attack();
            case DIGIT2, NUMPAD2 -> game.useAbility();
            case DIGIT3, NUMPAD3 -> game.defend();
            case DIGIT4, NUMPAD4 -> game.flee();
            case H -> quickUse(potion -> potion.getHealAmount() > 0);
            case M -> quickUse(potion -> potion.getManaAmount() > 0 && potion.getHealAmount() == 0);
            default -> handled = false;
        }
        if (handled) {
            event.consume();
        }
    }

    private void releaseAllKeys() {
        mapView.clearHeld();
    }

    private void onKeyReleased(KeyEvent event) {
        Direction direction = directionFor(event.getCode());
        if (direction != null) {
            mapView.release(direction);
            event.consume();
        }
    }

    private static Direction directionFor(KeyCode code) {
        return switch (code) {
            case W, UP -> Direction.UP;
            case S, DOWN -> Direction.DOWN;
            case A, LEFT -> Direction.LEFT;
            case D, RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    private void quickUse(Predicate<Potion> filter) {
        game.getInventory().stream()
                .map(ItemStack::item)
                .filter(item -> item instanceof Potion potion && filter.test(potion))
                .findFirst()
                .ifPresent(item -> game.useItem(item.getId()));
    }

    // ================= osservatore =================

    @Override
    public void onMessage(String message) {
        logArea.appendText(message + "\n");
    }

    @Override
    public void onStateChanged() {
        refresh();
    }

    @Override
    public void onShopRequested() {
        // showAndWait non è consentito durante un frame di animazione: rimando al ciclo successivo
        Platform.runLater(() -> {
            ShopDialog dialog = new ShopDialog(game);
            if (getScene() != null) {
                dialog.initOwner(getScene().getWindow());
            }
            mapView.clearHeld();
            dialog.showAndWait();
        });
    }

    // ================= aggiornamento =================

    private void refresh() {
        if (game.getPlayer() == null) {
            return;
        }
        updatePlayerPanel();
        updateCenterPanel();
        updateInventory();
        battleView.update(buildSceneModel());
    }

    private SceneModel buildSceneModel() {
        Player p = game.getPlayer();
        Optional<DungeonRun> run = game.getRun();
        run.ifPresent(r -> lastTheme = r.getDungeon().getId());
        boolean gameOver = game.getPhase() == GamePhase.GAME_OVER;
        String theme = run.isPresent() || gameOver ? lastTheme : "village";
        Monster monster = game.getCombat().map(Combat::getMonster).orElse(null);
        return new SceneModel(theme, p.getPlayerClass(), p.getName(), p.getHp(), p.getMaxHp(),
                monster == null ? null : monster.getId(),
                monster == null ? "" : monster.getName(),
                monster != null && monster.isBoss(),
                monster == null ? 0 : monster.getHp(),
                monster == null ? 0 : monster.getMaxHp(),
                gameOver);
    }

    private void updatePlayerPanel() {
        Player p = game.getPlayer();
        playerTitle.setText(p.getName() + " (" + p.getPlayerClass().getDisplayName() + ")");
        goldLabel.setText("Oro: " + p.getGold() + "   Livello: " + p.getLevel());
        statsLabel.setText("Attacco: " + p.getAttack() + "   Difesa: " + p.getDefense());
        equipLabel.setText("Arma: " + p.getWeapon().map(Item::getName).orElse("-")
                + "\nArmatura: " + p.getArmor().map(Item::getName).orElse("-"));
        hpBar.setProgress((double) p.getHp() / p.getMaxHp());
        hpText.setText(p.getHp() + " / " + p.getMaxHp());
        manaBar.setProgress((double) p.getMana() / p.getMaxMana());
        manaText.setText(p.getMana() + " / " + p.getMaxMana());
        xpBar.setProgress((double) p.getXp() / p.xpToNextLevel());
        xpText.setText(p.getXp() + " / " + p.xpToNextLevel());
    }

    private void updateCenterPanel() {
        GamePhase phase = game.getPhase();
        boolean walking = phase == GamePhase.HUB || phase == GamePhase.EXPLORING;
        setShown(mapView, walking);
        setShown(battleView, !walking);
        boolean inCombat = phase == GamePhase.COMBAT;
        setShown(monsterLabel, inCombat);

        switch (phase) {
            case HUB -> locationLabel.setText("Villaggio");
            case EXPLORING -> locationLabel.setText(game.getRun().map(r -> r.getDungeon().getName()).orElse(""));
            case COMBAT -> {
                locationLabel.setText("Combattimento!");
                Monster monster = game.getCombat().orElseThrow().getMonster();
                monsterLabel.setText(monster.getName() + ":  attacco " + monster.getAttack()
                        + ",  difesa " + monster.getDefense());
            }
            case GAME_OVER -> locationLabel.setText("Game Over");
            default -> locationLabel.setText("");
        }

        // i pulsanti si ricostruiscono solo se cambia qualcosa, non a ogni passo
        boolean canCast = phase == GamePhase.COMBAT
                && game.getPlayer().getMana() >= game.getPlayer().getPlayerClass().getAbility().getManaCost();
        String signature = phase + ":" + canCast;
        if (!signature.equals(actionSignature)) {
            actionSignature = signature;
            actionPane.getChildren().setAll(buildActions(phase, canCast));
        }
    }

    private List<Node> buildActions(GamePhase phase, boolean canCast) {
        List<Node> nodes = new ArrayList<>();
        switch (phase) {
            case HUB -> nodes.add(actionButton("Menu principale", this::exitToMenu));
            case EXPLORING -> nodes.add(actionButton("Abbandona il dungeon", game::abandonDungeon));
            case COMBAT -> {
                Ability ability = game.getPlayer().getPlayerClass().getAbility();
                nodes.add(actionButton("[1] Attacca", game::attack));
                Button special = actionButton("[2] " + ability.getName() + " (" + ability.getManaCost() + " MP)",
                        game::useAbility);
                special.setDisable(!canCast);
                nodes.add(special);
                nodes.add(actionButton("[3] Difendi", game::defend));
                nodes.add(actionButton("[4] Fuggi", game::flee));
            }
            case GAME_OVER -> nodes.add(actionButton("Torna al menu principale", this::exitToMenu));
            default -> { }
        }
        return nodes;
    }

    private void updateInventory() {
        List<ItemStack> current = game.getInventory();
        if (!current.equals(shownInventory)) {
            shownInventory = current;
            ItemStack selected = inventoryList.getSelectionModel().getSelectedItem();
            inventoryList.setItems(FXCollections.observableArrayList(current));
            if (selected != null) {
                current.stream()
                        .filter(stack -> stack.item().equals(selected.item()))
                        .findFirst()
                        .ifPresent(stack -> inventoryList.getSelectionModel().select(stack));
            }
        }
        updateInventoryButtons();
    }

    private void updateInventoryButtons() {
        ItemStack selected = inventoryList.getSelectionModel().getSelectedItem();
        GamePhase phase = game.getPhase();
        boolean canAct = selected != null && phase != GamePhase.GAME_OVER;
        boolean equippableInCombat = selected != null && selected.item() instanceof Equippable
                && phase == GamePhase.COMBAT;
        useButton.setDisable(!canAct || equippableInCombat);
        sellButton.setDisable(selected == null || phase != GamePhase.HUB);
    }

    // ================= utilità =================

    private void withSelectedItem(Consumer<String> action) {
        ItemStack selected = inventoryList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            action.accept(selected.item().getId());
        }
    }

    private void exitToMenu() {
        game.removeListener(this);
        mapView.stop();
        battleView.stop();
        if (getScene() != null) {
            detach(getScene());
        }
        onExit.run();
    }

    private static void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    private static Button actionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setOnAction(e -> action.run());
        return button;
    }

    private static ProgressBar bar(String color) {
        ProgressBar bar = new ProgressBar(1);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + color + ";");
        return bar;
    }
}
