package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import it.unicam.cs.mpgc.rpg129518.engine.Game;
import it.unicam.cs.mpgc.rpg129518.engine.GamePhase;
import it.unicam.cs.mpgc.rpg129518.model.entity.Player;
import it.unicam.cs.mpgc.rpg129518.model.world.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Vista dall'alto della mappa (villaggio o dungeon): camera che segue l'eroe, nebbia di guerra,
 * minimappa e HUD. Legge lo stato da {@link Game} a ogni frame; i tasti premuti arrivano da {@link #press}.
 */
public class MapView extends Pane {

    private static final double S = TileRenderer.S;
    private static final double MOVE_INTERVAL = 0.13;
    private static final double MINIMAP_TILE = 4;

    private record Drawable(double foot, Runnable drawing) {
    }

    private final Game game;
    private final Canvas canvas = new Canvas();
    private final AnimationTimer timer;
    private final Deque<Direction> held = new ArrayDeque<>();

    private GameMap lastMap;
    private double time;
    private double cooldown;
    private double renderX;
    private double renderY;
    private double flash;
    private int lastHp = -1;

    public MapView(Game game) {
        this.game = game;
        setStyle("-fx-background-color: #050508;");
        setMinSize(320, 240);
        getChildren().add(canvas);
        timer = new AnimationTimer() {
            private long last;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                }
                double dt = Math.min(0.1, (now - last) / 1_000_000_000.0);
                last = now;
                time += dt;
                step(dt);
                if (isVisible()) {
                    render();
                }
            }
        };
        timer.start();
    }

    // ================= input =================

    public void press(Direction direction) {
        held.remove(direction);
        held.addFirst(direction);
    }

    public void release(Direction direction) {
        held.remove(direction);
    }

    public void clearHeld() {
        held.clear();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }

    // ================= aggiornamento =================

    private void step(double dt) {
        cooldown = Math.max(0, cooldown - dt);
        flash = Math.max(0, flash - dt);
        Optional<GameMap> map = game.getMap();
        Player player = game.getPlayer();
        if (map.isEmpty() || player == null) {
            return;
        }
        GamePhase phase = game.getPhase();
        boolean canWalk = phase == GamePhase.HUB || phase == GamePhase.EXPLORING;
        if (canWalk && !held.isEmpty() && cooldown == 0) {
            game.move(held.peekFirst());
            cooldown = MOVE_INTERVAL;
        }
        Position target = game.getPosition();
        if (map.get() != lastMap) {
            lastMap = map.get();
            renderX = target.x();
            renderY = target.y();
        }
        double k = Math.min(1, dt * 16);
        renderX += (target.x() - renderX) * k;
        renderY += (target.y() - renderY) * k;
        if (lastHp >= 0 && player.getHp() < lastHp) {
            flash = 0.35;
        }
        lastHp = player.getHp();
    }

    // ================= disegno =================

    private void render() {
        double w = getWidth();
        double h = getHeight();
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#050508"));
        g.fillRect(0, 0, w, h);
        Optional<GameMap> optionalMap = game.getMap();
        if (optionalMap.isEmpty() || game.getPlayer() == null || w <= 0 || h <= 0) {
            return;
        }
        GameMap map = optionalMap.get();
        boolean dungeon = game.getRun().isPresent();
        String theme = game.getTheme();
        Position hero = game.getPosition();

        double camX = Math.round(camera(renderX * S + S / 2, w, map.getWidth() * S));
        double camY = Math.round(camera(renderY * S + S / 2, h, map.getHeight() * S));
        int x0 = Math.max(0, (int) Math.floor(camX / S));
        int y0 = Math.max(0, (int) Math.floor(camY / S));
        int x1 = Math.min(map.getWidth() - 1, (int) Math.floor((camX + w) / S));
        int y1 = Math.min(map.getHeight() - 1, (int) Math.floor((camY + h) / S));

        g.save();
        g.translate(-camX, -camY);
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (!dungeon || map.isExplored(x, y)) {
                    TileRenderer.drawTile(g, theme, map, x, y, time);
                }
            }
        }
        drawEntities(g, map, dungeon, hero, x0, y0, x1, y1);
        if (dungeon) {
            drawDarkness(g, map, hero, x0, y0, x1, y1);
        }
        g.restore();

        if (flash > 0) {
            g.setGlobalAlpha(flash);
            g.setFill(Color.web("#c0392b"));
            g.fillRect(0, 0, w, h);
            g.setGlobalAlpha(1);
        }
        drawHud(g, w, h, map, dungeon, hero);
    }

    private void drawEntities(GraphicsContext g, GameMap map, boolean dungeon, Position hero,
                              int x0, int y0, int x1, int y1) {
        List<Drawable> drawables = new ArrayList<>();
        int radiusSquared = Game.VIEW_RADIUS * Game.VIEW_RADIUS;
        for (MapObject o : map.getObjects()) {
            Position p = o.getPosition();
            if (p.x() < x0 - 1 || p.x() > x1 + 1 || p.y() < y0 - 1 || p.y() > y1 + 1) {
                continue;
            }
            boolean isMonster = o.getType() == ObjectType.ENEMY || o.getType() == ObjectType.BOSS;
            if (isMonster && !o.isActive()) {
                continue;
            }
            if (dungeon) {
                boolean seen = isMonster ? p.distanceSquared(hero) <= radiusSquared : map.isExplored(p.x(), p.y());
                if (!seen) {
                    continue;
                }
            }
            boolean unlocked = o.getType() != ObjectType.GATE || game.isGateUnlocked(o.getRefId());
            boolean flip = p.x() < hero.x();
            drawables.add(new Drawable(p.y() * S + S, () -> ObjectRenderer.draw(g, o, unlocked, flip, time)));
        }
        drawables.add(new Drawable(renderY * S + S, () -> drawHero(g)));
        drawables.sort(Comparator.comparingDouble(Drawable::foot));
        drawables.forEach(d -> d.drawing().run());
    }

    private void drawHero(GraphicsContext g) {
        Position target = game.getPosition();
        boolean moving = Math.abs(renderX - target.x()) + Math.abs(renderY - target.y()) > 0.05;
        double bob = moving ? Math.abs(Math.sin(time * 18)) * 3 : 0;
        double x = renderX * S + S / 2;
        double foot = renderY * S + S - 4 - bob;
        Draw.glow(g, "#ffd27a", 0.10, x - 40, foot - 40, 80, 60);
        Sprites.drawHeroOnMap(g, game.getPlayer().getPlayerClass(), x, foot, time, 0.36,
                game.getFacing() == Direction.LEFT);
    }

    /** Oscura le caselle lontane (sfumatura) e quelle già esplorate ma non più in vista. */
    private void drawDarkness(GraphicsContext g, GameMap map, Position hero, int x0, int y0, int x1, int y1) {
        double radius = Game.VIEW_RADIUS;
        g.setFill(Color.BLACK);
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (!map.isExplored(x, y)) {
                    continue;
                }
                double d = Math.sqrt(new Position(x, y).distanceSquared(hero));
                double alpha = d <= radius ? Math.max(0, (d - 2.0) / (radius - 0.5)) * 0.6 : 0.72;
                if (alpha > 0.01) {
                    g.setGlobalAlpha(alpha);
                    g.fillRect(x * S, y * S, S, S);
                }
            }
        }
        g.setGlobalAlpha(1);
    }

    private static double camera(double center, double viewSize, double mapSize) {
        if (mapSize <= viewSize) {
            return -(viewSize - mapSize) / 2;
        }
        return Math.max(0, Math.min(center - viewSize / 2, mapSize - viewSize));
    }

    // ================= HUD =================

    private void drawHud(GraphicsContext g, double w, double h, GameMap map, boolean dungeon, Position hero) {
        if (dungeon) {
            var run = game.getRun().orElseThrow();
            Draw.panel(g, 10, 10, 250, 48);
            Draw.textLeft(g, "#ffd54a", 15, run.getDungeon().getName(), 20, 30);
            Draw.textLeft(g, "#ffffff", 12, "Nemici rimasti: " + run.enemiesRemaining() + "   Obiettivo: sconfiggi il boss",
                    20, 49);
            drawMinimap(g, w, map, hero);
        } else {
            Draw.panel(g, 10, 10, 190, 30);
            Draw.textLeft(g, "#ffd54a", 15, "Villaggio di partenza", 20, 31);
        }
        String hint = dungeon
                ? "WASD / frecce: muoviti   -   urta i nemici per combattere   -   H: pozione cura   M: pozione mana"
                : "WASD / frecce: muoviti   -   urta locanda, negozio, cristallo (salva) e cancelli per interagire";
        double panelWidth = Math.min(w - 20, 640);
        Draw.panel(g, (w - panelWidth) / 2, h - 36, panelWidth, 26);
        Draw.text(g, "#dddddd", 12, hint, w / 2, h - 18);
    }

    private void drawMinimap(GraphicsContext g, double w, GameMap map, Position hero) {
        double mw = map.getWidth() * MINIMAP_TILE;
        double mh = map.getHeight() * MINIMAP_TILE;
        double ox = w - mw - 18;
        double oy = 14;
        Draw.panel(g, ox - 6, oy - 6, mw + 12, mh + 12);
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (!map.isExplored(x, y)) {
                    continue;
                }
                String color = map.tileAt(x, y) == TileType.FLOOR ? "#6a6a8c" : "#22222f";
                Draw.rect(g, color, ox + x * MINIMAP_TILE, oy + y * MINIMAP_TILE, MINIMAP_TILE, MINIMAP_TILE);
            }
        }
        for (MapObject o : map.getObjects()) {
            Position p = o.getPosition();
            if (!map.isExplored(p.x(), p.y()) || !o.isActive()) {
                continue;
            }
            String color = switch (o.getType()) {
                case BOSS -> "#ff4040";
                case CHEST -> "#ffd24a";
                case FOUNTAIN -> "#4aa8e8";
                default -> null;
            };
            if (color != null) {
                Draw.oval(g, color, ox + p.x() * MINIMAP_TILE - 1, oy + p.y() * MINIMAP_TILE - 1, 6, 6);
            }
        }
        double blink = 0.6 + Math.abs(Math.sin(time * 4)) * 0.4;
        Draw.withAlpha(g, blink, () ->
                Draw.oval(g, "#ffffff", ox + hero.x() * MINIMAP_TILE - 1, oy + hero.y() * MINIMAP_TILE - 1, 6, 6));
    }
}
