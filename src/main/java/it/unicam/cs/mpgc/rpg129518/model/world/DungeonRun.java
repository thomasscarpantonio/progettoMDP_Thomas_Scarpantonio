package it.unicam.cs.mpgc.rpg129518.model.world;

/** Esplorazione in corso di un dungeon. */
public class DungeonRun {

    private final Dungeon dungeon;

    public DungeonRun(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public GameMap getMap() {
        return dungeon.getMap();
    }

    public long enemiesRemaining() {
        return getMap().getObjects().stream()
                .filter(o -> o.getType() == ObjectType.ENEMY && o.isActive())
                .count();
    }
}
