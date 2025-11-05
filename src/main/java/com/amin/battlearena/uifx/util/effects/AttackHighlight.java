package com.amin.battlearena.uifx.util.effects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// Concrete strategy for attack highlight effects (red pulsing)
public class AttackHighlight extends HighlightEffect {
    
    public AttackHighlight() {
        super(Color.DARKRED, 18, 0.4);
    }
    
    @Override
    protected Timeline createAnimation(DropShadow effect) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(effect.radiusProperty(), 12)),
            new KeyFrame(Duration.millis(300), new KeyValue(effect.radiusProperty(), 22)),
            new KeyFrame(Duration.millis(600), new KeyValue(effect.radiusProperty(), 12))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        return pulse;
    }
    
    @Override
    protected String getEffectKey() {
        return "attack_highlight";
    }
}