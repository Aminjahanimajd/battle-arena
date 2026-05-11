package com.amin.battlearena.domain.consumable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ConsumableFactory {
    private static final Map<String, Supplier<Consumable>> ITEMS = new HashMap<>();
    
    static {
        ITEMS.put("HealthPotion", () -> new HealthPotion(50));
        ITEMS.put("ManaPotion", () -> new ManaPotion(30));
        ITEMS.put("HastePotion", () -> new HastePotion(2));
    }
    
    private ConsumableFactory() {}
    
    public static Consumable createItem(String itemName) {
        Supplier<Consumable> supplier = ITEMS.get(itemName);
        if (supplier != null) {
            return supplier.get();
        }
        throw new IllegalArgumentException("Unknown item: " + itemName);
    }
}
