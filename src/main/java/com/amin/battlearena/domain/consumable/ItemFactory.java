package com.amin.battlearena.domain.consumable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemFactory {
    private static final Map<String, Supplier<Consumable>> registry = new HashMap<>();

    static {
        registry.put("Health Potion", () -> new HealthPotion(50));
        registry.put("Mana Potion", () -> new ManaPotion(30));
        registry.put("Haste Potion", () -> new HastePotion(2));
    }

    public static Consumable createItem(String itemName) {
        Supplier<Consumable> supplier = registry.get(itemName);
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }
}
