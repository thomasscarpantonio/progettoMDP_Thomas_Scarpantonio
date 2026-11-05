package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;

/** Fotografia di ciò che la scena di combattimento deve mostrare. La vista la costruisce a partire dallo stato di {@link it.unicam.cs.mpgc.rpg129518.engine.Game}. */
public record SceneModel(String theme, PlayerClass playerClass, String playerName, int playerHp, int playerMaxHp,
                         String monsterId, String monsterName, boolean boss, int monsterHp, int monsterMaxHp,
                         boolean gameOver) {

    public boolean hasMonster() {
        return monsterId != null;
    }
}
