package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

import java.util.Random;

public class TrapRoom extends Room {

    private final int damage;

    public TrapRoom(int damage) {
        this.damage = damage;
    }

    @Override
    public String getName() {
        return "Corridoio trappolato";
    }

    @Override
    public RoomOutcome enter(Player player, Random random) {
        int dealt = player.takeDamage(damage);
        if (!player.isAlive()) {
            return RoomOutcome.peaceful("Una trappola scatta e ti infligge " + dealt + " danni. Ti è fatale...");
        }
        return RoomOutcome.peaceful("Una trappola scatta! Subisci " + dealt + " danni.");
    }
}
