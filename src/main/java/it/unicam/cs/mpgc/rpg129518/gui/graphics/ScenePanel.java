package it.unicam.cs.mpgc.rpg129518.gui.graphics;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pannello grafico che mostra eroe, mostro e ambiente. Riceve un {@link SceneModel} a ogni cambio di stato
 * e deduce da solo le animazioni (tremolio, numeri di danno, ingresso e dissolvenza del mostro).
 */
public class ScenePanel extends Pane {

    private static final double HERO_X = 150;
    private static final double MONSTER_X = 450;
    private static final double GROUND = Backdrop.GROUND;
    private static final double HIT_SHAKE_SECONDS = 0.3;
    private static final double ENTRY_SECONDS = 0.5;
    private static final double FADE_SECONDS = 0.8;
    private static final double FLOAT_SECONDS = 1.2;

    private static final class FloatingText {
        final double x;
        double y;
        final String text;
        final String color;
        double age;

        FloatingText(double x, double y, String text, String color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
        }
    }

    private final Canvas canvas = new Canvas();
    private final List<FloatingText> floats = new ArrayList<>();
    private final AnimationTimer timer;

    private SceneModel model;
    private SceneModel fadingMonster;
    private double time;
    private long lastNanos;
    private double heroShake;
    private double monsterShake;
    private double entry;
    private double fade;

    public ScenePanel() {
        setStyle("-fx-background-color: #111111;");
        setMinHeight(220);
        setPrefHeight(330);
        getChildren().add(canvas);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanos == 0) {
                    lastNanos = now;
                }
                double dt = Math.min(0.1, (now - lastNanos) / 1_000_000_000.0);
                lastNanos = now;
                time += dt;
                step(dt);
                render();
            }
        };
        timer.start();
    }

    /** Da chiamare quando la vista viene chiusa, per fermare l'animazione. */
    public void stop() {
        timer.stop();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }

    // ================= aggiornamento stato =================

    public void update(SceneModel next) {
        SceneModel prev = model;
        if (prev != null) {
            if (next.playerHp() < prev.playerHp()) {
                heroShake = HIT_SHAKE_SECONDS;
                addFloat(HERO_X, 130, "-" + (prev.playerHp() - next.playerHp()), "#ff5555");
            } else if (next.playerHp() > prev.playerHp()) {
                addFloat(HERO_X, 130, "+" + (next.playerHp() - prev.playerHp()), "#66ff88");
            }
            if (prev.hasMonster() && next.hasMonster() && prev.monsterId().equals(next.monsterId())
                    && next.monsterHp() < prev.monsterHp()) {
                monsterShake = HIT_SHAKE_SECONDS;
                addFloat(MONSTER_X, 120, "-" + (prev.monsterHp() - next.monsterHp()), "#ffd24a");
            }
            if (prev.hasMonster() && !next.hasMonster() && !next.gameOver()) {
                fadingMonster = prev;
                fade = FADE_SECONDS;
            }
        }
        if (next.hasMonster() && (prev == null || !prev.hasMonster())) {
            entry = ENTRY_SECONDS;
            fadingMonster = null;
        }
        model = next;
    }

    private void addFloat(double x, double y, String text, String color) {
        floats.add(new FloatingText(x, y, text, color));
    }

    private void step(double dt) {
        heroShake = Math.max(0, heroShake - dt);
        monsterShake = Math.max(0, monsterShake - dt);
        entry = Math.max(0, entry - dt);
        fade = Math.max(0, fade - dt);
        for (Iterator<FloatingText> it = floats.iterator(); it.hasNext(); ) {
            FloatingText f = it.next();
            f.age += dt;
            f.y -= 35 * dt;
            if (f.age > FLOAT_SECONDS) {
                it.remove();
            }
        }
    }

    // ================= disegno =================

    private void render() {
        double w = getWidth();
        double h = getHeight();
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);
        if (model == null || w <= 0 || h <= 0) {
            return;
        }
        double scale = Math.min(w / Backdrop.W, h / Backdrop.H);
        g.save();
        g.translate((w - Backdrop.W * scale) / 2, (h - Backdrop.H * scale) / 2);
        g.scale(scale, scale);
        g.beginPath();
        g.rect(0, 0, Backdrop.W, Backdrop.H);
        g.clip();

        Backdrop.draw(g, model.theme(), time);
        drawHero(g);
        drawMonster(g);
        drawHud(g);
        drawFloats(g);
        g.restore();
    }

    private void drawHero(GraphicsContext g) {
        double x = HERO_X + shake(heroShake);
        if (model.gameOver()) {
            g.save();
            g.translate(HERO_X, GROUND);
            g.rotate(90);
            g.setGlobalAlpha(0.7);
            Sprites.drawHero(g, model.playerClass(), 0, 0, 0);
            g.restore();
        } else {
            Sprites.drawHero(g, model.playerClass(), x, GROUND, time);
        }
    }

    private void drawMonster(GraphicsContext g) {
        if (model.hasMonster()) {
            double slide = entry / ENTRY_SECONDS * 160;
            Sprites.drawMonster(g, model.monsterId(), model.boss(),
                    MONSTER_X + slide + shake(monsterShake), GROUND, time);
        } else if (fadingMonster != null && fade > 0) {
            g.setGlobalAlpha(fade / FADE_SECONDS);
            Sprites.drawMonster(g, fadingMonster.monsterId(), fadingMonster.boss(), MONSTER_X, GROUND, time);
            g.setGlobalAlpha(1);
        }
    }

    private void drawHud(GraphicsContext g) {
        Draw.text(g, "#ffffff", 14, model.playerName(), HERO_X, 46);
        bar(g, HERO_X, 52, 110, ratio(model.playerHp(), model.playerMaxHp()), "#c0392b");
        if (model.hasMonster()) {
            Draw.text(g, model.boss() ? "#ff8a80" : "#ffffff", 14,
                    model.monsterName() + (model.boss() ? " (BOSS)" : ""), MONSTER_X, 46);
            bar(g, MONSTER_X, 52, 130, ratio(model.monsterHp(), model.monsterMaxHp()), "#8e44ad");
        }
    }

    private void drawFloats(GraphicsContext g) {
        for (FloatingText f : floats) {
            g.setGlobalAlpha(Math.max(0, 1 - f.age / FLOAT_SECONDS));
            Draw.text(g, f.color, 26, f.text, f.x, f.y);
        }
        g.setGlobalAlpha(1);
    }

    private static void bar(GraphicsContext g, double cx, double y, double w, double ratio, String color) {
        Draw.rect(g, "#000000", cx - w / 2 - 1, y - 1, w + 2, 10);
        Draw.rect(g, "#444444", cx - w / 2, y, w, 8);
        Draw.rect(g, color, cx - w / 2, y, w * Math.max(0, Math.min(1, ratio)), 8);
    }

    private static double ratio(int value, int max) {
        return max <= 0 ? 0 : (double) value / max;
    }

    private static double shake(double remaining) {
        return remaining > 0 ? Math.sin(remaining * 60) * 5 : 0;
    }
}
