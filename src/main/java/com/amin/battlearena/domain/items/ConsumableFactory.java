package com.amin.battlearena.domain.items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Factory for creating consumables with runtime registration and shop integration
public final class ConsumableFactory {
    
    private static final Map<String, Function<Integer, Consumable>> REGISTRY = new HashMap<>();
    
    static {
        registerType("health_potion", HealthPotion::new);
        registerType("mana_potion", ManaPotion::new);
        registerType("hp_potion", HealthPotion::new);
        registerType("mp_potion", ManaPotion::new);
        registerType("healing_potion", HealthPotion::new);
    }
    
    private ConsumableFactory() {}
    
    public static void registerType(String type, Function<Integer, Consumable> creator) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Consumable type cannot be null or empty");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Consumable creator function cannot be null");
        }
        REGISTRY.put(type.trim().toLowerCase(), creator);
    }
    
    // Register consumable and automatically add to shop
    public static void registerTypeWithShop(String type, Function<Integer, Consumable> creator,
                                           int shopPrice, String shopDisplayName, 
                                           String shopDescription, int defaultAmount) {
        registerType(type, creator);
        
        // Soft dependency on ShopConsumableRegistry via reflection
        try {
            Class<?> shopRegistry = Class.forName("com.amin.battlearena.economy.ShopConsumableRegistry");
            java.lang.reflect.Method registerMethod = shopRegistry.getMethod(
                "registerShopItem", String.class, int.class, String.class, String.class, int.class
            );
            registerMethod.invoke(null, type, shopPrice, shopDisplayName, shopDescription, defaultAmount);
        } catch (Exception e) {
            // Shop not available - consumable still registered with factory
        }
    }
    
    public static boolean isRegistered(String type) {
        if (type == null) return false;
        return REGISTRY.containsKey(type.trim().toLowerCase());
    }
    
    public static Consumable create(String type, int amount) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Consumable type cannot be null or empty");
        }
        
        String normalizedType = type.trim().toLowerCase();
        Function<Integer, Consumable> creator = REGISTRY.get(normalizedType);
        
        if (creator == null) {
            throw new IllegalArgumentException(
                "Unknown consumable type: " + type + ". Available types: " + REGISTRY.keySet()
            );
        }
        
        return creator.apply(amount);
    }
    
    public static Consumable createHealthPotion(int healAmount) {
        return new HealthPotion(healAmount);
    }
    
    public static Consumable createManaPotion(int manaAmount) {
        return new ManaPotion(manaAmount);
    }
    
    public static String[] getRegisteredTypes() {
        return REGISTRY.keySet().toArray(String[]::new);
    }
    
    public static boolean unregisterType(String type) {
        if (type == null) return false;
        return REGISTRY.remove(type.trim().toLowerCase()) != null;
    }
}
