package com.amin.battlearena.uifx.util;

import com.amin.battlearena.uifx.util.effects.AbilityHighlight;
import com.amin.battlearena.uifx.util.effects.AttackHighlight;
import com.amin.battlearena.uifx.util.effects.HighlightEffect;
import com.amin.battlearena.uifx.util.effects.MovementHighlight;
import com.amin.battlearena.uifx.handler.CharacterAnimationHandler;
import com.amin.battlearena.uifx.handler.NumberAnimationHandler;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Refactored Visual Effects Manager using delegation and strategy patterns
 * Follows OOP principles: Single Responsibility, Delegation, Strategy Pattern
 */
public class VisualEffectsManager {
    
    private static volatile VisualEffectsManager instance;
    
    // Delegation pattern: Specialized handlers for different effect types
    private final CharacterAnimationHandler characterAnimations;
    private final NumberAnimationHandler numberAnimations;
    private final HighlightEffect abilityHighlight;
    private final HighlightEffect attackHighlight;
    private final HighlightEffect movementHighlight;
    
    private VisualEffectsManager() {
        // Initialize delegated handlers (Dependency Injection pattern)
        this.characterAnimations = new CharacterAnimationHandler();
        this.numberAnimations = new NumberAnimationHandler();
        this.abilityHighlight = new AbilityHighlight();
        this.attackHighlight = new AttackHighlight();
        this.movementHighlight = new MovementHighlight();
    }
    
    public static VisualEffectsManager getInstance() {
        VisualEffectsManager result = instance;
        if (result == null) {
            synchronized (VisualEffectsManager.class) {
                result = instance;
                if (result == null) {
                    result = instance = new VisualEffectsManager();
                }
            }
        }
        return result;
    }
    
    /**
     * Create purple ability highlight effect using delegation
     */
    public void showAbilityHighlight(Node target) {
        Platform.runLater(() -> abilityHighlight.apply(target));
    }
    
    /**
     * Create red attack highlight effect using delegation
     */
    public void showAttackHighlight(Node target) {
        Platform.runLater(() -> attackHighlight.apply(target));
    }
    
    /**
     * Create blue movement highlight effect using delegation
     */
    public void showMovementHighlight(Node target) {
        Platform.runLater(() -> movementHighlight.apply(target));
    }
    
    /**
     * Remove all highlights from target using delegation
     */
    public void clearHighlights(Node target) {
        Platform.runLater(() -> {
            abilityHighlight.remove(target);
            attackHighlight.remove(target);
            movementHighlight.remove(target);
        });
    }
    
    /**
     * Show damage number animation using delegation
     */
    public void showDamageNumber(Pane parent, double x, double y, int damage, boolean isCritical) {
        Platform.runLater(() -> numberAnimations.showDamageNumber(parent, x, y, damage, isCritical));
    }
    
    /**
     * Show healing number animation using delegation
     */
    public void showHealingNumber(Pane parent, double x, double y, int healing) {
        Platform.runLater(() -> numberAnimations.showHealingNumber(parent, x, y, healing));
    }
    
    /**
     * Show ability range indicator
     */
    public void showAbilityRange(Pane parent, double centerX, double centerY, int range) {
        Platform.runLater(() -> {
            Circle rangeIndicator = new Circle(centerX, centerY, range * 40); // Scale up for visibility
            rangeIndicator.setFill(Color.rgb(128, 0, 128, 0.2)); // Transparent purple
            rangeIndicator.setStroke(Color.PURPLE);
            rangeIndicator.setStrokeWidth(2);
            rangeIndicator.getStyleClass().add("range-indicator");
            
            parent.getChildren().add(rangeIndicator);
            
            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rangeIndicator);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            // Store for cleanup
            parent.getProperties().put("range_indicator", rangeIndicator);
        });
    }
    
    /**
     * Show ability effect area
     */
    public void showAbilityEffectArea(Pane parent, double centerX, double centerY, int width, int height) {
        Platform.runLater(() -> {
            Rectangle effectArea = new Rectangle(centerX - (width / 2.0), centerY - (height / 2.0), width * 40.0, height * 40.0);
            effectArea.setFill(Color.rgb(255, 165, 0, 0.3)); // Transparent orange
            effectArea.setStroke(Color.ORANGE);
            effectArea.setStrokeWidth(2);
            effectArea.getStyleClass().add("effect-area");
            
            parent.getChildren().add(effectArea);
            
            // Pulsing effect
            ScaleTransition pulse = new ScaleTransition(Duration.millis(500), effectArea);
            pulse.setFromX(0.8);
            pulse.setFromY(0.8);
            pulse.setToX(1.1);
            pulse.setToY(1.1);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), effectArea);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            
            ParallelTransition combo = new ParallelTransition(pulse, fade);
            combo.play();
            
            parent.getProperties().put("effect_area", effectArea);
        });
    }
    
    /**
     * Clear all range and effect indicators
     */
    public void clearRangeIndicators(Pane parent) {
        Platform.runLater(() -> {
            Node rangeIndicator = (Node) parent.getProperties().get("range_indicator");
            Node effectArea = (Node) parent.getProperties().get("effect_area");
            
            if (rangeIndicator != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), rangeIndicator);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    parent.getChildren().remove(rangeIndicator);
                    parent.getProperties().remove("range_indicator");
                });
                fadeOut.play();
            }
            
            if (effectArea != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), effectArea);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    parent.getChildren().remove(effectArea);
                    parent.getProperties().remove("effect_area");
                });
                fadeOut.play();
            }
        });
    }
    
    /**
     * Character hit effect using delegation
     */
    public void showHitEffect(Node character) {
        Platform.runLater(() -> characterAnimations.performHitAnimation(character));
    }
    
    /**
     * Character death effect using delegation
     */
    public void showDeathEffect(Node character) {
        Platform.runLater(() -> characterAnimations.performDeathAnimation(character));
    }
    
    /**
     * Ability cast effect using delegation
     */
    public void showAbilityCastEffect(Node caster, String abilityType) {
        Platform.runLater(() -> characterAnimations.performCastAnimation(caster, abilityType));
    }
    
    /**
     * Selection highlight effect using delegation
     */
    public void showSelectionHighlight(Node target) {
        Platform.runLater(() -> characterAnimations.performSelectionAnimation(target));
    }
    
    /**
     * Clear selection highlight using delegation
     */
    public void clearSelectionHighlight(Node target) {
        Platform.runLater(() -> characterAnimations.clearSelectionAnimation(target));
    }
    
    /**
     * Level up effect using delegation
     */
    public void showLevelUpEffect(Node character) {
        Platform.runLater(() -> characterAnimations.performLevelUpAnimation(character));
    }
}
