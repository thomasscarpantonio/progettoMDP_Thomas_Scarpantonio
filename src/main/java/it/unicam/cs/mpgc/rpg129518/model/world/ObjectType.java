package it.unicam.cs.mpgc.rpg129518.model.world;

/** Cosa può trovarsi su una casella della mappa. */
public enum ObjectType {
    // nei dungeon
    ENEMY,
    BOSS,
    CHEST,
    FOUNTAIN,
    TRAP,
    // nel villaggio
    INN,
    SHOP,
    SAVE_POINT,
    GATE
}
