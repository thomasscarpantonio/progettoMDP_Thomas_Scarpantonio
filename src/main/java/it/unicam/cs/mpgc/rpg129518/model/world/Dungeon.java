package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.model.item.Item;
/** Un dungeon generato: mappa esplorabile e ricompensa per chi sconfigge il boss. */
public class Dungeon {

    private final String id;
    private final String name;
    private final GameMap map;
    private final Item completionReward;

    public Dungeon(String id, String name, GameMap map, Item completionReward) {
        this.id = id;
        this.name = name;
        this.map = map;
        this.completionReward = completionReward;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GameMap getMap() {
        return map;
    }

    public Item getCompletionReward() {
        return completionReward;
    }
}
