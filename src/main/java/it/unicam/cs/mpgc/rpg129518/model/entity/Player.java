package it.unicam.cs.mpgc.rpg129518.model.entity;
import it.unicam.cs.mpgc.rpg129518.model.item.Armor;
import it.unicam.cs.mpgc.rpg129518.model.item.Inventory;
import it.unicam.cs.mpgc.rpg129518.model.item.Weapon;

import java.util.Optional;

/**
 * L'eroe controllato dall'utente. Le statistiche dipendono da classe e livello,
 * quindi per ripristinare un salvataggio bastano pochi valori ({@link #/*restoreState}).
 */
public final class Player extends Entity {

    private static final int HP_PER_LEVEL = 6;
    private static final int MANA_PER_LEVEL = 3;
    private static final int XP_PER_LEVEL = 20;

    //private final PlayerClass playerClass;
    private final Inventory inventory = new Inventory();
    private int level = 1;
    private int xp;
    private int mana;
    private int gold;
    private Weapon weapon;
    private Armor armor;

    public Player(String name) {
        super(name);
        this.hp = getMaxHp();
    }
    public int getMaxHp() {
       return 0;
    }

    public int getMaxMana() {
      return 0;
    }

    @Override
    public int getBaseAttack() {
       return 0;
    }

    @Override
    public int getBaseDefense() {
       return 0;
    }

    @Override
    public int getAttack() {
        return getBaseAttack() + (weapon == null ? 0 : weapon.getAttackBonus());
    }

    @Override
    public int getDefense() {
        return getBaseDefense() + (armor == null ? 0 : armor.getDefenseBonus());
    }

    // ---- statistiche derivate ----
}
