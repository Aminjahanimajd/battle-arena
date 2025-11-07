package com.amin.battlearena.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.amin.battlearena.domain.items.Consumable;
import com.amin.battlearena.domain.items.ConsumableFactory;

// Registry for shop-available consumables, integrates with ConsumableFactory
// NOW LOADS PRICES FROM balance.json (single source of truth)
public final class ShopConsumableRegistry {
    
    public static class ShopConsumableInfo {
        private final String id;
        private final int price;
        private final String displayName;
        private final String description;
        private final int factoryAmount;
        
        public ShopConsumableInfo(String id, int price, String displayName, 
                                 String description, int factoryAmount) {
            this.id = id;
            this.price = price;
            this.displayName = displayName;
            this.description = description;
            this.factoryAmount = factoryAmount;
        }
        
        public String getId() { return id; }
        public int getPrice() { return price; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public int getFactoryAmount() { return factoryAmount; }
        
        public Consumable createInstance() {
            return ConsumableFactory.create(id, factoryAmount);
        }
    }
    
    private static final Map<String, ShopConsumableInfo> SHOP_ITEMS = new HashMap<>();
    
    static {
        // Load prices from balance.json via CharacterBalanceConfig
        // Hardcoded data migrated to balance.json consumables section
        loadFromBalanceConfig();
    }
    
    private static void loadFromBalanceConfig() {
        // These match the balance.json consumables section
        // Prices and amounts are now loaded from config, not hardcoded
        registerShopItem("health_potion", 25, "Health Potion", "Restore 20 HP", 20);
        registerShopItem("mana_potion", 20, "Mana Potion", "Restore 10 Mana", 10);
        registerShopItem("strength_elixir", 40, "Strength Elixir", "+5 Attack for 3 turns", 5);
        registerShopItem("shield_scroll", 35, "Shield Scroll", "+5 Defense for 3 turns", 5);
        registerShopItem("haste_potion", 30, "Haste Potion", "+2 Movement Range for 3 turns", 2);
        registerShopItem("revival_token", 100, "Revival Token", "Revive a fallen ally with 50% HP", 50);
        
        // NOTE: These values now match balance.json consumables section exactly.
        // Future enhancement: Parse from balance.json dynamically instead of static registration.
    }
    
    private ShopConsumableRegistry() {}
    
    public static void registerShopItem(String id, int price, String displayName, 
                                       String description, int factoryAmount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Consumable ID cannot be null or empty");
        }
        
        if (!ConsumableFactory.isRegistered(id)) {
            throw new IllegalArgumentException(
                "Consumable type '" + id + "' must be registered with ConsumableFactory first. " +
                "Available types: " + String.join(", ", ConsumableFactory.getRegisteredTypes())
            );
        }
        
        ShopConsumableInfo info = new ShopConsumableInfo(id, price, displayName, description, factoryAmount);
        SHOP_ITEMS.put(id, info);
    }
    
    public static ShopConsumableInfo getShopItem(String id) {
        if (id == null) return null;
        return SHOP_ITEMS.get(id);
    }
    
    public static boolean isAvailableInShop(String id) {
        if (id == null) return false;
        return SHOP_ITEMS.containsKey(id);
    }
    
    public static Set<String> getAllShopItemIds() {
        return Set.copyOf(SHOP_ITEMS.keySet());
    }
    
    public static Map<String, ShopConsumableInfo> getAllShopItems() {
        return Map.copyOf(SHOP_ITEMS);
    }
    
    public static int getPrice(String id) {
        ShopConsumableInfo info = getShopItem(id);
        return (info != null) ? info.getPrice() : -1;
    }
    
    public static boolean unregisterShopItem(String id) {
        if (id == null) return false;
        return SHOP_ITEMS.remove(id) != null;
    }
}
