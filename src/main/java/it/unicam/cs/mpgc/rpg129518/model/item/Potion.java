package it.unicam.cs.mpgc.rpg129518.model.item;

import java.util.ArrayList;

import java.util.List;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

/** Pozione che ripristina punti ferita e/o mana. */
public class Potion extends Item implements Consumable
{
    private final int healAmount;
    private final int manaAmount;

    public Potion(String id, String name, String description, int value, int healAmount, int manaAmount) {
        super(id, name, description, value);
        this.healAmount = healAmount;
        this.manaAmount = manaAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public int getManaAmount() {
        return manaAmount;
    }

    @Override
    public String getDetails() {
        List<String> effects = new ArrayList<>();
        if (healAmount > 0) {
            effects.add("+" + healAmount + " PV");
        }
        if (manaAmount > 0) {
            effects.add("+" + manaAmount + " MP");
        }
        return String.join(", ", effects);
    }

    @Override
    public String consume(Player player) {
        int healed = player.heal(healAmount);
        int restored = player.restoreMana(manaAmount);
        return player.getName() + " usa " + getName() + ": recupera " + healed + " PV e " + restored + " MP.";
    }
}
