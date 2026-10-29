package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;

/** Guerriero: un colpo con attacco raddoppiato. */
public class PowerStrike implements Ability {

    @Override
    public String getName() {
        return "Colpo Possente";
    }

    @Override
    public int getManaCost() {
        return 4;
    }

    @Override
    public int computeDamage(Player user, Monster target, DamageCalculator calculator) {
        return calculator.rollHit(user.getAttack() * 2, target.getDefense()).amount();
    }
}
