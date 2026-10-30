package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

import java.util.Random;

/** Un angolo tranquillo dove recuperare un po' di salute e mana. */
public class RestRoom extends Room {

    private static final int HEAL_PERCENT = 30;
    private static final int MANA_AMOUNT = 5;

    @Override
    public String getName() {
        return "Fontana curativa";
    }

    @Override
    public RoomOutcome enter(Player player, Random random) {
        int healed = player.heal(Math.max(5, player.getMaxHp() * HEAL_PERCENT / 100));
        int mana = player.restoreMana(MANA_AMOUNT);
        return RoomOutcome.peaceful("Una fonte magica ti ristora: +" + healed + " PV, +" + mana + " MP.");
    }
}