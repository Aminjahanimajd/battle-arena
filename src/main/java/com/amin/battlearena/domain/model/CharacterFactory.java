package com.amin.battlearena.domain.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

// Factory for creating characters with runtime type registration
public final class CharacterFactory {
    
    private static final Map<String, BiFunction<String, Position, Character>> REGISTRY = new HashMap<>();
    
    static {
        registerType("warrior", Warrior::new);
        registerType("mage", Mage::new);
        registerType("archer", Archer::new);
        registerType("knight", Knight::new);
        registerType("ranger", Ranger::new);
        registerType("master", Master::new);
    }
    
    private CharacterFactory() {}
    
    public static void registerType(String type, BiFunction<String, Position, Character> creator) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Character type cannot be null or empty");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Character creator function cannot be null");
        }
        REGISTRY.put(type.trim().toLowerCase(), creator);
    }
    
    public static boolean isRegistered(String type) {
        if (type == null) return false;
        return REGISTRY.containsKey(type.trim().toLowerCase());
    }
    
    public static Character create(String type, String name, Position position) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Character type cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be null or empty");
        }
        if (position == null) {
            throw new IllegalArgumentException("Character position cannot be null");
        }
        
        String normalizedType = type.trim().toLowerCase();
        BiFunction<String, Position, Character> creator = REGISTRY.get(normalizedType);
        
        if (creator == null) {
            throw new IllegalArgumentException(
                "Unknown character type: " + type + ". Available types: " + REGISTRY.keySet()
            );
        }
        
        return creator.apply(name, position);
    }
    
    // Create character with scaled stats for difficulty adjustment
    public static Character createWithStats(String type, String name, Position position, 
                                           int hpModifier, int attackModifier, int defenseModifier) {
        Character character = create(type, name, position);
        Stats stats = character.getStats();
        stats.setMaxHp(hpModifier);
        stats.setHp(hpModifier);
        stats.setAttack(attackModifier);
        stats.setDefense(defenseModifier);
        return character;
    }
    
    public static String[] getRegisteredTypes() {
        return REGISTRY.keySet().toArray(String[]::new);
    }
    
    public static boolean unregisterType(String type) {
        if (type == null) return false;
        return REGISTRY.remove(type.trim().toLowerCase()) != null;
    }
}
