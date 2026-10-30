package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;

import java.util.Random;

public class TreasureRoom extends Room {

    private final Item loot;
    private final int gold;

    public TreasureRoom(Item loot, int gold) {
        this.loot = loot;
        this.gold = gold;
    }

    @Override
    public String getName() {
        return "Stanza del tesoro";
    }

    @Override
    public RoomOutcome enter(Player player, Random random) {
        player.getInventory().add(loot);
        player.addGold(gold);
        return RoomOutcome.peaceful("Trovi uno scrigno! Dentro ci sono " + gold + " monete e " + loot.getName() + ".");
    }
}
