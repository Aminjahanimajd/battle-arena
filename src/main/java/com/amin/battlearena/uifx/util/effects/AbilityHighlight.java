package com.amin.battlearena.uifx.util.effects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// Concrete strategy for ability highlight effects (purple pulsing)
public class AbilityHighlight extends HighlightEffect {
    
    public AbilityHighlight() {
        super(Color.PURPLE, 20, 0.3);
    }
    
    @Override
    protected Timeline createAnimation(DropShadow effect) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(effect.radiusProperty(), 15)),
            new KeyFrame(Duration.millis(500), new KeyValue(effect.radiusProperty(), 25)),
            new KeyFrame(Duration.millis(1000), new KeyValue(effect.radiusProperty(), 15))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        return pulse;
    }
    
    @Override
    protected String getEffectKey() {
        return "ability_highlight";
    }
}