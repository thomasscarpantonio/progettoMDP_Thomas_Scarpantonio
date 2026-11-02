package it.unicam.cs.mpgc.rpg129518.content;

import it.unicam.cs.mpgc.rpg129518.model.item.Armor;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import it.unicam.cs.mpgc.rpg129518.model.item.Potion;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;

/** Descrizione di un oggetto così come appare in items.json; sa costruire l'oggetto di gioco. */
public record ItemDefinition(String id, ItemType type, String name, String description, int value,
                             boolean buyable, int attackBonus, int defenseBonus, int healAmount, int manaAmount) {

    public Item toItem() {
        return switch (type) {
            case WEAPON -> new Weapon(id, name, description, value, attackBonus);
            case ARMOR -> new Armor(id, name, description, value, defenseBonus);
            case POTION -> new Potion(id, name, description, value, healAmount, manaAmount);
        };
    }
}
