package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.economy.UpgradeCatalog;
import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;

// Handler for upgrade purchase operations with delegation pattern
// NOW USES UpgradeCatalog as single source of truth for all pricing
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
        
        // Use UpgradeCatalog for all price calculations
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
    
    // Delegate to UpgradeCatalog for cost calculation
    public int calculateUpgradeCost(String upgradeName, int currentLevel) {
        return UpgradeCatalog.calculateUpgradeCost(upgradeName, currentLevel);
    }
    
    // Delegate to UpgradeCatalog for max level
    public int getMaxUpgradeLevel(String upgradeName) {
        return UpgradeCatalog.getMaxUpgradeLevel(upgradeName);
    }
    
    public boolean canPurchaseUpgrade(PlayerData playerData, String upgradeName) {
        if (playerData == null) return false;
        
        int currentLevel = playerData.getUpgradeLevel(upgradeName);
        int maxLevel = getMaxUpgradeLevel(upgradeName);
        int cost = calculateUpgradeCost(upgradeName, currentLevel);
        
        return currentLevel < maxLevel && playerData.getGold() >= cost;
    }
}