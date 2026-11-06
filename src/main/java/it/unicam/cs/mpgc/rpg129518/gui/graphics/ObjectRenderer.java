package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import it.unicam.cs.mpgc.rpg129518.model.world.MapObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Disegna gli oggetti posati sulla mappa: mostri, scrigni, fontane, trappole e punti di interesse del villaggio. */
final class ObjectRenderer {

    private static final double S = TileRenderer.S;

    private ObjectRenderer() {
    }

    /**
     * @param gateUnlocked per i cancelli del villaggio: false = sbarrato
     * @param flip         per i mostri: true = guardano verso destra (l'eroe è alla loro destra)
     */
    static void draw(GraphicsContext g, MapObject o, boolean gateUnlocked, boolean flip, double t) {
        double px = o.getPosition().x() * S;
        double py = o.getPosition().y() * S;
        switch (o.getType()) {
            case ENEMY, BOSS -> monster(g, o, px, py, flip, t);
            case CHEST -> chest(g, o.isActive(), px, py, t);
            case FOUNTAIN -> fountain(g, o.isActive(), px, py, t);
            case TRAP -> {
                if (!o.isActive()) {
                    trap(g, px, py);
                }
            }
            case INN -> inn(g, o, px, py, t);
            case SHOP -> shop(g, o, px, py);
            case SAVE_POINT -> savePoint(g, o, px, py, t);
            case GATE -> gate(g, o, gateUnlocked, px, py, t);
        }
    }

    private static void monster(GraphicsContext g, MapObject o, double px, double py, boolean flip, double t) {
        double cx = px + S / 2;
        double foot = py + S - 4;
        boolean boss = o.getType() == it.unicam.cs.mpgc.rpg129518.model.world.ObjectType.BOSS;
        if (boss) {
            Draw.glow(g, "#ff2a2a", 0.25 + Math.sin(t * 3) * 0.1, cx - 40, foot - 20, 80, 36);
        }
        Sprites.drawMonsterOnMap(g, o.getRefId(), boss, cx, foot, t + px * 0.01, boss ? 0.5 : 0.42, flip);
    }

    private static void chest(GraphicsContext g, boolean closed, double px, double py, double t) {
        Draw.shadow(g, px + S / 2, py + S - 4, 36);
        if (closed) {
            Draw.rrect(g, "#8b5a2b", px + 8, py + 22, 32, 20, 4);
            Draw.rrect(g, "#a8703a", px + 8, py + 14, 32, 14, 7);
            Draw.rect(g, "#caa24a", px + 12, py + 14, 4, 28);
            Draw.rect(g, "#caa24a", px + 32, py + 14, 4, 28);
            Draw.oval(g, "#ffd24a", px + 21, py + 24, 7, 8);
            Draw.glow(g, "#ffe066", 0.18 + Math.sin(t * 4) * 0.08, px + 4, py + 6, 40, 40);
        } else {
            Draw.rrect(g, "#6b4420", px + 8, py + 22, 32, 20, 4);
            Draw.rect(g, "#1a0f05", px + 10, py + 22, 28, 6);
            Draw.poly(g, "#8a5a2c", px + 8, py + 22, px + 40, py + 22, px + 36, py + 6, px + 12, py + 6);
            Draw.oval(g, "#ffd24a", px + 14, py + 22, 6, 4);
            Draw.oval(g, "#ffd24a", px + 24, py + 23, 6, 4);
        }
    }

    private static void fountain(GraphicsContext g, boolean active, double px, double py, double t) {
        Draw.oval(g, "#7c7c8c", px + 3, py + 16, 42, 28);
        Draw.oval(g, "#9aa0b0", px + 7, py + 19, 34, 22);
        Draw.oval(g, active ? "#4aa8e8" : "#555a66", px + 10, py + 22, 28, 16);
        if (!active) {
            return;
        }
        Draw.glow(g, "#6ef2c8", 0.20 + Math.sin(t * 3) * 0.08, px - 4, py, 56, 56);
        double r = (t * 10) % 14;
        Draw.withAlpha(g, 1 - r / 14, () -> {
            g.setStroke(Color.web("#cfefff"));
            g.setLineWidth(1.5);
            g.strokeOval(px + 24 - r, py + 30 - r * 0.5, r * 2, r);
        });
        Draw.rect(g, "#9aa0b0", px + 22, py + 10, 4, 14);
        for (int i = 0; i < 3; i++) {
            double phase = (t * 2 + i * 0.33) % 1;
            Draw.oval(g, "#bfe6ff", px + 22 + Math.sin(i * 2.1) * 6 * phase, py + 6 + phase * 18, 4, 4);
        }
    }

    private static void trap(GraphicsContext g, double px, double py) {
        Draw.rect(g, "#2a2a2a", px + 6, py + 20, 36, 22);
        for (int i = 0; i < 4; i++) {
            Draw.poly(g, "#c0c4cc", px + 8 + i * 9, py + 38, px + 12 + i * 9, py + 18, px + 16 + i * 9, py + 38);
        }
        Draw.oval(g, "#8b1a1a", px + 14, py + 36, 8, 4);
    }

    // ================= villaggio =================

    private static void inn(GraphicsContext g, MapObject o, double px, double py, double t) {
        Draw.rect(g, "#d9b88a", px, py, S, S);
        Draw.rect(g, "#8a6a45", px, py + S - 6, S, 6);
        Draw.rrect(g, "#5a3a22", px + 9, py + 6, 30, 42, 12);
        Draw.rrect(g, "#7a5232", px + 12, py + 9, 24, 39, 10);
        Draw.oval(g, "#ffd24a", px + 30, py + 28, 4, 4);
        Draw.glow(g, "#ffd27a", 0.25 + Math.sin(t * 3) * 0.08, px - 4, py - 6, 56, 56);
        Draw.label(g, "#ffffff", 11, o.getLabel().toUpperCase(), px + S / 2, py + S + 13);
    }

    private static void shop(GraphicsContext g, MapObject o, double px, double py) {
        Draw.rect(g, "#d9b88a", px, py, S, S);
        Draw.rect(g, "#8a6a45", px, py + S - 6, S, 6);
        for (int i = 0; i < 4; i++) {
            Draw.rect(g, i % 2 == 0 ? "#c0392b" : "#f4efe0", px + i * 12, py + 2, 12, 12);
        }
        Draw.rrect(g, "#4a3a2a", px + 10, py + 16, 28, 32, 6);
        Draw.oval(g, "#ffd24a", px + 30, py + 32, 4, 4);
        Draw.label(g, "#ffffff", 11, o.getLabel().toUpperCase(), px + S / 2, py + S + 13);
    }

    private static void savePoint(GraphicsContext g, MapObject o, double px, double py, double t) {
        double cx = px + S / 2;
        double bob = Math.sin(t * 2.5) * 3;
        Draw.shadow(g, cx, py + S - 6, 30);
        Draw.rect(g, "#8a8f99", px + 14, py + 32, 20, 10);
        Draw.rect(g, "#a9aeb8", px + 11, py + 40, 26, 5);
        Draw.glow(g, "#7fe0ff", 0.30 + Math.sin(t * 3) * 0.1, px - 2, py - 4 + bob, 52, 52);
        Draw.poly(g, "#7fe0ff", cx, py + 4 + bob, cx + 9, py + 18 + bob, cx, py + 32 + bob, cx - 9, py + 18 + bob);
        Draw.poly(g, "#d6f6ff", cx, py + 4 + bob, cx + 3, py + 18 + bob, cx, py + 32 + bob);
        Draw.label(g, "#bfeeff", 11, o.getLabel().toUpperCase(), cx, py + S + 13);
    }

    private static void gate(GraphicsContext g, MapObject o, boolean unlocked, double px, double py, double t) {
        Draw.rect(g, "#59595f", px, py, S, S);
        Draw.rrect(g, "#3d3d44", px + 3, py + 2, S - 6, S - 2, 20);
        Draw.rrect(g, "#07070d", px + 9, py + 9, 30, 39, 15);
        String color = switch (o.getRefId()) {
            case "crypt" -> "#9a7bff";
            case "caves" -> "#ffb347";
            case "fortress" -> "#ff4a1a";
            default -> "#7fe0ff";
        };
        if (unlocked) {
            Draw.glow(g, color, 0.35 + Math.sin(t * 3) * 0.15, px + 10, py + 14, 28, 32);
        } else {
            for (int i = 0; i < 3; i++) {
                Draw.line(g, "#8a8a95", 2, px + 16 + i * 8, py + 10, px + 16 + i * 8, py + 48);
            }
            Draw.rrect(g, "#c9a24a", px + 19, py + 28, 10, 9, 2);
        }
        Draw.label(g, unlocked ? "#ffffff" : "#999999", 11, o.getLabel(), px + S / 2, py + S + 13);
    }
}
