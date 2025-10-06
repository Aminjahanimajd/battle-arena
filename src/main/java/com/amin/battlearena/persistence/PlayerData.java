package com.amin.battlearena.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Persistent data for a player account including gold, upgrades, and settings.
 */
public class PlayerData {
    private String playerName;
    private int gold = 1000; // Starting gold
    private final Map<String, Integer> upgrades = new HashMap<>(); // upgrade_name -> level
    private final Map<String, Integer> upgradeCosts = new HashMap<>(); // upgrade_name -> current_cost
    private final Map<String, Object> settings = new HashMap<>();
    
    // Campaign progress
    private int currentLevel = 1; // Highest unlocked level
    private int victories = 0; // Total victories
    private final Map<Integer, Boolean> levelCompleted = new HashMap<>(); // level_number -> completed
    
    // Account data
    private String accountId; // Unique account identifier
    private long createdAt; // Account creation timestamp
    private long lastLoginAt; // Last login timestamp
    private int totalPlayTime = 0; // Total play time in minutes
    private final Map<String, Integer> statistics = new HashMap<>(); // Game statistics
    
    // Shop data
    private final Map<String, Integer> purchasedUpgrades = new HashMap<>(); // upgrade_id -> level
    private final Map<String, Long> upgradeTimestamps = new HashMap<>(); // upgrade_id -> purchase_time
    
    // Consumables inventory
    private final Map<String, Integer> consumables = new HashMap<>(); // consumable_id -> quantity
    
    public PlayerData() {
        initializeDefaults();
    }
    
    public PlayerData(String playerName) {
        this.playerName = playerName;
        this.accountId = generateAccountId(playerName);
        this.createdAt = System.currentTimeMillis();
        this.lastLoginAt = System.currentTimeMillis();
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        // Initialize upgrade costs
        upgradeCosts.put("Health Boost", 150);
        upgradeCosts.put("Attack Power", 200);
        upgradeCosts.put("Armor Boost", 175);
        upgradeCosts.put("Eagle Eye", 180);
        upgradeCosts.put("Swift Steps", 220);
        upgradeCosts.put("Precision Shot", 300);
        upgradeCosts.put("Mana Pool", 160);
        upgradeCosts.put("Spell Power", 250);
        upgradeCosts.put("Quick Cast", 350);
        
        // Initialize upgrade levels
        for (String upgrade : upgradeCosts.keySet()) {
            upgrades.put(upgrade, 0);
        }
        
        // Default settings
        settings.put("language", "English");
        settings.put("dialogueEnabled", true);
        settings.put("musicEnabled", true);
        settings.put("soundEnabled", true);
        settings.put("masterVolume", 70);
        settings.put("musicVolume", 60);
        settings.put("sfxVolume", 80);
        settings.put("gameHintsEnabled", true);
        settings.put("fullScreenMode", false);
        
        // Initialize statistics
        statistics.put("battlesWon", 0);
        statistics.put("battlesLost", 0);
        statistics.put("totalDamageDealt", 0);
        statistics.put("totalDamageTaken", 0);
        statistics.put("abilitiesUsed", 0);
        statistics.put("goldEarned", 0);
        statistics.put("upgradesPurchased", 0);
        
        // Initialize consumables
        consumables.put("health_potion", 3); // Start with 3 health potions
        consumables.put("mana_potion", 2); // Start with 2 mana potions
        consumables.put("strength_elixir", 1); // Start with 1 strength elixir
    }
    
    private String generateAccountId(String playerName) {
        return playerName == null ? "unknown_" + System.currentTimeMillis()
            : playerName.toLowerCase(java.util.Locale.ROOT) + "_" + System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }
    public void addGold(int amount) { this.gold += amount; }
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
    
    public int getUpgradeLevel(String upgradeName) {
        return upgrades.getOrDefault(upgradeName, 0);
    }
    
    public void setUpgradeLevel(String upgradeName, int level) {
        upgrades.put(upgradeName, Math.max(0, level));
    }
    
    public int getUpgradeCost(String upgradeName) {
        return upgradeCosts.getOrDefault(upgradeName, 100);
    }
    
    public void increaseUpgradeCost(String upgradeName) {
        int currentCost = getUpgradeCost(upgradeName);
        int newCost = (int)(currentCost * 1.5); // 50% increase each time
        upgradeCosts.put(upgradeName, newCost);
    }
    
    public Object getSetting(String key) {
        return settings.get(key);
    }
    
    public void setSetting(String key, Object value) {
        settings.put(key, value);
    }
    
    public Map<String, Integer> getUpgrades() { return new HashMap<>(upgrades); }
    public Map<String, Integer> getUpgradeCosts() { return new HashMap<>(upgradeCosts); }
    public Map<String, Object> getSettings() { return new HashMap<>(settings); }
    
    // Campaign progress methods
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = Math.max(1, level); }
    
    public int getVictories() { return victories; }
    public void setVictories(int victories) { this.victories = Math.max(0, victories); }
    public void addVictory() { this.victories++; }
    
    public boolean isLevelCompleted(int levelNumber) {
        return levelCompleted.getOrDefault(levelNumber, false);
    }
    
    public void setLevelCompleted(int levelNumber, boolean completed) {
        levelCompleted.put(levelNumber, completed);
        if (completed && levelNumber >= currentLevel) {
            currentLevel = levelNumber + 1; // Unlock next level
        }
    }
    
    public void completeLevel(int levelNumber, int goldReward) {
        setLevelCompleted(levelNumber, true);
        addGold(goldReward);
        addVictory();
        incrementStatistic("goldEarned", goldReward);
        incrementStatistic("battlesWon", 1);
    }
    
    // Account data methods
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(long lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void updateLastLogin() { this.lastLoginAt = System.currentTimeMillis(); }
    
    public int getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(int totalPlayTime) { this.totalPlayTime = totalPlayTime; }
    public void addPlayTime(int minutes) { this.totalPlayTime += minutes; }
    
    // Statistics methods
    public int getStatistic(String key) { return statistics.getOrDefault(key, 0); }
    public void setStatistic(String key, int value) { statistics.put(key, value); }
    public void incrementStatistic(String key, int amount) {
        statistics.put(key, getStatistic(key) + amount);
    }
    public Map<String, Integer> getStatistics() { return new HashMap<>(statistics); }
    
    // Purchased upgrades methods
    public int getPurchasedUpgradeLevel(String upgradeId) {
        return purchasedUpgrades.getOrDefault(upgradeId, 0);
    }
    
    public void setPurchasedUpgradeLevel(String upgradeId, int level) {
        purchasedUpgrades.put(upgradeId, level);
        upgradeTimestamps.put(upgradeId, System.currentTimeMillis());
        incrementStatistic("upgradesPurchased", 1);
    }
    
    public Map<String, Integer> getPurchasedUpgrades() { return new HashMap<>(purchasedUpgrades); }
    public Map<String, Long> getUpgradeTimestamps() { return new HashMap<>(upgradeTimestamps); }
    
    // Consumables methods
    public int getConsumableQuantity(String consumableId) {
        return consumables.getOrDefault(consumableId, 0);
    }
    
    public void setConsumableQuantity(String consumableId, int quantity) {
        consumables.put(consumableId, Math.max(0, quantity));
    }
    
    public boolean useConsumable(String consumableId) {
        int current = getConsumableQuantity(consumableId);
        if (current > 0) {
            setConsumableQuantity(consumableId, current - 1);
            return true;
        }
        return false;
    }
    
    public void addConsumable(String consumableId, int quantity) {
        int current = getConsumableQuantity(consumableId);
        setConsumableQuantity(consumableId, current + quantity);
    }
    
    public Map<String, Integer> getConsumables() { return new HashMap<>(consumables); }
}