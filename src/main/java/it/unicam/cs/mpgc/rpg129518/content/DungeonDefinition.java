package it.unicam.cs.mpgc.rpg129518.content;

import java.util.List;

/** Descrizione di un dungeon letta da dungeons.json. */
public record DungeonDefinition(String id, String name, String description, int minLevel, int roomCount,
                                List<String> monsterIds, String bossId, List<String> lootIds, int trapDamage) {
}
