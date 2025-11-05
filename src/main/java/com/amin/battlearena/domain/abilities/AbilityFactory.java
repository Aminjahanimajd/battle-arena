package com.amin.battlearena.domain.abilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

// Factory for creating abilities with runtime registration
public final class AbilityFactory {
    
    private static final Map<String, Supplier<Ability>> REGISTRY = new HashMap<>();
    
    static {
        registerAbility("powerstrike", PowerStrike::new);
        registerAbility("arcaneburst", ArcaneBurst::new);
        registerAbility("doubleshot", DoubleShot::new);
        registerAbility("charge", Charge::new);
        registerAbility("masterstrike", MasterStrike::new);
        registerAbility("piercingvolley", PiercingVolley::new);
        registerAbility("evasion", Evasion::new);
    }
    
    private AbilityFactory() {}
    
    public static void registerAbility(String key, Supplier<Ability> creator) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Ability key cannot be null or empty");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Ability creator cannot be null");
        }
        REGISTRY.put(key.trim().toLowerCase(Locale.ROOT), creator);
    }
    
    public static boolean isRegistered(String key) {
        if (key == null) return false;
        return REGISTRY.containsKey(key.trim().toLowerCase(Locale.ROOT));
    }

    public static Ability create(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Ability key cannot be null");
        }
        
        String lowerKey = key.trim().toLowerCase(Locale.ROOT);
        Supplier<Ability> creator = REGISTRY.get(lowerKey);
        
        if (creator == null) {
            throw new IllegalArgumentException(
                "Unknown ability key: " + key + ". Available abilities: " + REGISTRY.keySet()
            );
        }
        
        return creator.get();
    }
    
    public static String[] getRegisteredAbilities() {
        return REGISTRY.keySet().toArray(new String[0]);
    }
    
    public static boolean unregisterAbility(String key) {
        if (key == null) return false;
        return REGISTRY.remove(key.trim().toLowerCase(Locale.ROOT)) != null;
    }
}
