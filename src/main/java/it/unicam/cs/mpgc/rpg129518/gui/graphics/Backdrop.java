package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/** Sfondi della scena: villaggio e un tema per ogni dungeon (id come in dungeons.json). */
final class Backdrop {

    static final double W = 600;
    static final double H = 320;
    static final double GROUND = 262;

    private Backdrop() {
    }

    static void draw(GraphicsContext g, String theme, double t) {
        switch (theme) {
            case "village" -> village(g, t);
            case "caves" -> caves(g, t);
            case "fortress" -> fortress(g, t);
            default -> crypt(g, t);
        }
    }

    private static void gradient(GraphicsContext g, String top, String bottom) {
        g.setFill(new LinearGradient(0, 0, 0, H, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(top)), new Stop(1, Color.web(bottom))));
        g.fillRect(0, 0, W, H);
    }

    private static void torch(GraphicsContext g, double x, double y, double t, String outer, String inner) {
        double flicker = Math.sin(t * 12 + x) * 2;
        Draw.glow(g, outer, 0.15, x - 60, y - 60, 120, 120);
        Draw.rect(g, "#5a4632", x - 3, y, 6, 26);
        Draw.oval(g, outer, x - 8, y - 18 - flicker, 16, 24 + flicker);
        Draw.oval(g, inner, x - 4, y - 10, 8, 14);
    }

    private static void village(GraphicsContext g, double t) {
        gradient(g, "#7ec8f0", "#d9f2ff");
        Draw.oval(g, "#ffe27a", 500, 30, 56, 56);
        Draw.glow(g, "#fff3b0", 0.35, 485, 15, 86, 86);
        for (int i = 0; i < 3; i++) {
            double cx = (t * 8 + i * 230) % 760 - 100;
            Draw.glow(g, "#ffffff", 0.9, cx, 50 + i * 22, 80, 24);
            Draw.glow(g, "#ffffff", 0.9, cx + 22, 40 + i * 22, 50, 26);
        }
        Draw.oval(g, "#6bb35a", -80, 190, 380, 160);
        Draw.oval(g, "#5aa24a", 250, 205, 450, 150);
        Draw.rect(g, "#4a8f3c", 0, GROUND - 8, W, H);
        Draw.rect(g, "#c9a66b", 0, GROUND + 18, W, 14);
        // casa
        Draw.rect(g, "#b07d52", 390, 150, 120, 104);
        Draw.poly(g, "#8b2e2e", 378, 154, 450, 98, 522, 154);
        Draw.rect(g, "#5a3a22", 438, 204, 28, 50);
        Draw.rect(g, "#ffe9a0", 400, 176, 26, 26);
        Draw.rect(g, "#ffe9a0", 478, 176, 22, 26);
        // albero
        Draw.rect(g, "#6b4a2a", 56, 170, 16, 84);
        Draw.oval(g, "#2f7a3a", 20, 110, 90, 80);
        Draw.oval(g, "#3a8f46", 34, 90, 62, 60);
    }

    private static void crypt(GraphicsContext g, double t) {
        gradient(g, "#14142a", "#2a2a44");
        for (int row = 0; row < 10; row++) {
            double y = row * 28;
            Draw.line(g, "#33334f", 1, 0, y, W, y);
            for (double x = (row % 2) * 30; x < W; x += 60) {
                Draw.line(g, "#33334f", 1, x, y, x, y + 28);
            }
        }
        Draw.rrect(g, "#4a4a5c", 40, GROUND - 56, 38, 56, 18);
        Draw.line(g, "#2c2c3a", 3, 59, GROUND - 44, 59, GROUND - 22);
        Draw.line(g, "#2c2c3a", 3, 50, GROUND - 36, 68, GROUND - 36);
        Draw.rrect(g, "#4a4a5c", 538, GROUND - 46, 34, 46, 16);
        torch(g, 110, 100, t, "#ff9a1a", "#ffe066");
        torch(g, 500, 100, t + 1, "#ff9a1a", "#ffe066");
        Draw.rect(g, "#3a3a4e", 0, GROUND - 8, W, H);
        Draw.line(g, "#555570", 2, 0, GROUND - 8, W, GROUND - 8);
    }

    private static void caves(GraphicsContext g, double t) {
        gradient(g, "#1f150e", "#4a3424");
        for (int i = 0; i < 11; i++) {
            double x = i * 58 + (i % 2) * 14;
            double h = 30 + (i * 37) % 55;
            Draw.poly(g, "#3a281a", x, 0, x + 42, 0, x + 21, h);
        }
        Draw.oval(g, "#3a2a1c", 130, GROUND - 40, 60, 46);
        Draw.oval(g, "#33241a", 380, GROUND - 28, 80, 34);
        for (double mx : new double[]{60, 540}) {
            double pulse = 0.25 + Math.sin(t * 2 + mx) * 0.1;
            Draw.glow(g, "#6ef2c8", pulse, mx - 30, GROUND - 46, 70, 60);
            Draw.rect(g, "#d8cdb8", mx - 2, GROUND - 22, 5, 16);
            Draw.oval(g, "#6ef2c8", mx - 10, GROUND - 30, 22, 14);
        }
        Draw.rect(g, "#5a4632", 0, GROUND - 8, W, H);
        Draw.line(g, "#7a6246", 2, 0, GROUND - 8, W, GROUND - 8);
    }

    private static void fortress(GraphicsContext g, double t) {
        gradient(g, "#1a0707", "#4a1414");
        for (int i = 0; i < 12; i++) {
            Draw.rect(g, "#2a1a1a", i * 52, 0, 32, 38);
        }
        Draw.rect(g, "#2a1a1a", 0, 30, W, 14);
        for (double px : new double[]{110, 470}) {
            Draw.rect(g, "#2e2323", px, 44, 44, H);
            Draw.rect(g, "#3d2f2f", px + 6, 44, 8, H);
            torch(g, px + 22, 120, t + px, "#ff4a1a", "#ffb347");
        }
        Draw.rect(g, "#2a2020", 0, GROUND - 8, W, H);
        double pulse = 0.5 + Math.sin(t * 2) * 0.3;
        for (int i = 0; i < 6; i++) {
            double x = 20 + i * 100;
            Draw.glow(g, "#ff6a00", 0.3 * pulse, x - 30, GROUND - 2, 90, 20);
            Draw.line(g, "#ff6a00", 2, x, GROUND + 4, x + 28, GROUND + 14);
            Draw.line(g, "#ff6a00", 2, x + 28, GROUND + 14, x + 60, GROUND + 8);
        }
    }

}
