package it.unicam.cs.mpgc.rpg129518.generation;

import it.unicam.cs.mpgc.rpg129518.content.DungeonDefinition;
import it.unicam.cs.mpgc.rpg129518.model.world.Dungeon;

public interface DungeonFactory {
    Dungeon generate(DungeonDefinition definition);
}
