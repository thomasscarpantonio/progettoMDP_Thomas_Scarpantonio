package it.unicam.cs.mpgc.rpg129518.model.combat;

import java.util.Random;

/** Unica responsabilità: calcolare i danni. Il Random è iniettato, quindi i test sono deterministici. */
public class DamageCalculator {

    private static final double CRITICAL_CHANCE = 0.10;
    private static final int VARIANCE = 3;

    private final Random random;

    public DamageCalculator(Random random) {
        this.random = random;
    }

    /** Colpo fisico: la difesa riduce il danno (minimo 1), con piccola variabilità e possibile critico. */
    public Hit rollHit(int attack, int defense) {
        int base = Math.max(1, attack - defense) + random.nextInt(VARIANCE);
        boolean critical = random.nextDouble() < CRITICAL_CHANCE;
        return new Hit(critical ? base * 2 : base, critical);
    }

    /** Danno magico: ignora la difesa. */
    public int rollMagic(int power) {
        return Math.max(1, power) + random.nextInt(VARIANCE);
    }
}
