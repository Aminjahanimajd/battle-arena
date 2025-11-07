package com.amin.battlearena.infra;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration loader for character balance stats from balance.json.
 * Single source of truth for all character base stats, mana, movement, and abilities.
 * Also loads ability configurations (cooldowns, mana costs, ranges).
 * 
 * Similar pattern to LevelRepository - load once, cache in memory, provide query API.
 */
public final class CharacterBalanceConfig {
    
    private static CharacterBalanceConfig instance;
    private final Map<String, CharacterConfig> characterConfigs = new HashMap<>();
    private final Map<String, AbilityConfig> abilityConfigs = new HashMap<>();
    
    /**
     * Immutable configuration for an ability.
     * Contains all stats previously hardcoded in ability constructors.
     */
    public static class AbilityConfig {
        private final String name;
        private final String description;
        private final int cooldown;
        private final int manaCost;
        private final int range;
        private final String damageFormula;  // NEW: For UI damage display
        private final String damageType;      // NEW: physical/magical/utility
        
        public AbilityConfig(String name, String description, int cooldown, int manaCost, int range,
                            String damageFormula, String damageType) {
            this.name = name;
            this.description = description;
            this.cooldown = cooldown;
            this.manaCost = manaCost;
            this.range = range;
            this.damageFormula = damageFormula != null ? damageFormula : "Unknown";
            this.damageType = damageType != null ? damageType : "physical";
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getCooldown() { return cooldown; }
        public int getManaCost() { return manaCost; }
        public int getRange() { return range; }
        public String getDamageFormula() { return damageFormula; }
        public String getDamageType() { return damageType; }
    }
    
    /**
     * Immutable configuration for a character class.
     * Contains all stats previously hardcoded in character constructors.
     */
    public static class CharacterConfig {
        private final int health;
        private final int attack;
        private final int defense;
        private final int range;
        private final int maxMana;
        private final int manaRegen;
        private final int startingMana;
        private final int movementRange;
        private final int baseDamage;
        private final List<String> abilities;
        
        public CharacterConfig(int health, int attack, int defense, int range,
                              int maxMana, int manaRegen, int startingMana,
                              int movementRange, int baseDamage, List<String> abilities) {
            this.health = health;
            this.attack = attack;
            this.defense = defense;
            this.range = range;
            this.maxMana = maxMana;
            this.manaRegen = manaRegen;
            this.startingMana = startingMana;
            this.movementRange = movementRange;
            this.baseDamage = baseDamage;
            this.abilities = List.copyOf(abilities); // Defensive copy
        }
        
        // Base stats getters
        public int getHealth() { return health; }
        public int getAttack() { return attack; }
        public int getDefense() { return defense; }
        public int getRange() { return range; }
        
        // Mana stats getters
        public int getMaxMana() { return maxMana; }
        public int getManaRegen() { return manaRegen; }
        public int getStartingMana() { return startingMana; }
        
        // Other stats getters
        public int getMovementRange() { return movementRange; }
        public int getBaseDamage() { return baseDamage; }
        public List<String> getAbilities() { return abilities; }
    }
    
    private CharacterBalanceConfig() {
        loadConfiguration();
    }
    
    /**
     * Singleton instance accessor with lazy initialization.
     */
    public static synchronized CharacterBalanceConfig getInstance() {
        if (instance == null) {
            instance = new CharacterBalanceConfig();
        }
        return instance;
    }
    
    /**
     * Loads character and ability configurations from balance.json in classpath.
     * Called once during initialization.
     */
    private void loadConfiguration() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("balance.json")) {
            if (is == null) {
                throw new IllegalStateException("balance.json not found in classpath");
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            
            // Load character configurations
            JsonNode charactersNode = root.get("characters");
            if (charactersNode == null || !charactersNode.isObject()) {
                throw new IllegalStateException("balance.json missing 'characters' object");
            }
            
            charactersNode.fields().forEachRemaining(entry -> {
                String className = entry.getKey();
                JsonNode config = entry.getValue();
                
                CharacterConfig characterConfig = parseCharacterConfig(className, config);
                characterConfigs.put(className.toLowerCase(), characterConfig);
            });
            
            System.out.println("✅ Loaded " + characterConfigs.size() + " character configurations from balance.json");
            
            // Load ability configurations
            JsonNode abilitiesNode = root.get("abilities");
            if (abilitiesNode != null && abilitiesNode.isObject()) {
                abilitiesNode.fields().forEachRemaining(entry -> {
                    String abilityId = entry.getKey();
                    JsonNode config = entry.getValue();
                    
                    AbilityConfig abilityConfig = parseAbilityConfig(abilityId, config);
                    abilityConfigs.put(abilityId, abilityConfig);
                });
                
                System.out.println("✅ Loaded " + abilityConfigs.size() + " ability configurations from balance.json");
            }
            
            // Load economy configuration
            JsonNode economyNode = root.get("economy");
            if (economyNode != null && economyNode.isObject()) {
                if (economyNode.has("goldPerKill")) {
                    economyGoldPerKill = economyNode.get("goldPerKill").asInt();
                }
                if (economyNode.has("goldForWin")) {
                    economyGoldForWin = economyNode.get("goldForWin").asInt();
                }
                System.out.println("✅ Loaded economy config: goldPerKill=" + economyGoldPerKill + ", goldForWin=" + economyGoldForWin);
            }
            
            // Load game configuration
            JsonNode gameNode = root.get("game");
            if (gameNode != null && gameNode.isObject()) {
                if (gameNode.has("maxTurns")) {
                    gameMaxTurns = gameNode.get("maxTurns").asInt();
                }
                if (gameNode.has("undoHistorySize")) {
                    gameUndoHistorySize = gameNode.get("undoHistorySize").asInt();
                }
                System.out.println("✅ Loaded game config: maxTurns=" + gameMaxTurns + ", undoHistorySize=" + gameUndoHistorySize);
            }
            
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load balance.json", e);
        }
    }
    
    /**
     * Parses a single ability configuration from JSON node.
     */
    private AbilityConfig parseAbilityConfig(String abilityId, JsonNode config) {
        try {
            String name = config.get("name").asText();
            String description = config.get("description").asText();
            int cooldown = config.get("cooldown").asInt();
            int manaCost = config.get("manaCost").asInt();
            int range = config.get("range").asInt();
            
            // NEW: Load damage formula and type (optional fields with defaults)
            String damageFormula = config.has("damageFormula") 
                ? config.get("damageFormula").asText() 
                : "attack + baseDamage";
            String damageType = config.has("damageType") 
                ? config.get("damageType").asText() 
                : "physical";
            
            return new AbilityConfig(name, description, cooldown, manaCost, range, 
                                    damageFormula, damageType);
            
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to parse configuration for ability '" + abilityId + "'", e
            );
        }
    }
    
    /**
     * Parses a single character configuration from JSON node.
     */
    private CharacterConfig parseCharacterConfig(String className, JsonNode config) {
        try {
            JsonNode baseStats = config.get("baseStats");
            JsonNode manaStats = config.get("manaStats");
            
            if (baseStats == null || manaStats == null) {
                throw new IllegalStateException(
                    "Character '" + className + "' missing baseStats or manaStats in balance.json"
                );
            }
            
            int health = baseStats.get("health").asInt();
            int attack = baseStats.get("attack").asInt();
            int defense = baseStats.get("defense").asInt();
            int range = baseStats.get("range").asInt();
            
            int maxMana = manaStats.get("maxMana").asInt();
            int manaRegen = manaStats.get("manaRegen").asInt();
            int startingMana = manaStats.get("startingMana").asInt();
            
            int movementRange = config.get("movementRange").asInt();
            int baseDamage = config.get("baseDamage").asInt();
            
            JsonNode abilitiesNode = config.get("abilities");
            List<String> abilities = new java.util.ArrayList<>();
            if (abilitiesNode != null && abilitiesNode.isArray()) {
                abilitiesNode.forEach(node -> abilities.add(node.asText()));
            }
            
            return new CharacterConfig(health, attack, defense, range,
                                      maxMana, manaRegen, startingMana,
                                      movementRange, baseDamage, abilities);
            
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to parse configuration for character '" + className + "'", e
            );
        }
    }
    
    /**
     * Retrieves configuration for a character class.
     * 
     * @param className Class name (case-insensitive): "warrior", "mage", "archer", "knight", "ranger", "master"
     * @return Character configuration with all stats
     * @throws IllegalArgumentException if character class not found in balance.json
     */
    public CharacterConfig getCharacterConfig(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Character class name cannot be null");
        }
        
        CharacterConfig config = characterConfigs.get(className.toLowerCase());
        if (config == null) {
            throw new IllegalArgumentException(
                "Unknown character class: '" + className + "'. " +
                "Available classes: " + characterConfigs.keySet()
            );
        }
        
        return config;
    }
    
    /**
     * Checks if a character class is defined in balance.json.
     */
    public boolean hasCharacterConfig(String className) {
        return className != null && characterConfigs.containsKey(className.toLowerCase());
    }
    
    /**
     * Gets all available character class names.
     */
    public java.util.Set<String> getAvailableCharacterClasses() {
        return java.util.Set.copyOf(characterConfigs.keySet());
    }
    
    /**
     * Retrieves configuration for an ability.
     * 
     * @param abilityId Ability ID (case-sensitive): "PowerStrike", "ArcaneBurst", etc.
     * @return Ability configuration with cooldown, mana cost, range
     * @throws IllegalArgumentException if ability not found in balance.json
     */
    public AbilityConfig getAbilityConfig(String abilityId) {
        if (abilityId == null) {
            throw new IllegalArgumentException("Ability ID cannot be null");
        }
        
        AbilityConfig config = abilityConfigs.get(abilityId);
        if (config == null) {
            throw new IllegalArgumentException(
                "Unknown ability: '" + abilityId + "'. " +
                "Available abilities: " + abilityConfigs.keySet()
            );
        }
        
        return config;
    }
    
    /**
     * Checks if an ability is defined in balance.json.
     */
    public boolean hasAbilityConfig(String abilityId) {
        return abilityId != null && abilityConfigs.containsKey(abilityId);
    }
    
    /**
     * Gets all available ability IDs.
     */
    public java.util.Set<String> getAvailableAbilities() {
        return java.util.Set.copyOf(abilityConfigs.keySet());
    }
    
    // ========== ECONOMY CONFIG ==========
    
    /**
     * Gets gold reward per kill from balance.json economy section.
     * Returns 10 if not configured.
     */
    public int getGoldPerKill() {
        return economyGoldPerKill;
    }
    
    /**
     * Gets gold reward for winning battle from balance.json economy section.
     * Returns 50 if not configured.
     */
    public int getGoldForWin() {
        return economyGoldForWin;
    }
    
    // ========== GAME CONFIG ==========
    
    /**
     * Gets maximum turns limit from balance.json game section.
     * Returns 1000 if not configured.
     */
    public int getMaxTurns() {
        return gameMaxTurns;
    }
    
    /**
     * Gets undo history size from balance.json game section.
     * Returns 10 if not configured.
     */
    public int getUndoHistorySize() {
        return gameUndoHistorySize;
    }
    
    private int economyGoldPerKill = 10;  // default fallback
    private int economyGoldForWin = 50;   // default fallback
    private int gameMaxTurns = 1000;      // default fallback
    private int gameUndoHistorySize = 10; // default fallback
}
