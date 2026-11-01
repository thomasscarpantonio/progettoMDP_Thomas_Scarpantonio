package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.content.DungeonDefinition;

import java.util.List;

/**
 * Costruisce la mappa del villaggio da un file di testo (una riga = una riga di caselle).
 * Legenda: T albero, H edificio, . prato, @ partenza, I locanda, S negozio, W cristallo di salvataggio,
 * 1-9 cancello del dungeon corrispondente (nell'ordine di dungeons.json).
 */
public final class MapParser {

    private MapParser() {
    }

    public static GameMap parseVillage(List<String> rows, List<DungeonDefinition> dungeons) {
        int height = rows.size();
        int width = rows.stream().mapToInt(String::length).max().orElse(0);
        Position start = findStart(rows, width, height);
        GameMap map = new GameMap(width, height, start);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = x < rows.get(y).length() ? rows.get(y).charAt(x) : 'T';
                Position here = new Position(x, y);
                switch (c) {
                    case 'T' -> map.setTile(x, y, TileType.TREE);
                    case 'H' -> map.setTile(x, y, TileType.BUILDING);
                    default -> {
                        map.setTile(x, y, TileType.FLOOR);
                        placeLandmark(map, here, c, dungeons);
                    }
                }
            }
        }
        map.revealAll();
        return map;
    }

    private static Position findStart(List<String> rows, int width, int height) {
        for (int y = 0; y < height; y++) {
            int x = rows.get(y).indexOf('@');
            if (x >= 0) {
                return new Position(x, y);
            }
        }
        return new Position(width / 2, Math.max(0, height - 3));
    }

    private static void placeLandmark(GameMap map, Position p, char c, List<DungeonDefinition> dungeons) {
        switch (c) {
            case 'I' -> map.addObject(MapObject.landmark(p, ObjectType.INN, "inn", "Locanda"));
            case 'S' -> map.addObject(MapObject.landmark(p, ObjectType.SHOP, "shop", "Negozio"));
            case 'W' -> map.addObject(MapObject.landmark(p, ObjectType.SAVE_POINT, "save", "Salva"));
            default -> {
                int index = c - '1';
                if (Character.isDigit(c) && index >= 0 && index < dungeons.size()) {
                    DungeonDefinition def = dungeons.get(index);
                    map.addObject(MapObject.landmark(p, ObjectType.GATE, def.id(), def.name()));
                }
            }
        }
    }
}
