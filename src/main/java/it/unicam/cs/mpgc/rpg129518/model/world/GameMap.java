package it.unicam.cs.mpgc.rpg129518.model.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Griglia di caselle con gli oggetti che contiene e la "nebbia di guerra" (caselle già esplorate). */
public class GameMap {

    private final int width;
    private final int height;
    private final TileType[][] tiles;
    private final boolean[][] explored;
    private final Map<Position, MapObject> objects = new HashMap<>();
    private final Position start;

    public GameMap(int width, int height, Position start) {
        this.width = width;
        this.height = height;
        this.start = start;
        this.tiles = new TileType[height][width];
        this.explored = new boolean[height][width];
        for (TileType[] row : tiles) {
            java.util.Arrays.fill(row, TileType.WALL);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Position getStart() {
        return start;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public TileType tileAt(int x, int y) {
        return inBounds(x, y) ? tiles[y][x] : TileType.WALL;
    }

    public void setTile(int x, int y, TileType type) {
        if (inBounds(x, y)) {
            tiles[y][x] = type;
        }
    }

    public boolean isWalkable(Position p) {
        return tileAt(p.x(), p.y()) == TileType.FLOOR;
    }

    public void addObject(MapObject object) {
        objects.put(object.getPosition(), object);
    }

    public Optional<MapObject> objectAt(Position p) {
        return Optional.ofNullable(objects.get(p));
    }

    public List<MapObject> getObjects() {
        return List.copyOf(objects.values());
    }

    /** Segna come esplorate le caselle entro il raggio indicato. */
    public void reveal(Position center, int radius) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int x = center.x() + dx;
                int y = center.y() + dy;
                if (dx * dx + dy * dy <= radius * radius && inBounds(x, y)) {
                    explored[y][x] = true;
                }
            }
        }
    }

    public void revealAll() {
        for (boolean[] row : explored) {
            java.util.Arrays.fill(row, true);
        }
    }

    public boolean isExplored(int x, int y) {
        return inBounds(x, y) && explored[y][x];
    }
}
