package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.economy.ShopConsumableRegistry;
import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;

// Handler for consumable purchase operations with delegation pattern
public class ConsumablePurchaseHandler {
    
    public boolean purchaseConsumable(PlayerData playerData, String itemId, int cost, 
                                    String successMessage, ShopUIHandler uiHandler) {
        if (playerData == null) return false;
        
        if (playerData.spendGold(cost)) {
            // Add item to inventory (IDs are snake_case)
            playerData.addConsumable(itemId, 1);
            uiHandler.showSuccessMessage(successMessage);
            
            // Save immediately after purchase
            PlayerDataManager.getInstance().savePlayerData(playerData);
            return true;
            
        } else {
            uiHandler.showErrorMessage("❌ Not enough gold! Need " + cost + " gold.");
            return false;
        }
    }
    
    /**
     * Retrieves consumable cost from ShopConsumableRegistry (single source of truth).
     * Delegates to registry instead of maintaining duplicate switch statement.
     */
    public int getConsumableCost(String itemId) {
        int price = ShopConsumableRegistry.getPrice(itemId);
        return price >= 0 ? price : 50; // Default fallback if not registered
    }
    
    public boolean canPurchaseConsumable(PlayerData playerData, String itemId) {
        if (playerData == null) return false;
        int cost = getConsumableCost(itemId);
        return playerData.getGold() >= cost;
    }
}