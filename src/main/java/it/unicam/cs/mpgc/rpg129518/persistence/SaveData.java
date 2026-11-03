package it.unicam.cs.mpgc.rpg129518.persistence;

import java.util.List;
import java.util.Map;

/** Istantanea serializzabile della partita (DTO): contiene solo dati semplici, mai oggetti di gioco. */
public record SaveData(String playerName, String playerClass, int level, int xp, int hp, int mana, int gold,
                       Map<String, Integer> inventory, String weaponId, String armorId,
                       List<String> completedDungeons) {
}
