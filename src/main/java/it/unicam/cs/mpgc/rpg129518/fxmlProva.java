package it.unicam.cs.mpgc.rpg129518;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class fxmlProva extends Application
{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlProva.class.getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);

        stage.setTitle("Dungeon Crawler RPG");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
