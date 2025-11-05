package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;

// Handler for upgrade purchase operations with delegation pattern
public class UpgradePurchaseHandler {
    
    public boolean purchaseUpgrade(PlayerData playerData, String upgradeName, String successMessage, 
                                 ShopUIHandler uiHandler) {
        if (playerData == null) return false;
        
        int currentLevel = playerData.getUpgradeLevel(upgradeName);
        int maxLevel = getMaxUpgradeLevel(upgradeName);
        
        if (currentLevel >= maxLevel) {
            uiHandler.showErrorMessage("❌ " + upgradeName + " is already at maximum level!");
            return false;
        }
        
        int actualCost = calculateUpgradeCost(upgradeName, currentLevel);
        
        if (playerData.spendGold(actualCost)) {
            // Increase upgrade level
            playerData.setUpgradeLevel(upgradeName, currentLevel + 1);
            uiHandler.showSuccessMessage(successMessage);
            
            // Save immediately after purchase
            PlayerDataManager.getInstance().savePlayerData(playerData);
            return true;
            
        } else {
            uiHandler.showErrorMessage("❌ Not enough gold! Need " + actualCost + " gold.");
            return false;
        }
    }
    
    public int calculateUpgradeCost(String upgradeName, int currentLevel) {
        int baseCost = getBaseUpgradeCost(upgradeName);
        return (int) (baseCost * Math.pow(1.5, currentLevel));
    }
    
    private int getBaseUpgradeCost(String upgradeName) {
        return switch (upgradeName) {
            case "Health Boost" -> 150;
            case "Attack Power" -> 200;
            case "Armor Boost" -> 175;
            case "Eagle Eye" -> 220;
            case "Swift Steps" -> 250;
            case "Precision Shot" -> 300;
            case "Mana Pool" -> 180;
            case "Spell Power" -> 280;
            case "Quick Cast" -> 320;
            default -> 100;
        };
    }
    
    public int getMaxUpgradeLevel(String upgradeName) {
        return switch (upgradeName) {
            case "Health Boost", "Attack Power", "Armor Boost" -> 10; // Core stats can go higher
            case "Eagle Eye", "Swift Steps", "Precision Shot", 
                 "Mana Pool", "Spell Power", "Quick Cast" -> 8; // Special abilities
            default -> 5; // Default max level
        };
    }
    
    public boolean canPurchaseUpgrade(PlayerData playerData, String upgradeName) {
        if (playerData == null) return false;
        
        int currentLevel = playerData.getUpgradeLevel(upgradeName);
        int maxLevel = getMaxUpgradeLevel(upgradeName);
        int cost = calculateUpgradeCost(upgradeName, currentLevel);
        
        return currentLevel < maxLevel && playerData.getGold() >= cost;
    }
}