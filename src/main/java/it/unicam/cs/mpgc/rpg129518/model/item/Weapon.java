package it.unicam.cs.mpgc.rpg129518.model.item;

import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

public class Weapon extends Item implements Equippable {

    private final int attackBonus;

    public Weapon(String id, String name, String description, int value, int attackBonus) {
        super(id, name, description, value);
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public String getDetails() {
        return "+" + attackBonus + " attacco";
    }

    @Override
    public String equipOn(Player player) {
        //player.equip(this);
        return player.getName() + " impugna " + getName() + ".";
    }
}