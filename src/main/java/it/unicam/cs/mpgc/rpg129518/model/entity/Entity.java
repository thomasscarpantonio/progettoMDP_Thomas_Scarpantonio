package it.unicam.cs.mpgc.rpg129518.model.entity;

import java.util.Objects;

/**
 * Qualsiasi creatura combattente del gioco (giocatore o mostro).
 * Le statistiche di base sono delegate alle sottoclassi (metodi astratti),
 * mentre la gestione dei punti ferita è comune a tutti.
 */
public abstract class Entity {

    private final String name;
    /** Punti ferita correnti: le sottoclassi lo inizializzano dopo aver impostato le proprie statistiche. */
    protected int hp;

    protected Entity(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public abstract int getMaxHp();

    public abstract int getBaseAttack();

    public abstract int getBaseDefense();

    /** Attacco effettivo (le sottoclassi possono aggiungere bonus, es. equipaggiamento). */
    public int getAttack() {
        return getBaseAttack();
    }

    /** Difesa effettiva. */
    public int getDefense() {
        return getBaseDefense();
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    /** @return i danni realmente subiti (mai oltre gli HP rimasti). */
    public int takeDamage(int amount) {
        int actual = Math.min(Math.max(0, amount), hp);
        hp -= actual;
        return actual;
    }

    /** @return i punti ferita realmente recuperati. */
    public int heal(int amount) {
        int before = hp;
        hp = Math.min(getMaxHp(), hp + Math.max(0, amount));
        return hp - before;
    }
}
