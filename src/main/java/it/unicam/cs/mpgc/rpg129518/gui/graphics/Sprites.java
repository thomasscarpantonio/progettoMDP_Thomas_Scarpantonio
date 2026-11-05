package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import it.unicam.cs.mpgc.rpg129518.model.entity.PlayerClass;
import javafx.scene.canvas.GraphicsContext;

/**
 * Personaggio e mostri disegnati con forme geometriche. Tutte le coordinate dei mostri sono locali:
 * l'origine è tra i piedi e l'asse y negativo va verso l'alto. I mostri guardano verso sinistra (verso l'eroe).
 */
public final class Sprites {

    private Sprites() {
    }

    /** Disegna anteprima dell'eroe su sfondo del villaggio (menu principale). */
    public static void drawPreview(GraphicsContext g, PlayerClass playerClass, double time) {
        g.save();
        g.scale(0.6, 0.6);
        Backdrop.draw(g, "village", time);
        drawHero(g, playerClass, 300, Backdrop.GROUND, time);
        g.restore();
    }

    /** Eroe in scala ridotta per la mappa; flip = guarda verso sinistra. */
    static void drawHeroOnMap(GraphicsContext g, PlayerClass playerClass, double x, double ground, double t,
                              double scale, boolean flip) {
        g.save();
        g.translate(x, ground);
        g.scale(flip ? -scale : scale, scale);
        drawHero(g, playerClass, 0, 0, t);
        g.restore();
    }

    /** Mostro in scala ridotta per la mappa; flip = guarda verso destra (i mostri nascono rivolti a sinistra). */
    static void drawMonsterOnMap(GraphicsContext g, String id, boolean boss, double x, double ground, double t,
                                 double scale, boolean flip) {
        g.save();
        g.translate(x, ground);
        g.scale(flip ? -scale : scale, scale);
        drawMonster(g, id, boss, 0, 0, t);
        g.restore();
    }

    // ================= eroe =================

    static void drawHero(GraphicsContext g, PlayerClass playerClass, double x, double ground, double t) {
        double b = Math.sin(t * 3) * 2;
        Draw.shadow(g, x, ground, 60);
        String tunic = switch (playerClass) {
            case WARRIOR -> "#6b7a8f";
            case MAGE -> "#5b3fa0";
            case ROGUE -> "#2f5d3a";
        };
        Draw.rect(g, "#3b2f2f", x - 14, ground - 30, 10, 30);
        Draw.rect(g, "#3b2f2f", x + 4, ground - 30, 10, 30);
        Draw.rrect(g, tunic, x - 20, ground - 72 + b, 40, 46, 12);
        Draw.rect(g, "#5a3a22", x - 20, ground - 44 + b, 40, 5);
        Draw.oval(g, "#f0c9a0", x - 13, ground - 98 + b, 26, 26);
        Draw.oval(g, "#222222", x - 7, ground - 89 + b, 4, 4);
        Draw.oval(g, "#222222", x + 3, ground - 89 + b, 4, 4);

        switch (playerClass) {
            case WARRIOR -> {
                Draw.arc(g, "#9aa5b1", x - 15, ground - 104 + b, 30, 28, 0, 180);
                Draw.poly(g, "#c0392b", x - 3, ground - 104 + b, x + 4, ground - 104 + b, x + 12, ground - 118 + b);
                Draw.rrect(g, "#3a6ea5", x - 38, ground - 70 + b, 18, 30, 8);
                Draw.rect(g, "#d8dee6", x - 31, ground - 66 + b, 4, 22);
                double sway = Math.sin(t * 2) * 3;
                Draw.line(g, "#d8dee6", 4, x + 24, ground - 52 + b, x + 32 + sway, ground - 112 + b);
                Draw.line(g, "#8b5a2b", 5, x + 17, ground - 54 + b, x + 33, ground - 50 + b);
            }
            case MAGE -> {
                Draw.poly(g, "#5b3fa0", x - 20, ground - 92 + b, x + 20, ground - 92 + b, x + 4, ground - 142 + b);
                Draw.oval(g, "#4a3290", x - 25, ground - 99 + b, 50, 11);
                Draw.line(g, "#8b5a2b", 4, x + 30, ground, x + 30, ground - 112 + b);
                double r = 8 + Math.sin(t * 5) * 2;
                Draw.glow(g, "#7fe0ff", 0.35, x + 30 - r * 2, ground - 118 + b - r * 2, r * 4, r * 4);
                Draw.oval(g, "#7fe0ff", x + 30 - r, ground - 118 + b - r, r * 2, r * 2);
            }
            case ROGUE -> {
                Draw.arc(g, "#244a2f", x - 17, ground - 104 + b, 34, 32, 0, 180);
                Draw.rect(g, "#7a1f1f", x - 14, ground - 76 + b, 28, 5);
                Draw.line(g, "#d8dee6", 3, x + 22, ground - 46 + b, x + 38, ground - 74 + b);
                Draw.line(g, "#d8dee6", 3, x - 22, ground - 46 + b, x - 38, ground - 74 + b);
            }
        }
    }

    // ================= mostri =================

    static void drawMonster(GraphicsContext g, String id, boolean boss, double x, double ground, double t) {
        g.save();
        g.translate(x, ground);
        double scale = boss ? ("dragon".equals(id) ? 1.15 : 1.35) : 1.0;
        g.scale(scale, scale);
        Draw.shadow(g, 0, 0, "dragon".equals(id) ? 150 : 70);
        switch (id) {
            case "giant_rat" -> rat(g, t);
            case "skeleton" -> skeleton(g, t, false);
            case "skeleton_king" -> skeleton(g, t, true);
            case "zombie" -> zombie(g, t);
            case "goblin" -> goblin(g, t);
            case "cave_spider" -> spider(g, t);
            case "orc" -> orc(g, t);
            case "troll" -> troll(g, t);
            case "dark_knight" -> darkKnight(g, t);
            case "corrupted_mage" -> corruptedMage(g, t);
            case "gargoyle" -> gargoyle(g, t);
            case "dragon" -> dragon(g, t);
            default -> blob(g, t);
        }
        g.restore();
    }

    private static void rat(GraphicsContext g, double t) {
        double b = Math.sin(t * 6) * 1.5;
        Draw.line(g, "#d49aa0", 3, 26, -16, 50, -30 + Math.sin(t * 5) * 6);
        Draw.oval(g, "#7a6a5a", -28, -34 + b, 58, 30);
        Draw.oval(g, "#8a7868", -46, -36 + b, 28, 22);
        Draw.oval(g, "#7a6a5a", -30, -48 + b, 12, 12);
        Draw.oval(g, "#e8a0a0", -52, -30 + b, 8, 6);
        Draw.oval(g, "#d03030", -40, -31 + b, 5, 5);
        Draw.rect(g, "#5a4a3a", -18, -6, 6, 6);
        Draw.rect(g, "#5a4a3a", 14, -6, 6, 6);
    }

    private static void skeleton(GraphicsContext g, double t, boolean king) {
        double b = Math.sin(t * 2.5) * 2;
        if (king) {
            Draw.poly(g, "#7a1f2f", -18, -84 + b, 18, -84 + b, 28, -6, -28, -6);
        }
        Draw.rect(g, "#e8e4d0", -12, -40, 6, 40);
        Draw.rect(g, "#e8e4d0", 6, -40, 6, 40);
        Draw.rrect(g, "#e8e4d0", -16, -84 + b, 32, 44, 8);
        for (int i = 0; i < 4; i++) {
            Draw.line(g, "#555555", 2, -12, -76 + b + i * 9, 12, -76 + b + i * 9);
        }
        Draw.oval(g, "#e8e4d0", -13, -110 + b, 26, 26);
        Draw.oval(g, "#111111", -8, -102 + b, 7, 8);
        Draw.oval(g, "#111111", 2, -102 + b, 7, 8);
        Draw.rect(g, "#e8e4d0", -8, -86 + b, 16, 5);
        Draw.line(g, "#e8e4d0", 4, -16, -78 + b, -32, -54 + b + Math.sin(t * 3) * 3);
        Draw.line(g, "#e8e4d0", 4, 16, -78 + b, 34, -62 + b);
        Draw.line(g, "#aab4c0", 4, 34, -62 + b, 40, -110 + b);
        if (king) {
            Draw.poly(g, "#f1c40f", -13, -108 + b, -13, -124 + b, -6, -116 + b, 0, -126 + b, 6, -116 + b,
                    13, -124 + b, 13, -108 + b);
        }
    }

    private static void zombie(GraphicsContext g, double t) {
        double b = Math.sin(t * 2) * 2;
        Draw.rect(g, "#4a5a3a", -12, -38, 10, 38);
        Draw.rect(g, "#4a5a3a", 2, -38, 10, 38);
        Draw.rrect(g, "#5a4632", -18, -84 + b, 36, 48, 8);
        Draw.oval(g, "#7fa86a", -14, -112 + b, 28, 28);
        Draw.oval(g, "#ffe14a", -8, -102 + b, 5, 5);
        Draw.oval(g, "#ffe14a", 2, -102 + b, 5, 5);
        Draw.line(g, "#3a1010", 2, -6, -91 + b, 6, -91 + b);
        double sway = Math.sin(t * 2.5) * 3;
        Draw.line(g, "#7fa86a", 8, -14, -78 + b, -52, -76 + b + sway);
        Draw.line(g, "#7fa86a", 8, -14, -66 + b, -50, -62 + b - sway);
    }

    private static void goblin(GraphicsContext g, double t) {
        double b = Math.sin(t * 4) * 2;
        Draw.rect(g, "#3d5a2a", -9, -24, 7, 24);
        Draw.rect(g, "#3d5a2a", 3, -24, 7, 24);
        Draw.rrect(g, "#6a4a2a", -13, -54 + b, 26, 32, 6);
        Draw.poly(g, "#6fae45", -15, -66 + b, -36, -78 + b, -14, -56 + b);
        Draw.poly(g, "#6fae45", 15, -66 + b, 36, -78 + b, 14, -56 + b);
        Draw.oval(g, "#6fae45", -16, -82 + b, 32, 28);
        Draw.oval(g, "#ffe14a", -9, -72 + b, 6, 6);
        Draw.oval(g, "#ffe14a", 3, -72 + b, 6, 6);
        Draw.poly(g, "#ffffff", -6, -62 + b, -3, -57 + b, 0, -62 + b);
        Draw.line(g, "#c9ced6", 3, -14, -44 + b, -30, -62 + b);
    }

    private static void spider(GraphicsContext g, double t) {
        double b = Math.sin(t * 5) * 1.5;
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i < 4; i++) {
                double sx = 6 * i - 9;
                double w = Math.sin(t * 6 + i) * 4;
                Draw.line(g, "#1b0f26", 3, sx, -30 + b, sx + side * 22, -56 + b);
                Draw.line(g, "#1b0f26", 3, sx + side * 22, -56 + b, sx + side * 38, -2 + w);
            }
        }
        Draw.oval(g, "#2d1b3d", -22, -48 + b, 48, 32);
        Draw.oval(g, "#3b2650", -38, -44 + b, 22, 22);
        Draw.oval(g, "#ff3030", -34, -40 + b, 5, 5);
        Draw.oval(g, "#ff3030", -27, -40 + b, 5, 5);
    }

    private static void orc(GraphicsContext g, double t) {
        double b = Math.sin(t * 2.2) * 2;
        Draw.rect(g, "#2f3a22", -16, -40, 13, 40);
        Draw.rect(g, "#2f3a22", 3, -40, 13, 40);
        Draw.rrect(g, "#8a5a32", -24, -94 + b, 48, 58, 10);
        Draw.rrect(g, "#4a7a2e", -38, -90 + b, 14, 38, 6);
        Draw.rrect(g, "#4a7a2e", 24, -90 + b, 14, 38, 6);
        Draw.oval(g, "#4a7a2e", -16, -124 + b, 32, 32);
        Draw.oval(g, "#e03030", -10, -111 + b, 6, 5);
        Draw.oval(g, "#e03030", 4, -111 + b, 6, 5);
        Draw.poly(g, "#f4efe0", -10, -96 + b, -6, -104 + b, -4, -96 + b);
        Draw.poly(g, "#f4efe0", 10, -96 + b, 6, -104 + b, 4, -96 + b);
        Draw.line(g, "#6b4a2a", 4, 30, -58 + b, 38, -126 + b);
        Draw.poly(g, "#aab4c0", 38, -126 + b, 60, -120 + b, 60, -96 + b, 38, -100 + b);
    }

    private static void troll(GraphicsContext g, double t) {
        double b = Math.sin(t * 1.8) * 2.5;
        Draw.rect(g, "#5b6b57", -20, -46, 16, 46);
        Draw.rect(g, "#5b6b57", 4, -46, 16, 46);
        Draw.oval(g, "#6f8068", -34, -112 + b, 68, 74);
        Draw.oval(g, "#7d9076", -18, -142 + b, 36, 32);
        Draw.oval(g, "#ffd24a", -10, -130 + b, 6, 5);
        Draw.oval(g, "#ffd24a", 4, -130 + b, 6, 5);
        Draw.line(g, "#2a1a1a", 3, -8, -116 + b, 8, -116 + b);
        Draw.oval(g, "#6f8068", -50, -106 + b, 20, 58);
        Draw.oval(g, "#6f8068", 30, -106 + b, 20, 58);
        Draw.line(g, "#6b4a2a", 10, 44, -54 + b, 52, -132 + b);
        Draw.oval(g, "#5a3a22", 40, -154 + b, 28, 30);
    }

    private static void darkKnight(GraphicsContext g, double t) {
        double b = Math.sin(t * 2.2) * 2;
        Draw.rect(g, "#2b2f3a", -13, -40, 11, 40);
        Draw.rect(g, "#2b2f3a", 2, -40, 11, 40);
        Draw.rrect(g, "#3a3f4d", -21, -90 + b, 42, 52, 8);
        Draw.oval(g, "#4a5060", -28, -92 + b, 16, 16);
        Draw.oval(g, "#4a5060", 12, -92 + b, 16, 16);
        Draw.rrect(g, "#2f3340", -14, -120 + b, 28, 32, 10);
        Draw.poly(g, "#8b1a1a", 0, -120 + b, -4, -138 + b, 16, -124 + b);
        Draw.rect(g, "#ff3030", -10, -106 + b, 20, 4);
        Draw.rrect(g, "#1c1c24", -42, -86 + b, 18, 36, 6);
        Draw.line(g, "#c9ced6", 5, 26, -62 + b, 32, -128 + b);
        Draw.line(g, "#6b4a2a", 6, 20, -62 + b, 38, -58 + b);
    }

    private static void corruptedMage(GraphicsContext g, double t) {
        double b = Math.sin(t * 2.5) * 2;
        Draw.poly(g, "#4b1d6e", -12, -98 + b, 12, -98 + b, 28, 0, -28, 0);
        Draw.oval(g, "#3a1455", -15, -124 + b, 30, 32);
        Draw.oval(g, "#ff4de0", -9, -111 + b, 7, 6);
        Draw.oval(g, "#ff4de0", 2, -111 + b, 7, 6);
        Draw.line(g, "#6b4a2a", 4, 24, 0, 24, -120 + b);
        double oy = -112 + Math.sin(t * 3) * 8;
        Draw.glow(g, "#ff4de0", 0.3, -70, oy - 10, 36, 36);
        Draw.oval(g, "#ff4de0", -62, oy, 18, 18);
    }

    private static void gargoyle(GraphicsContext g, double t) {
        double b = Math.sin(t * 2) * 2;
        double flap = Math.sin(t * 4) * 10;
        Draw.poly(g, "#6b6f78", -14, -84 + b, -64, -122 + b + flap, -52, -58 + b);
        Draw.poly(g, "#6b6f78", 14, -84 + b, 64, -122 + b + flap, 52, -58 + b);
        Draw.rect(g, "#8a8f99", -16, -26, 12, 26);
        Draw.rect(g, "#8a8f99", 4, -26, 12, 26);
        Draw.oval(g, "#8a8f99", -21, -86 + b, 42, 64);
        Draw.oval(g, "#9aa0aa", -14, -114 + b, 28, 28);
        Draw.poly(g, "#555555", -12, -108 + b, -18, -126 + b, -6, -112 + b);
        Draw.poly(g, "#555555", 12, -108 + b, 18, -126 + b, 6, -112 + b);
        Draw.oval(g, "#ff8a00", -8, -102 + b, 5, 5);
        Draw.oval(g, "#ff8a00", 3, -102 + b, 5, 5);
    }

    private static void dragon(GraphicsContext g, double t) {
        double b = Math.sin(t * 1.8) * 3;
        double flap = Math.sin(t * 3) * 14;
        Draw.poly(g, "#a8221c", 40, -56, 100, -24 + Math.sin(t * 3) * 6, 40, -22);
        Draw.poly(g, "#7a1713", 6, -92 + b, 46, -160 + flap, 78, -84 + b);
        Draw.rect(g, "#8f1d17", -28, -32, 16, 32);
        Draw.rect(g, "#8f1d17", 20, -32, 16, 32);
        Draw.oval(g, "#b3261e", -42, -92 + b, 104, 66);
        Draw.oval(g, "#e8b46a", -36, -72 + b, 62, 40);
        Draw.poly(g, "#b3261e", -34, -72 + b, -54, -124 + b, -30, -128 + b, -10, -82 + b);
        Draw.oval(g, "#b3261e", -74, -142 + b, 46, 30);
        Draw.oval(g, "#ffe14a", -62, -134 + b, 8, 7);
        Draw.poly(g, "#e8dcc0", -50, -140 + b, -44, -158 + b, -40, -140 + b);
        for (int i = 0; i < 3; i++) {
            double flicker = Math.sin(t * 14 + i) * 4;
            Draw.glow(g, i == 0 ? "#ff9a1a" : "#ffd24a", 0.55, -92 - i * 14 - flicker, -130 + b - i * 2, 22, 14);
        }
    }

    private static void blob(GraphicsContext g, double t) {
        double b = Math.sin(t * 3) * 3;
        Draw.oval(g, "#5a8f5a", -30, -60 + b, 60, 60);
        Draw.oval(g, "#ffffff", -14, -40 + b, 10, 10);
        Draw.oval(g, "#ffffff", 4, -40 + b, 10, 10);
        Draw.oval(g, "#111111", -11, -37 + b, 4, 4);
        Draw.oval(g, "#111111", 7, -37 + b, 4, 4);
    }
}
