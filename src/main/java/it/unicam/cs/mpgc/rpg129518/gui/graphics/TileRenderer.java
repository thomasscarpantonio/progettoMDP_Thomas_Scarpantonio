package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import it.unicam.cs.mpgc.rpg129518.model.world.GameMap;
import it.unicam.cs.mpgc.rpg129518.model.world.TileType;
import javafx.scene.canvas.GraphicsContext;

/** Disegna le caselle del terreno (prato e case del villaggio, pavimenti e muri dei dungeon). */
final class TileRenderer {

    /** Lato di una casella in pixel. */
    static final double S = 48;

    private record Palette(String floorA, String floorB, String wallFace, String wallBrick, String wallTop) {
    }

    private TileRenderer() {
    }

    static int hash(int x, int y) {
        int h = x * 73856093 ^ y * 19349663;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return h & 0x7fffffff;
    }

    static void drawTile(GraphicsContext g, String theme, GameMap map, int x, int y, double t) {
        if ("village".equals(theme)) {
            village(g, map, x, y);
        } else {
            dungeon(g, theme, map, x, y, t);
        }
    }

    // ================= villaggio =================

    private static void village(GraphicsContext g, GameMap map, int x, int y) {
        double px = x * S;
        double py = y * S;
        TileType tile = map.tileAt(x, y);
        if (tile == TileType.BUILDING) {
            building(g, map, x, y, px, py);
            return;
        }
        grass(g, x, y, px, py);
        if (tile == TileType.TREE) {
            Draw.shadow(g, px + S / 2, py + S - 4, 40);
            Draw.rect(g, "#6b4a2a", px + 20, py + 26, 8, 20);
            Draw.oval(g, "#2f7a3a", px + 2, py - 4, 44, 36);
            Draw.oval(g, "#3a8f46", px + 8, py - 10, 32, 28);
            Draw.oval(g, "#4aa656", px + 14, py - 8, 14, 11);
        }
    }

    private static void grass(GraphicsContext g, int x, int y, double px, double py) {
        int h = hash(x, y);
        Draw.rect(g, h % 3 == 0 ? "#4a933b" : h % 3 == 1 ? "#4f9a3f" : "#55a244", px, py, S, S);
        if (h % 7 == 0) {
            Draw.line(g, "#3d7d30", 2, px + 10, py + 32, px + 8, py + 23);
            Draw.line(g, "#3d7d30", 2, px + 14, py + 32, px + 16, py + 22);
        }
        if (h % 29 == 0) {
            Draw.oval(g, "#ffffff", px + 30, py + 12, 6, 6);
            Draw.oval(g, "#ffd24a", px + 32, py + 14, 2, 2);
        } else if (h % 31 == 0) {
            Draw.oval(g, "#ff8fb1", px + 12, py + 28, 6, 6);
        }
    }

    private static void building(GraphicsContext g, GameMap map, int x, int y, double px, double py) {
        boolean roof = map.tileAt(x, y + 1) == TileType.BUILDING;
        if (roof) {
            Draw.rect(g, "#a63d34", px, py, S, S);
            for (int i = 1; i < 4; i++) {
                Draw.line(g, "#7d2a24", 2, px, py + 12 * i, px + S, py + 12 * i);
            }
            for (int row = 0; row < 4; row++) {
                double offset = (row % 2) * 12;
                for (double vx = offset; vx < S; vx += 24) {
                    Draw.line(g, "#7d2a24", 1, px + vx, py + 12 * row, px + vx, py + 12 * row + 12);
                }
            }
            if (map.tileAt(x, y - 1) != TileType.BUILDING) {
                Draw.rect(g, "#6e211c", px, py, S, 5);
            }
        } else {
            Draw.rect(g, "#d9b88a", px, py, S, S);
            for (double vx = 12; vx < S; vx += 12) {
                Draw.line(g, "#b8956a", 1, px + vx, py, px + vx, py + S);
            }
            Draw.rect(g, "#8a6a45", px, py + S - 6, S, 6);
            if (hash(x, y) % 2 == 0) {
                Draw.rect(g, "#5a3a22", px + 11, py + 9, 26, 24);
                Draw.rect(g, "#8fc6ec", px + 14, py + 12, 20, 18);
                Draw.line(g, "#5a3a22", 2, px + 24, py + 12, px + 24, py + 30);
                Draw.line(g, "#5a3a22", 2, px + 14, py + 21, px + 34, py + 21);
            }
        }
    }

    // ================= dungeon =================

    private static Palette palette(String theme) {
        return switch (theme) {
            case "caves" -> new Palette("#6b5440", "#614b39", "#4a3626", "#3a2a1c", "#1e150e");
            case "fortress" -> new Palette("#3d3238", "#352b30", "#5a2a2a", "#3f1c1c", "#1e0b0b");
            default -> new Palette("#4a4a5e", "#434356", "#3a3a55", "#2a2a40", "#14141f");
        };
    }

    private static void dungeon(GraphicsContext g, String theme, GameMap map, int x, int y, double t) {
        double px = x * S;
        double py = y * S;
        Palette p = palette(theme);
        int h = hash(x, y);

        if (map.tileAt(x, y) == TileType.FLOOR) {
            Draw.rect(g, (h & 1) == 0 ? p.floorA() : p.floorB(), px, py, S, S);
            Draw.withAlpha(g, 0.25, () -> {
                Draw.line(g, "#000000", 2, px, py, px + S, py);
                Draw.line(g, "#000000", 2, px, py, px, py + S);
            });
            floorDetail(g, theme, h, px, py, t);
            if (map.tileAt(x, y - 1) == TileType.WALL) {
                Draw.withAlpha(g, 0.35, () -> Draw.rect(g, "#000000", px, py, S, 10));
            }
            return;
        }

        boolean face = map.tileAt(x, y + 1) == TileType.FLOOR;
        if (!face) {
            Draw.rect(g, p.wallTop(), px, py, S, S);
            if (h % 4 == 0) {
                Draw.withAlpha(g, 0.08, () -> Draw.rect(g, "#ffffff", px + 6, py + 6, 20, 14));
            }
            return;
        }
        Draw.rect(g, p.wallFace(), px, py, S, S);
        for (int row = 0; row < 3; row++) {
            double y0 = py + row * 16;
            double offset = (row % 2) * 12;
            Draw.line(g, p.wallBrick(), 2, px, y0, px + S, y0);
            for (double vx = offset + 12; vx < S; vx += 24) {
                Draw.line(g, p.wallBrick(), 2, px + vx, y0, px + vx, y0 + 16);
            }
        }
        Draw.withAlpha(g, 0.10, () -> Draw.rect(g, "#ffffff", px, py, S, 5));
        if (h % 13 == 0) {
            torch(g, px, py, t + x * 1.7 + y);
        }
    }

    private static void floorDetail(GraphicsContext g, String theme, int h, double px, double py, double t) {
        switch (theme) {
            case "caves" -> {
                if (h % 5 == 0) {
                    Draw.oval(g, "#4a3a2a", px + 12, py + 28, 10, 7);
                    Draw.oval(g, "#7a6246", px + 30, py + 14, 6, 5);
                }
            }
            case "fortress" -> {
                if (h % 6 == 0) {
                    double pulse = 0.6 + Math.sin(t * 2 + h) * 0.3;
                    Draw.withAlpha(g, pulse, () -> {
                        Draw.line(g, "#ff6a00", 2, px + 8, py + 12, px + 22, py + 24);
                        Draw.line(g, "#ff6a00", 2, px + 22, py + 24, px + 40, py + 20);
                    });
                }
            }
            default -> {
                if (h % 9 == 0) {
                    Draw.line(g, "#2c2c3c", 1, px + 10, py + 10, px + 22, py + 22);
                    Draw.line(g, "#2c2c3c", 1, px + 22, py + 22, px + 20, py + 34);
                }
            }
        }
    }

    private static void torch(GraphicsContext g, double px, double py, double phase) {
        double flicker = Math.sin(phase * 12) * 2;
        Draw.glow(g, "#ff9a1a", 0.20, px - 16, py - 12, 80, 80);
        Draw.rect(g, "#3a2a1a", px + 22, py + 18, 4, 14);
        Draw.oval(g, "#ff9a1a", px + 18, py + 4 - flicker, 12, 18 + flicker);
        Draw.oval(g, "#ffe066", px + 21, py + 10, 6, 10);
    }
}
