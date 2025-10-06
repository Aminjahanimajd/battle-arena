package com.amin.battlearena.uifx.util.effects;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;

/**
 * Strategy pattern implementation for highlight effects
 * Follows OOP principle of polymorphism and strategy design pattern
 */
public abstract class HighlightEffect {
    
    protected final Color color;
    protected final double radius;
    protected final double spread;
    
    protected HighlightEffect(Color color, double radius, double spread) {
        this.color = color;
        this.radius = radius;
        this.spread = spread;
    }
    
    /**
     * Template method pattern - defines the algorithm structure
     */
    public final void apply(Node target) {
        if (target == null) return;
        
        DropShadow effect = createEffect();
        target.setEffect(effect);
        
        Timeline animation = createAnimation(effect);
        if (animation != null) {
            animation.play();
            target.getProperties().put(getEffectKey(), animation);
        }
    }
    
    /**
     * Factory method pattern - subclasses define specific effect creation
     */
    protected DropShadow createEffect() {
        DropShadow effect = new DropShadow();
        effect.setBlurType(BlurType.GAUSSIAN);
        effect.setColor(color);
        effect.setRadius(radius);
        effect.setSpread(spread);
        
        // Add inner glow for enhanced effect
        if (shouldAddInnerGlow()) {
            Glow innerGlow = new Glow();
            innerGlow.setLevel(0.8);
            effect.setInput(innerGlow);
        }
        
        return effect;
    }
    
    /**
     * Template method - subclasses define animation behavior
     */
    protected abstract Timeline createAnimation(DropShadow effect);
    
    /**
     * Template method - subclasses define their unique key
     */
    protected abstract String getEffectKey();
    
    /**
     * Hook method - subclasses can override
     */
    protected boolean shouldAddInnerGlow() {
        return true;
    }
    
    /**
     * Removes the effect from target
     */
    public void remove(Node target) {
        if (target == null) return;
        
        Timeline animation = (Timeline) target.getProperties().get(getEffectKey());
        if (animation != null) {
            animation.stop();
            target.getProperties().remove(getEffectKey());
        }
        target.setEffect(null);
    }
}