package it.unicam.cs.mpgc.rpg129518.content;

import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Catalogo dei contenuti di gioco (oggetti, mostri, dungeon), indipendente dalla fonte dei dati. */
public class GameContent {

    private final Map<String, Item> items;
    private final List<String> shopItemIds;
    private final Map<String, MonsterTemplate> monsters;
    private final List<DungeonDefinition> dungeons;
    private final List<String> villageRows;

    public GameContent(List<ItemDefinition> itemDefinitions, List<MonsterTemplate> monsterTemplates,
                       List<DungeonDefinition> dungeons, List<String> villageRows) {
        this.items = itemDefinitions.stream().collect(Collectors.toMap(
                ItemDefinition::id, ItemDefinition::toItem, (a, b) -> a, LinkedHashMap::new));
        this.shopItemIds = itemDefinitions.stream()
                .filter(ItemDefinition::buyable)
                .map(ItemDefinition::id)
                .toList();
        this.monsters = monsterTemplates.stream().collect(Collectors.toMap(
                MonsterTemplate::id, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        this.dungeons = List.copyOf(dungeons);
        this.villageRows = List.copyOf(villageRows);
    }

    /** Crea una nuova mappa del villaggio (ogni volta una copia fresca). */
    public List<String> getVillageRows() {
        return villageRows;
    }

    public Item item(String id) {
        Item item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Oggetto sconosciuto: " + id);
        }
        return item;
    }

    public Monster createMonster(String id) {
        MonsterTemplate template = monsters.get(id);
        if (template == null) {
            throw new IllegalArgumentException("Mostro sconosciuto: " + id);
        }
        return template.create();
    }

    public List<Item> getShopItems() {
        return shopItemIds.stream().map(items::get).toList();
    }

    public List<DungeonDefinition> getDungeons() {
        return dungeons;
    }
}
