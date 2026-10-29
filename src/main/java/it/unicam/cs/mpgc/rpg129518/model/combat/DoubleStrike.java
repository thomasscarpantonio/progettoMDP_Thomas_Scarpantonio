package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

/** Ladro: due colpi consecutivi. */
public class DoubleStrike implements Ability {

    @Override
    public String getName() {
        return "Doppio Fendente";
    }

    @Override
    public int getManaCost() {
        return 3;
    }

    @Override
    public int computeDamage(Player user, Monster target, DamageCalculator calculator) {
        int first = calculator.rollHit(user.getAttack(), target.getDefense()).amount();
        int second = calculator.rollHit(user.getAttack(), target.getDefense()).amount();
        return first + second;
    }
}
