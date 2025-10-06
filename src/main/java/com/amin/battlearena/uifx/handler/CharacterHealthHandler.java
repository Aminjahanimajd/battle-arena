package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.domain.model.Character;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Handler responsible for managing character health visualization
 * Includes health bar updates and visual feedback for wounded characters
 * Follows Single Responsibility Principle
 */
public class CharacterHealthHandler {
    
    /**
     * Update character visual based on health status
     */
    public void updateCharacterHealth(StackPane characterNode, Character character) {
        // Find health bar and update
        characterNode.getChildren().stream()
            .filter(node -> node instanceof VBox)
            .map(node -> (VBox) node)
            .forEach(vbox -> {
                vbox.getChildren().stream()
                    .filter(child -> child instanceof ProgressBar)
                    .map(child -> (ProgressBar) child)
                    .forEach(healthBar -> {
                        double healthPercent = (double) character.getStats().getHp() / character.getStats().getMaxHp();
                        healthBar.setProgress(healthPercent);
                        
                        // Change color based on health
                        healthBar.getStyleClass().removeAll("health-low", "health-medium", "health-high");
                        if (healthPercent < 0.25) {
                            healthBar.getStyleClass().add("health-low");
                        } else if (healthPercent < 0.60) {
                            healthBar.getStyleClass().add("health-medium");
                        } else {
                            healthBar.getStyleClass().add("health-high");
                        }
                    });
            });
        
        // Visual feedback for low health
        if (character.getStats().getHp() <= character.getStats().getMaxHp() * 0.2) {
            // Add wounded effect
            characterNode.getStyleClass().add("wounded");
        } else {
            characterNode.getStyleClass().remove("wounded");
        }
    }
}