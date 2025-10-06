package com.amin.battlearena.uifx.util.effects;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Handles floating number animations using delegation pattern
 * Separates number animation concerns from main effects manager
 */
public class NumberAnimationHandler {
    
    /**
     * Show damage number with floating animation
     */
    public void showDamageNumber(Pane parent, double x, double y, int damage, boolean isCritical) {
        if (parent == null) return;
        
        Label damageLabel = createDamageLabel(damage, isCritical);
        positionLabel(damageLabel, x, y);
        
        parent.getChildren().add(damageLabel);
        animateFloatingNumber(parent, damageLabel, -50, 1000);
    }
    
    /**
     * Show healing number with floating animation
     */
    public void showHealingNumber(Pane parent, double x, double y, int healing) {
        if (parent == null) return;
        
        Label healLabel = createHealingLabel(healing);
        positionLabel(healLabel, x, y);
        
        parent.getChildren().add(healLabel);
        animateFloatingNumber(parent, healLabel, -40, 800);
    }
    
    /**
     * Factory method for damage labels
     */
    private Label createDamageLabel(int damage, boolean isCritical) {
        Label label = new Label("-" + damage);
        label.setFont(Font.font("System", FontWeight.BOLD, isCritical ? 18 : 14));
        label.setTextFill(isCritical ? Color.YELLOW : Color.RED);
        
        if (isCritical) {
            label.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.ORANGE, 5, 0.8, 0, 0));
        }
        
        return label;
    }
    
    /**
     * Factory method for healing labels
     */
    private Label createHealingLabel(int healing) {
        Label label = new Label("+" + healing);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setTextFill(Color.LIGHTGREEN);
        label.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.GREEN, 3, 0.6, 0, 0));
        
        return label;
    }
    
    /**
     * Utility method for positioning labels
     */
    private void positionLabel(Label label, double x, double y) {
        label.setLayoutX(x);
        label.setLayoutY(y);
    }
    
    /**
     * Template method for floating number animation
     */
    private void animateFloatingNumber(Pane parent, Label label, double floatDistance, int durationMs) {
        // Animation: float up and fade out
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(durationMs), label);
        floatUp.setByY(floatDistance);
        
        FadeTransition fade = new FadeTransition(Duration.millis(durationMs), label);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        ParallelTransition combo = new ParallelTransition(floatUp, fade);
        combo.setOnFinished(e -> parent.getChildren().remove(label));
        combo.play();
    }
}