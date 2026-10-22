package it.unicam.cs.mpgc.rpg129518;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class prova  extends Application {
        @Override
        public void start(Stage primaryStage) {
            Label label = new Label("Il Dungeon Crawler sta per iniziare!");
            StackPane root = new StackPane(label);
            Scene scene = new Scene(root, 400, 300);

            primaryStage.setTitle("RPG Universitario - Test");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
}
