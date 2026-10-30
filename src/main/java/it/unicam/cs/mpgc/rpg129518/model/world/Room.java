package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

import java.util.Random;

/** Stanza di un dungeon. Ogni tipo di stanza decide da sé cosa accade (polimorfismo). */
public abstract class Room {

    public abstract String getName();

    public abstract RoomOutcome enter(Player player, Random random);
}
