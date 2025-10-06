package com.amin.battlearena.uifx.rendering;

import com.amin.battlearena.uifx.util.VisualEffectsManager;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * CharacterAnimator handles action animations for character nodes.
 * 
 * Responsibilities:
 * - Attack animations (thrust movements)
 * - Ability animations (scaling, glowing effects)
 * - Movement animations (smooth gliding)
 * - Death animations (delegated to VisualEffectsManager)
 */
public class CharacterAnimator {
    
    /**
     * Show character action animation based on action type
     */
    public static void showActionAnimation(StackPane characterNode, String actionType) {
        if (actionType == null) {
            return;
        }
        
        String key = actionType.toLowerCase(java.util.Locale.ROOT);
        if ("attack".equals(key)) {
            playAttackAnimation(characterNode);
        } else if ("ability".equals(key)) {
            playAbilityAnimation(characterNode);
        } else if ("move".equals(key)) {
            playMoveAnimation(characterNode);
        } else if ("death".equals(key)) {
            playDeathAnimation(characterNode);
        }
        // Default: No-op for unknown action types
    }
    
    private static void playAttackAnimation(StackPane characterNode) {
        // Quick forward thrust
        TranslateTransition attackMove = new TranslateTransition(Duration.millis(200), characterNode);
        attackMove.setByX(10);
        attackMove.setAutoReverse(true);
        attackMove.setCycleCount(2);
        attackMove.play();
        
        // Audio removed for simplified architecture
    }
    
    private static void playAbilityAnimation(StackPane characterNode) {
        // Magical glow and scale
        ScaleTransition abilityScale = new ScaleTransition(Duration.millis(300), characterNode);
        abilityScale.setFromX(1.0);
        abilityScale.setFromY(1.0);
        abilityScale.setToX(1.2);
        abilityScale.setToY(1.2);
        abilityScale.setAutoReverse(true);
        abilityScale.setCycleCount(2);
        abilityScale.play();
        
        // Audio removed for simplified architecture
    }
    
    private static void playMoveAnimation(StackPane characterNode) {
        // Smooth glide effect
        ScaleTransition moveScale = new ScaleTransition(Duration.millis(150), characterNode);
        moveScale.setFromX(1.0);
        moveScale.setFromY(1.0);
        moveScale.setToX(1.05);
        moveScale.setToY(1.05);
        moveScale.setAutoReverse(true);
        moveScale.setCycleCount(2);
        moveScale.play();
        
        // Audio removed for simplified architecture
    }
    
    private static void playDeathAnimation(StackPane characterNode) {
        // Death animation handled by VisualEffectsManager
        VisualEffectsManager.getInstance().showDeathEffect(characterNode);
        
        // Audio removed for simplified architecture
    }
}