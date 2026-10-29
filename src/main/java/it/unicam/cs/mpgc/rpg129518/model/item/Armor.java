package it.unicam.cs.mpgc.rpg129518.model.item;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

public class Armor extends Item implements Equippable {

    private final int defenseBonus;

    public Armor(String id, String name, String description, int value, int defenseBonus) {
        super(id, name, description, value);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    @Override
    public String getDetails() {
        return "+" + defenseBonus + " difesa";
    }

    @Override
    public String equipOn(Player player) {
        player.equip(this);
        return player.getName() + " indossa " + getName() + ".";
    }
}
