package it.unicam.cs.mpgc.rpg129518;

import it.unicam.cs.mpgc.rpg129518.content.DungeonDefinition;
import it.unicam.cs.mpgc.rpg129518.content.GameContent;
import it.unicam.cs.mpgc.rpg129518.content.JsonContentProvider;
import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.engine.GamePhase;
import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import it.unicam.cs.mpgc.rpg129518.model.world.*;
import it.unicam.cs.mpgc.rpg129518.persistence.JsonSaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowTest {

    private final GameContent content = new JsonContentProvider().load();

    @Test
    void everyGeneratedDungeonHasABossAndEverythingIsReachable() {
        DungeonGenerator generator = new DungeonGenerator(content, new Random(7));
        for (DungeonDefinition definition : content.getDungeons()) {
            Dungeon dungeon = generator.generate(definition);
            GameMap map = dungeon.getMap();
            long bosses = map.getObjects().stream().filter(o -> o.getType() == ObjectType.BOSS).count();
            assertEquals(1, bosses);
            for (MapObject object : map.getObjects()) {
                assertTrue(firstStep(map, map.getStart(), object.getPosition()).isPresent()
                        || object.getPosition().equals(map.getStart()));
            }
        }
    }

    @Test
    void villageHasOneGatePerDungeon() {
        GameMap village = content.createVillage();
        long gates = village.getObjects().stream().filter(o -> o.getType() == ObjectType.GATE).count();
        assertEquals(content.getDungeons().size(), gates);
    }

    @Test
    void walkingIntoAWallDoesNotMoveThePlayer(@TempDir Path dir) {
        Game game = newGame(dir);
        game.newGame("Test", PlayerClass.WARRIOR);
        Position start = game.getPosition();
        for (int i = 0; i < 40; i++) {
            game.move(Direction.LEFT);
        }
        assertTrue(game.getPosition().x() < start.x());
        assertTrue(game.getMap().orElseThrow().isWalkable(game.getPosition()));
    }

    @Test
    void onlyTheFirstDungeonIsInitiallyUnlocked(@TempDir Path dir) {
        Game game = newGame(dir);
        game.newGame("Test", PlayerClass.WARRIOR);
        assertTrue(game.isUnlocked(game.getDungeons().get(0)));
        assertFalse(game.isUnlocked(game.getDungeons().get(1)));
    }

    @Test
    void walkingIntoTheFirstGateEntersTheDungeon(@TempDir Path dir) {
        Game game = newGame(dir);
        game.newGame("Test", PlayerClass.WARRIOR);
        walkIntoGate(game, game.getDungeons().get(0).id());
        assertEquals(GamePhase.EXPLORING, game.getPhase());
        assertNotEquals("village", game.getTheme());
    }

    @Test
    void walkingToTheBossAlwaysTerminatesInAValidPhase(@TempDir Path dir) {
        Game game = newGame(dir);
        game.newGame("Test", PlayerClass.WARRIOR);
        walkIntoGate(game, game.getDungeons().get(0).id());
        GameMap map = game.getMap().orElseThrow();
        Position boss = map.getObjects().stream()
                .filter(o -> o.getType() == ObjectType.BOSS).findFirst().orElseThrow().getPosition();

        for (int i = 0; i < 5000 && (game.getPhase() == GamePhase.EXPLORING || game.getPhase() == GamePhase.COMBAT); i++) {
            if (game.getPhase() == GamePhase.COMBAT) {
                game.attack();
            } else {
                game.move(firstStep(map, game.getPosition(), boss).orElseThrow());
            }
        }
        assertTrue(game.getPhase() == GamePhase.HUB || game.getPhase() == GamePhase.GAME_OVER);
    }

    // ---------- utilità di test ----------

    private Game newGame(Path dir) {
        return new Game(content, new JsonSaveRepository(dir), new Random(3));
    }

    private void walkIntoGate(Game game, String dungeonId) {
        GameMap village = game.getMap().orElseThrow();
        Position gate = village.getObjects().stream()
                .filter(o -> o.getType() == ObjectType.GATE && o.getRefId().equals(dungeonId))
                .findFirst().orElseThrow().getPosition();
        Position below = gate.translate(Direction.DOWN);
        for (int i = 0; i < 200 && game.getPhase() == GamePhase.HUB; i++) {
            if (game.getPosition().equals(below)) {
                game.move(Direction.UP);
            } else {
                game.move(firstStep(village, game.getPosition(), below).orElseThrow());
            }
        }
    }

    /** Ricerca in ampiezza: restituisce la prima mossa del percorso più breve verso target. */
    private static Optional<Direction> firstStep(GameMap map, Position from, Position target) {
        Map<Position, Direction> first = new HashMap<>();
        Set<Position> seen = new HashSet<>();
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(from);
        seen.add(from);
        while (!queue.isEmpty()) {
            Position p = queue.poll();
            for (Direction d : Direction.values()) {
                Position n = p.translate(d);
                if (!seen.add(n) || !map.isWalkable(n)) {
                    continue;
                }
                first.put(n, p.equals(from) ? d : first.get(p));
                if (n.equals(target)) {
                    return Optional.of(first.get(n));
                }
                queue.add(n);
            }
        }
        return Optional.empty();
    }
}
