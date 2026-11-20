package it.unicam.cs.mpgc.rpg129518.model.entity;

/** Classi giocabili: definiscono statistiche iniziali, arma di partenza e abilità speciale. */
public enum PlayerClass {
    WARRIOR("Guerriero", 40, 10, 6, 4, "rusty_sword"),
    MAGE("Mago", 28, 25, 4, 2, "apprentice_staff"),
    ROGUE("Ladro", 32, 15, 5, 3, "dagger");

    private final String displayName;
    private final int baseHp;
    private final int baseMana;
    private final int baseAttack;
    private final int baseDefense;
    private final String startingWeaponId;

    PlayerClass(String displayName, int baseHp, int baseMana, int baseAttack, int baseDefense,
                String startingWeaponId) {
        this.displayName = displayName;
        this.baseHp = baseHp;
        this.baseMana = baseMana;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.startingWeaponId = startingWeaponId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public String getStartingWeaponId() {
        return startingWeaponId;
    }
}