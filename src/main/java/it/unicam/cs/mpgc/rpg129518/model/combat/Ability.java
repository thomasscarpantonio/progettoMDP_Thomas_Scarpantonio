package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

/** Abilità speciale di una classe (Strategy): nuove abilità = nuove implementazioni. */
public interface Ability {

    String getName();

    int getManaCost();

    int computeDamage(Player user, Monster target, DamageCalculator calculator);
}
