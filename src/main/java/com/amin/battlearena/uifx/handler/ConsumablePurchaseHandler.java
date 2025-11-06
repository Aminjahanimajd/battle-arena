package com.amin.battlearena.uifx.handler;

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
    
    public int getConsumableCost(String itemId) {
        return switch (itemId) {
            case "health_potion" -> 25;
            case "mana_potion" -> 20;
            case "strength_elixir" -> 40;
            case "shield_scroll" -> 35;
            case "haste_potion" -> 30;
            case "revival_token" -> 100;
            default -> 50;
        };
    }
    
    public boolean canPurchaseConsumable(PlayerData playerData, String itemId) {
        if (playerData == null) return false;
        int cost = getConsumableCost(itemId);
        return playerData.getGold() >= cost;
    }
}