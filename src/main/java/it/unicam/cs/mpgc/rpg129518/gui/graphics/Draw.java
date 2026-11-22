package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.HashMap;
import java.util.Map;

/** Piccole funzioni di utilità per disegnare forme colorate (colori in formato "#rrggbb"). */
final class Draw {
    /** Cache dei colori: ogni stringa "#rrggbb" viene convertita in Color una sola volta. */
    private static final Map<String, Color> COLORS = new HashMap<>();

    static Color parse(String hex) {
        return COLORS.computeIfAbsent(hex, Color::web);
    }

    private Draw() {
    }

    static void oval(GraphicsContext g, String color, double x, double y, double w, double h) {
        g.setFill(parse(color));
        g.fillOval(x, y, w, h);
    }

    static void rect(GraphicsContext g, String color, double x, double y, double w, double h) {
        g.setFill(parse(color));
        g.fillRect(x, y, w, h);
    }

    static void rrect(GraphicsContext g, String color, double x, double y, double w, double h, double radius) {
        g.setFill(parse(color));
        g.fillRoundRect(x, y, w, h, radius, radius);
    }

    /** Poligono dato come sequenza x1, y1, x2, y2, ... */
    static void poly(GraphicsContext g, String color, double... points) {
        int n = points.length / 2;
        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = points[2 * i];
            ys[i] = points[2 * i + 1];
        }
        g.setFill(parse(color));
        g.fillPolygon(xs, ys, n);
    }

    static void line(GraphicsContext g, String color, double width, double x1, double y1, double x2, double y2) {
        g.setStroke(parse(color));
        g.setLineWidth(width);
        g.strokeLine(x1, y1, x2, y2);
    }

    static void arc(GraphicsContext g, String color, double x, double y, double w, double h,
                    double start, double extent) {
        g.setFill(parse(color));
        g.fillArc(x, y, w, h, start, extent, ArcType.ROUND);
    }

    /** Alone semitrasparente (luci, fiamme, ombre morbide). */
    static void glow(GraphicsContext g, String color, double alpha, double x, double y, double w, double h) {
        double old = g.getGlobalAlpha();
        g.setGlobalAlpha(old * alpha);
        oval(g, color, x, y, w, h);
        g.setGlobalAlpha(old);
    }

    static void shadow(GraphicsContext g, double x, double ground, double width) {
        glow(g, "#000000", 0.35, x - width / 2, ground - width * 0.12, width, width * 0.24);
    }

    static void text(GraphicsContext g, String color, double size, String text, double x, double y) {
        g.setFill(parse(color));
        g.setFont(Font.font("SansSerif", FontWeight.BOLD, size));
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(text, x, y);
    }

    /** Esegue il disegno con una trasparenza aggiuntiva. */
    static void withAlpha(GraphicsContext g, double alpha, Runnable drawing) {
        double old = g.getGlobalAlpha();
        g.setGlobalAlpha(old * alpha);
        drawing.run();
        g.setGlobalAlpha(old);
    }

    static void textLeft(GraphicsContext g, String color, double size, String text, double x, double y) {
        g.setFill(parse(color));
        g.setFont(Font.font("SansSerif", FontWeight.BOLD, size));
        g.setTextAlign(TextAlignment.LEFT);
        g.fillText(text, x, y);
    }

    /** Testo centrato con ombra, leggibile su qualsiasi sfondo. */
    static void label(GraphicsContext g, String color, double size, String text, double x, double y) {
        text(g, "#000000", size, text, x + 1, y + 1);
        text(g, color, size, text, x, y);
    }

    /** Pannello scuro semitrasparente per i testi sovrapposti alla mappa. */
    static void panel(GraphicsContext g, double x, double y, double w, double h) {
        withAlpha(g, 0.6, () -> rrect(g, "#000000", x, y, w, h, 10));
    }
}
