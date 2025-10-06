package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.persistence.PlayerData;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Handler for shop UI operations with delegation pattern
 */
public class ShopUIHandler {
    
    private Label playerGold;
    private Label statusMessage;
    
    public ShopUIHandler(Label playerGold, Label statusMessage) {
        this.playerGold = playerGold;
        this.statusMessage = statusMessage;
    }
    
    /**
     * Update gold display with animation
     */
    public void updateGoldDisplay(PlayerData playerData) {
        if (playerData != null && playerGold != null) {
            playerGold.setText(String.valueOf(playerData.getGold()));
            
            // Add coin sparkle animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), playerGold);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
        }
    }
    
    /**
     * Show success message with animation
     */
    public void showSuccessMessage(String message) {
        if (statusMessage != null) {
            statusMessage.setText(message);
            statusMessage.getStyleClass().removeAll("error-message");
            statusMessage.getStyleClass().add("success-message");
            
            // Flash animation
            FadeTransition flash = new FadeTransition(Duration.millis(100), statusMessage);
            flash.setFromValue(0.3);
            flash.setToValue(1.0);
            flash.setCycleCount(3);
            flash.setAutoReverse(true);
            flash.play();
        }
    }
    
    /**
     * Show error message with animation
     */
    public void showErrorMessage(String message) {
        if (statusMessage != null) {
            statusMessage.setText(message);
            statusMessage.getStyleClass().removeAll("success-message");
            statusMessage.getStyleClass().add("error-message");
            
            // Shake animation
            ScaleTransition shake = new ScaleTransition(Duration.millis(50), statusMessage);
            shake.setFromX(1.0);
            shake.setToX(1.05);
            shake.setCycleCount(6);
            shake.setAutoReverse(true);
            shake.play();
        }
    }
    
    /**
     * Update upgrade button state and text based on affordability
     */
    public void updateUpgradeButton(String upgradeName, Button button, PlayerData playerData, 
                                  UpgradePurchaseHandler upgradeHandler) {
        if (button == null || playerData == null) return;
        
        int currentLevel = playerData.getUpgradeLevel(upgradeName);
        int maxLevel = upgradeHandler.getMaxUpgradeLevel(upgradeName);
        int cost = upgradeHandler.calculateUpgradeCost(upgradeName, currentLevel);
        boolean canAfford = playerData.getGold() >= cost;
        
        if (currentLevel >= maxLevel) {
            button.setText("MAX");
            button.setDisable(true);
        } else {
            button.setText("Buy (" + cost + " gold)");
            button.setDisable(!canAfford);
        }
    }
    
    /**
     * Update consumable button state based on affordability
     */
    public void updateConsumableButton(Button button, int cost, PlayerData playerData) {
        if (button != null && playerData != null) {
            button.setDisable(playerData.getGold() < cost);
        }
    }
}