package com.amin.battlearena.domain.shop;

import java.util.HashMap;
import java.util.Map;

public final class ConsumableShop {
    private static final Map<String, Integer> CONSUMABLE_PRICES = new HashMap<>();
    
    static {
        CONSUMABLE_PRICES.put("HealthPotion", 50);
        CONSUMABLE_PRICES.put("ManaPotion", 30);
        CONSUMABLE_PRICES.put("HastePotion", 75);
    }
    
    public int getPrice(String consumableName) {
        return CONSUMABLE_PRICES.getOrDefault(consumableName, 0);
    }
    
    public boolean canAffordConsumable(int playerGold, String consumableName) {
        return playerGold >= getPrice(consumableName);
    }
    
    public Map<String, Integer> getAllPrices() {
        return new HashMap<>(CONSUMABLE_PRICES);
    }
}
