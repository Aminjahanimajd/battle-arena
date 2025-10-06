package com.amin.battlearena.uifx.util.effects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Concrete strategy for movement highlight effects
 * Uses blue gentle pulsing animation
 */
public class MovementHighlight extends HighlightEffect {
    
    public MovementHighlight() {
        super(Color.DODGERBLUE, 16, 0.3);
    }
    
    @Override
    protected Timeline createAnimation(DropShadow effect) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(effect.radiusProperty(), 10)),
            new KeyFrame(Duration.millis(800), new KeyValue(effect.radiusProperty(), 20)),
            new KeyFrame(Duration.millis(1600), new KeyValue(effect.radiusProperty(), 10))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        return pulse;
    }
    
    @Override
    protected String getEffectKey() {
        return "movement_highlight";
    }
}