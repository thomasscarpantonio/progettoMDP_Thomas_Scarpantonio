package it.unicam.cs.mpgc.rpg129518.model.combat;

import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;

import java.util.EnumMap;
import java.util.Map;

public final class AbilityCatalog {

    private static final Map<PlayerClass, Ability> ABILITIES = new EnumMap<>(PlayerClass.class);

    static {
        ABILITIES.put(PlayerClass.WARRIOR, new PowerStrike());
        ABILITIES.put(PlayerClass.MAGE, new Fireball());
        ABILITIES.put(PlayerClass.ROGUE, new DoubleStrike());
    }

    private AbilityCatalog() {
    }

    public static Ability forClass(PlayerClass playerClass) {
        return ABILITIES.get(playerClass);
    }
}
