package it.unicam.cs.mpgc.rpg129518.model.world;

import it.unicam.cs.mpgc.rpg129518.content.DungeonDefinition;
import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.model.entity.Monster;
import it.unicam.cs.mpgc.rpg129518.model.item.Item;

import java.util.*;

/**
 * Genera la mappa di un dungeon: stanze rettangolari collegate da corridoi, con mostri, tesori,
 * fontane curative e trappole. L'ultima stanza ospita il boss. Tutte le stanze sono raggiungibili.
 */
public class DungeonGenerator {

    private static final int WIDTH = 46;
    private static final int HEIGHT = 30;
    private static final int MAX_ATTEMPTS = 2000;

    private static final int CHEST_CHANCE = 40;
    private static final int FOUNTAIN_CHANCE = 25;
    private static final int TRAP_CHANCE = 30;

    private record Rect(int x, int y, int w, int h) {
        Position center() {
            return new Position(x + w / 2, y + h / 2);
        }

        boolean overlaps(Rect o, int margin) {
            return x - margin < o.x + o.w && x + w + margin > o.x
                    && y - margin < o.y + o.h && y + h + margin > o.y;
        }
    }

    private final GameContent content;
    private final Random random;

    public DungeonGenerator(GameContent content, Random random) {
        this.content = content;
        this.random = random;
    }

    public Dungeon generate(DungeonDefinition definition) {
        List<Rect> rooms = placeRooms(Math.max(2, definition.roomCount()));
        GameMap map = new GameMap(WIDTH, HEIGHT, rooms.get(0).center());
        rooms.forEach(room -> carveRoom(map, room));
        for (int i = 0; i < rooms.size() - 1; i++) {
            carveCorridor(map, rooms.get(i).center(), rooms.get(i + 1).center());
        }
        populate(map, definition, rooms);
        map.reveal(map.getStart(), 5);
        return new Dungeon(definition.id(), definition.name(), map, content.item(pickId(definition.lootIds())));
    }

    // ---------- geometria ----------

    private List<Rect> placeRooms(int count) {
        List<Rect> rooms = new ArrayList<>();
        for (int attempt = 0; attempt < MAX_ATTEMPTS && rooms.size() < count; attempt++) {
            int w = 5 + random.nextInt(5);
            int h = 5 + random.nextInt(3);
            Rect candidate = new Rect(1 + random.nextInt(WIDTH - w - 1), 1 + random.nextInt(HEIGHT - h - 1), w, h);
            if (rooms.stream().noneMatch(r -> r.overlaps(candidate, 2))) {
                rooms.add(candidate);
            }
        }
        if (rooms.size() < 2) {
            throw new IllegalStateException("Impossibile generare il dungeon");
        }
        return rooms;
    }

    private void carveRoom(GameMap map, Rect room) {
        for (int y = room.y(); y < room.y() + room.h(); y++) {
            for (int x = room.x(); x < room.x() + room.w(); x++) {
                map.setTile(x, y, TileType.FLOOR);
            }
        }
    }

    private void carveCorridor(GameMap map, Position a, Position b) {
        if (random.nextBoolean()) {
            carveHorizontal(map, a.x(), b.x(), a.y());
            carveVertical(map, a.y(), b.y(), b.x());
        } else {
            carveVertical(map, a.y(), b.y(), a.x());
            carveHorizontal(map, a.x(), b.x(), b.y());
        }
    }

    private void carveHorizontal(GameMap map, int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            map.setTile(x, y, TileType.FLOOR);
        }
    }

    private void carveVertical(GameMap map, int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            map.setTile(x, y, TileType.FLOOR);
        }
    }

    // ---------- contenuto ----------

    private void populate(GameMap map, DungeonDefinition def, List<Rect> rooms) {
        Set<Position> used = new HashSet<>();
        used.add(map.getStart());

        Rect bossRoom = rooms.getLast();
        Monster boss = content.createMonster(def.bossId());
        Position bossPosition = bossRoom.center();
        used.add(bossPosition);
        map.addObject(MapObject.encounter(bossPosition, ObjectType.BOSS, new BossRoom(boss), boss.getId()));

        for (int i = 0; i < rooms.size() - 1; i++) {
            Rect room = rooms.get(i);
            if (i > 0) {
                int enemies = 1 + random.nextInt(2);
                for (int e = 0; e < enemies; e++) {
                    placeInRoom(room, used).ifPresent(p -> addEnemy(map, p, def));
                }
            }
            if (random.nextInt(100) < CHEST_CHANCE) {
                placeInRoom(room, used).ifPresent(p -> addChest(map, p, def));
            }
            if (random.nextInt(100) < FOUNTAIN_CHANCE) {
                placeInRoom(room, used).ifPresent(p ->
                        map.addObject(MapObject.encounter(p, ObjectType.FOUNTAIN, new RestRoom(), null)));
            }
            if (i > 0 && random.nextInt(100) < TRAP_CHANCE) {
                placeInRoom(room, used).ifPresent(p ->
                        map.addObject(MapObject.encounter(p, ObjectType.TRAP, new TrapRoom(def.trapDamage()), null)));
            }
        }
    }

    private void addEnemy(GameMap map, Position p, DungeonDefinition def) {
        Monster monster = content.createMonster(pickId(def.monsterIds()));
        map.addObject(MapObject.encounter(p, ObjectType.ENEMY, new EnemyRoom(monster), monster.getId()));
    }

    private void addChest(GameMap map, Position p, DungeonDefinition def) {
        Item loot = content.item(pickId(def.lootIds()));
        int gold = 5 + def.minLevel() * 3 + random.nextInt(10);
        map.addObject(MapObject.encounter(p, ObjectType.CHEST, new TreasureRoom(loot, gold), null));
    }

    /** Sceglie una casella libera all'interno di una stanza (mai sul bordo, per non ostruire i passaggi). */
    private Optional<Position> placeInRoom(Rect room, Set<Position> used) {
        for (int attempt = 0; attempt < 30; attempt++) {
            Position p = new Position(room.x() + 1 + random.nextInt(room.w() - 2),
                    room.y() + 1 + random.nextInt(room.h() - 2));
            if (used.add(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    private String pickId(List<String> ids) {
        return ids.get(random.nextInt(ids.size()));
    }
}
