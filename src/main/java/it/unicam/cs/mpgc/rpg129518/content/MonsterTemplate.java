package it.unicam.cs.mpgc.rpg129518.content;

import it.unicam.cs.mpgc.rpg129518.model.entity.Boss;
import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;

/** Modello di mostro letto da monsters.json (Factory Method: ogni chiamata crea una nuova istanza). */
public record MonsterTemplate(String id, String name, int hp, int attack, int defense, int xp, int gold,
                              boolean boss) {

    public Monster create() {
        return boss
                ? new Boss(id, name, hp, attack, defense, xp, gold)
                : new Monster(id, name, hp, attack, defense, xp, gold);
    }
}
