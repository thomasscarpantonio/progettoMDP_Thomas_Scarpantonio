package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

/** Mago: danno magico che ignora la difesa del bersaglio. */
public class Fireball implements Ability {

    @Override
    public String getName() {
        return "Palla di Fuoco";
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public int computeDamage(Player user, Monster target, DamageCalculator calculator) {
        return calculator.rollMagic(user.getAttack() * 3);
    }
}
