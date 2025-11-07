package com.amin.battlearena.uifx.util;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.uifx.handler.CharacterDesignHandler;
import com.amin.battlearena.uifx.handler.CharacterHealthHandler;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

// CharacterRenderer creates visual representations with delegation pattern
public class CharacterRenderer {
    
    private static final CharacterDesignHandler designHandler = new CharacterDesignHandler();
    private static final CharacterHealthHandler healthHandler = new CharacterHealthHandler();
    
    private VBox createInfoOverlay(Character character) {
        VBox info = new VBox(2);
        info.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label(character.getName());
        nameLabel.getStyleClass().add("character-name");
        
        ProgressBar healthBar = new ProgressBar();
        healthBar.getStyleClass().add("health-bar");
        healthBar.setPrefWidth(40);
        healthBar.setPrefHeight(4);
        healthBar.setProgress(character.getStats().getHealthPercentage());
        
        ProgressBar manaBar = new ProgressBar();
        manaBar.getStyleClass().add("mana-bar");
        manaBar.setPrefWidth(40);
        manaBar.setPrefHeight(4);
        manaBar.setProgress(character.getMaxMana() > 0 ? (double) character.getCurrentMana() / character.getMaxMana() : 0);
        
        info.getChildren().addAll(nameLabel, healthBar, manaBar);
        return info;
    }

    
    public static void updateCharacterHealth(StackPane characterNode, Character character) {
        // Delegate health visualization to specialized handler
        healthHandler.updateCharacterHealth(characterNode, character);
    }
    
    public static void showActionAnimation(StackPane characterNode, String actionType) {
        String key = actionType == null ? "" : actionType.toLowerCase(java.util.Locale.ROOT);
        if ("attack".equals(key)) {
            // Quick forward thrust
            TranslateTransition attackMove = new TranslateTransition(Duration.millis(200), characterNode);
            attackMove.setByX(10);
            attackMove.setAutoReverse(true);
            attackMove.setCycleCount(2);
            attackMove.play();
            
            // Audio removed for simplified architecture
        } else if ("ability".equals(key)) {
            // Magical glow and scale
            ScaleTransition abilityScale = new ScaleTransition(Duration.millis(300), characterNode);
            abilityScale.setFromX(1.0);
            abilityScale.setFromY(1.0);
            abilityScale.setToX(1.2);
            abilityScale.setToY(1.2);
            abilityScale.setAutoReverse(true);
            abilityScale.setCycleCount(2);
            abilityScale.play();
            
            // Play magic sound
            // Audio removed for simplified architecture
        } else if ("move".equals(key)) {
            // Smooth glide effect
            ScaleTransition moveScale = new ScaleTransition(Duration.millis(150), characterNode);
            moveScale.setFromX(1.0);
            moveScale.setFromY(1.0);
            moveScale.setToX(1.05);
            moveScale.setToY(1.05);
            moveScale.setAutoReverse(true);
            moveScale.setCycleCount(2);
            moveScale.play();
            
            // Play movement sound
            // Audio removed for simplified architecture
        } else if ("death".equals(key)) {
            // Death animation handled by VisualEffectsManager
            VisualEffectsManager.getInstance().showDeathEffect(characterNode);
            // Audio removed for simplified architecture
        }
        // Default: No-op for unknown action types
    }
}
