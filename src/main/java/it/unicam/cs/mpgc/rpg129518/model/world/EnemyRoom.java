package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

import java.util.Random;

public class EnemyRoom extends Room {

    private final Monster monster;

    public EnemyRoom(Monster monster) {
        this.monster = monster;
    }

    public Monster getMonster() {
        return monster;
    }

    @Override
    public String getName() {
        return "Sala del nemico";
    }

    @Override
    public RoomOutcome enter(Player player, Random random) {
        return RoomOutcome.encounter(monster, "Un " + monster.getName() + " ti sbarra la strada!");
    }
}
