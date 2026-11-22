package it.unicam.cs.mpgc.rpg129518.gui;

import it.unicam.cs.mpgc.rpg129518.content.ContentProvider;
import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.content.JsonContentProvider;
import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.generation.DungeonGenerator;
import it.unicam.cs.mpgc.rpg129518.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg129518.persistence.SaveRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Random;

/**
 * Composition root: è l'unico punto in cui si scelgono le implementazioni concrete
 * (contenuti JSON, salvataggio JSON).
 */
public class DungeonApp extends Application {

    private Game game;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        ContentProvider contentProvider = new JsonContentProvider();
        Path saveDirectory = Path.of("saves");
        SaveRepository repository = new JsonSaveRepository(saveDirectory);

        GameContent content = contentProvider.load();
        Random random = new Random();
        game = new Game(content, repository, new DungeonGenerator(content, random), random);

        scene = new Scene(new StackPane(), 1050, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        showMenu();

        stage.setTitle("Dungeon Busters");
        stage.setScene(scene);
        stage.setMinWidth(1050);
        stage.setMinHeight(720);
        stage.show();
    }

    private void showMenu() {
        scene.setRoot(new MainMenuView(game, this::showGame));
    }

    private void showGame() {
        scene.setRoot(new GameView(game, this::showMenu));
    }
}
