package com.amin.battlearena.uifx.util.effects;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Handles character-specific animations using delegation pattern
 * Separates animation concerns from main VisualEffectsManager
 */
public class CharacterAnimationHandler {
    
    /**
     * Hit effect with screen shake and red flash
     */
    public void performHitAnimation(Node character) {
        if (character == null) return;
        
        // Screen shake effect
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), character);
        shake.setFromX(0);
        shake.setToX(5);
        shake.setFromY(0);
        shake.setToY(2);
        shake.setAutoReverse(true);
        shake.setCycleCount(4);
        shake.play();
        
        // Red flash effect
        DropShadow hitFlash = new DropShadow();
        hitFlash.setColor(Color.RED);
        hitFlash.setRadius(15);
        hitFlash.setSpread(0.6);
        
        character.setEffect(hitFlash);
        
        // Remove effect after flash
        PauseTransition delay = new PauseTransition(Duration.millis(150));
        delay.setOnFinished(e -> character.setEffect(null));
        delay.play();
    }
    
    /**
     * Death animation with fade and scale
     */
    public void performDeathAnimation(Node character) {
        if (character == null) return;
        
        // Dramatic fade to gray with scale down
        FadeTransition fade = new FadeTransition(Duration.millis(800), character);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), character);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.8);
        scale.setToY(0.8);
        
        // Red death glow
        DropShadow deathGlow = new DropShadow();
        deathGlow.setColor(Color.DARKRED);
        deathGlow.setRadius(25);
        deathGlow.setSpread(0.5);
        
        character.setEffect(deathGlow);
        
        ParallelTransition deathAnimation = new ParallelTransition(fade, scale);
        deathAnimation.play();
    }
    
    /**
     * Ability casting animation with color-coded effects
     */
    public void performCastAnimation(Node caster, String abilityType) {
        if (caster == null) return;
        
        Color effectColor = determineAbilityColor(abilityType);
        
        DropShadow castGlow = new DropShadow();
        castGlow.setColor(effectColor);
        castGlow.setRadius(20);
        castGlow.setSpread(0.7);
        
        caster.setEffect(castGlow);
        
        // Scale up briefly
        ScaleTransition castScale = new ScaleTransition(Duration.millis(200), caster);
        castScale.setFromX(1.0);
        castScale.setFromY(1.0);
        castScale.setToX(1.15);
        castScale.setToY(1.15);
        castScale.setAutoReverse(true);
        castScale.setCycleCount(2);
        castScale.setOnFinished(e -> caster.setEffect(null));
        castScale.play();
    }
    
    /**
     * Level up celebration animation
     */
    public void performLevelUpAnimation(Node character) {
        if (character == null) return;
        
        // Golden sparkles effect
        DropShadow levelGlow = new DropShadow();
        levelGlow.setColor(Color.GOLD);
        levelGlow.setRadius(30);
        levelGlow.setSpread(0.8);
        
        character.setEffect(levelGlow);
        
        // Celebration scale
        ScaleTransition celebrate = new ScaleTransition(Duration.millis(400), character);
        celebrate.setFromX(1.0);
        celebrate.setFromY(1.0);
        celebrate.setToX(1.3);
        celebrate.setToY(1.3);
        celebrate.setAutoReverse(true);
        celebrate.setCycleCount(2);
        
        // Spin effect
        RotateTransition spin = new RotateTransition(Duration.millis(800), character);
        spin.setByAngle(360);
        
        ParallelTransition levelUpAnimation = new ParallelTransition(celebrate, spin);
        levelUpAnimation.setOnFinished(e -> character.setEffect(null));
        levelUpAnimation.play();
    }
    
    /**
     * Selection highlight effect
     */
    public void performSelectionAnimation(Node target) {
        if (target == null) return;
        
        DropShadow selection = new DropShadow();
        selection.setColor(Color.GOLD);
        selection.setRadius(12);
        selection.setSpread(0.4);
        
        target.setEffect(selection);
        target.getProperties().put("selection_highlight", selection);
    }
    
    /**
     * Clear selection highlight
     */
    public void clearSelectionAnimation(Node target) {
        if (target == null) return;
        
        if (target.getProperties().containsKey("selection_highlight")) {
            target.setEffect(null);
            target.getProperties().remove("selection_highlight");
        }
    }
    
    /**
     * Factory method for ability color determination
     * Uses strategy pattern for color mapping
     */
    private Color determineAbilityColor(String abilityType) {
        if (abilityType == null) return Color.WHITE;
        
        String key = abilityType.toLowerCase();
        if (key.contains("fire") || key.contains("power")) return Color.ORANGE;
        if (key.contains("ice") || key.contains("frost")) return Color.LIGHTBLUE;
        if (key.contains("lightning") || key.contains("electric")) return Color.YELLOW;
        if (key.contains("arcane") || key.contains("magic")) return Color.PURPLE;
        return Color.WHITE;
    }
}