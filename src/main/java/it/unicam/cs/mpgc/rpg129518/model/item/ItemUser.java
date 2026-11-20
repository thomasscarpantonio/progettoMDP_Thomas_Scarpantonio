package it.unicam.cs.mpgc.rpg129518.model.item;

public interface ItemUser {
    String getName();
    int heal(int amount);
    int restoreMana(int amount);
    void equip(Weapon weapon);
    void equip(Armor armor);
}