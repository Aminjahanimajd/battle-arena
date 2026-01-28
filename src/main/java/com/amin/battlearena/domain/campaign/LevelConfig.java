package com.amin.battlearena.domain.campaign;

import java.util.HashMap;
import java.util.Map;

public final class LevelConfig {
    private static final Map<Integer, LevelData> LEVELS = new HashMap<>();
    
    static {
        Map<String, Integer> level1Consumables = new HashMap<>();
        level1Consumables.put("HealthPotion", 1);
        LEVELS.put(1, new LevelData(new String[]{"Warrior", "Warrior"}, 1.0f, new Reward(50, level1Consumables)));
        
        Map<String, Integer> level2Consumables = new HashMap<>();
        level2Consumables.put("HealthPotion", 1);
        level2Consumables.put("ManaPotion", 1);
        LEVELS.put(2, new LevelData(new String[]{"Warrior", "Warrior", "Archer"}, 1.2f, new Reward(75, level2Consumables)));
        
        Map<String, Integer> level3Consumables = new HashMap<>();
        level3Consumables.put("HealthPotion", 2);
        LEVELS.put(3, new LevelData(new String[]{"Warrior", "Archer", "Archer"}, 1.4f, new Reward(100, level3Consumables)));
        
        Map<String, Integer> level4Consumables = new HashMap<>();
        level4Consumables.put("HealthPotion", 2);
        level4Consumables.put("ManaPotion", 1);
        LEVELS.put(4, new LevelData(new String[]{"Warrior", "Archer", "Mage"}, 1.6f, new Reward(125, level4Consumables)));
        
        Map<String, Integer> level5Consumables = new HashMap<>();
        level5Consumables.put("HealthPotion", 2);
        level5Consumables.put("ManaPotion", 2);
        LEVELS.put(5, new LevelData(new String[]{"Archer", "Archer", "Mage"}, 1.8f, new Reward(150, level5Consumables)));
        
        Map<String, Integer> level6Consumables = new HashMap<>();
        level6Consumables.put("HealthPotion", 3);
        level6Consumables.put("HastePotion", 1);
        LEVELS.put(6, new LevelData(new String[]{"Warrior", "Archer", "Mage"}, 2.0f, new Reward(200, level6Consumables)));
        
        Map<String, Integer> level7Consumables = new HashMap<>();
        level7Consumables.put("HealthPotion", 3);
        level7Consumables.put("ManaPotion", 2);
        level7Consumables.put("HastePotion", 1);
        LEVELS.put(7, new LevelData(new String[]{"Warrior", "Mage", "Mage"}, 2.2f, new Reward(250, level7Consumables)));
        
        Map<String, Integer> level8Consumables = new HashMap<>();
        level8Consumables.put("HealthPotion", 4);
        level8Consumables.put("ManaPotion", 3);
        LEVELS.put(8, new LevelData(new String[]{"Archer", "Archer", "Mage", "Mage"}, 2.4f, new Reward(300, level8Consumables)));
        
        Map<String, Integer> level9Consumables = new HashMap<>();
        level9Consumables.put("HealthPotion", 5);
        level9Consumables.put("ManaPotion", 3);
        level9Consumables.put("HastePotion", 2);
        LEVELS.put(9, new LevelData(new String[]{"Warrior", "Archer", "Mage", "Mage"}, 2.6f, new Reward(400, level9Consumables)));
        
        Map<String, Integer> level10Consumables = new HashMap<>();
        level10Consumables.put("HealthPotion", 6);
        level10Consumables.put("ManaPotion", 4);
        level10Consumables.put("HastePotion", 2);
        LEVELS.put(10, new LevelData(new String[]{"Warrior", "Warrior", "Archer", "Mage", "Mage"}, 3.0f, new Reward(500, level10Consumables)));
    }
    
    private LevelConfig() {}
    
    public static String[] getEnemyTypes(int level) {
        LevelData data = LEVELS.get(level);
        return data != null ? data.getTypes() : new String[]{};
    }
    
    public static float getDifficultyMultiplier(int level) {
        LevelData data = LEVELS.get(level);
        return data != null ? data.getMultiplier() : 1.0f;
    }
    
    public static Reward getReward(int level) {
        LevelData data = LEVELS.get(level);
        return data != null ? data.getReward() : new Reward(0);
    }
    
    public static boolean isLevelValid(int level) {
        return LEVELS.containsKey(level);
    }
}
