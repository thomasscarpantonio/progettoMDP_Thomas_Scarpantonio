package it.unicam.cs.mpgc.rpg129518.gui;

import javafx.application.Application;

/** Punto di ingresso separato dalla classe Application (evita problemi di avvio con JavaFX). */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(DungeonApp.class, args);
    }
}
