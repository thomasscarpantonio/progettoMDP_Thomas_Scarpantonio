package it.unicam.cs.mpgc.rpg129518;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
public class MainController
{
    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartButtonClicked() {
        welcomeText.setText("Sei entrato nel primo Dungeon! Preparati a combattere.");
        }
}
