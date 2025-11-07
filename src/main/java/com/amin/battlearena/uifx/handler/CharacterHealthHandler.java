package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.domain.model.Character;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// Handler responsible for managing character health visualization
public class CharacterHealthHandler {
    
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
                        double healthPercent = character.getStats().getHealthPercentage();
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
        if (character.isLowHealth()) {
            // Add wounded effect
            characterNode.getStyleClass().add("wounded");
        } else {
            characterNode.getStyleClass().remove("wounded");
        }
    }
}