package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

import java.util.Random;

public class BossRoom extends EnemyRoom {

    public BossRoom(Monster boss) {
        super(boss);
    }

    @Override
    public String getName() {
        return "Sala del boss";
    }

    @Override
    public RoomOutcome enter(Player player, Random random) {
        return RoomOutcome.encounter(getMonster(),
                "Una presenza opprimente riempie la sala... " + getMonster().getName() + " ti attende!");
    }
}
