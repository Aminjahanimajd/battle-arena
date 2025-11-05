package com.amin.battlearena.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Catalog of all available upgrades in the game
public final class UpgradeCatalog {
    
    private static final Map<String, List<Upgrade>> upgradesByCharacter = new HashMap<>();
    
    static {
        initializeUpgrades();
    }
    
    private static void initializeUpgrades() {
        // Warrior upgrades
        List<Upgrade> warriorUpgrades = new ArrayList<>();
        warriorUpgrades.add(new Upgrade("warrior_hp", "Warrior Vitality", "Increase Warrior's max HP", 
                                       Upgrade.Type.STAT_HP, 0, 3, 100, 25, 150, 1.5));
        warriorUpgrades.add(new Upgrade("warrior_attack", "Warrior Strength", "Increase Warrior's attack power", 
                                       Upgrade.Type.STAT_ATTACK, 0, 3, 12, 3, 200, 1.8));
        warriorUpgrades.add(new Upgrade("warrior_defense", "Warrior Armor", "Increase Warrior's defense", 
                                       Upgrade.Type.STAT_DEFENSE, 0, 3, 8, 2, 180, 1.6));
        warriorUpgrades.add(new Upgrade("warrior_mana", "Warrior Spirit", "Increase Warrior's mana pool", 
                                       Upgrade.Type.STAT_MANA, 0, 3, 30, 10, 120, 1.4));
        upgradesByCharacter.put("Warrior", warriorUpgrades);
        
        // Archer upgrades
        List<Upgrade> archerUpgrades = new ArrayList<>();
        archerUpgrades.add(new Upgrade("archer_hp", "Archer Endurance", "Increase Archer's max HP", 
                                      Upgrade.Type.STAT_HP, 0, 3, 90, 20, 140, 1.5));
        archerUpgrades.add(new Upgrade("archer_attack", "Archer Precision", "Increase Archer's attack power", 
                                      Upgrade.Type.STAT_ATTACK, 0, 3, 14, 2, 180, 1.7));
        archerUpgrades.add(new Upgrade("archer_defense", "Archer Evasion", "Increase Archer's defense", 
                                      Upgrade.Type.STAT_DEFENSE, 0, 3, 6, 2, 170, 1.6));
        archerUpgrades.add(new Upgrade("archer_mana", "Archer Focus", "Increase Archer's mana pool", 
                                      Upgrade.Type.STAT_MANA, 0, 3, 45, 8, 150, 1.5));
        upgradesByCharacter.put("Archer", archerUpgrades);
        
        // Mage upgrades
        List<Upgrade> mageUpgrades = new ArrayList<>();
        mageUpgrades.add(new Upgrade("mage_hp", "Mage Constitution", "Increase Mage's max HP", 
                                    Upgrade.Type.STAT_HP, 0, 3, 80, 15, 120, 1.4));
        mageUpgrades.add(new Upgrade("mage_attack", "Mage Power", "Increase Mage's attack power", 
                                    Upgrade.Type.STAT_ATTACK, 0, 3, 15, 3, 220, 1.9));
        mageUpgrades.add(new Upgrade("mage_mana", "Mage Wisdom", "Increase Mage's mana pool", 
                                    Upgrade.Type.STAT_MANA, 0, 3, 60, 15, 200, 1.6));
        mageUpgrades.add(new Upgrade("mage_mana_regen", "Mage Meditation", "Increase Mage's mana regeneration", 
                                    Upgrade.Type.STAT_MANA_REGEN, 0, 3, 8, 2, 300, 2.0));
        upgradesByCharacter.put("Mage", mageUpgrades);
        
        // Knight upgrades
        List<Upgrade> knightUpgrades = new ArrayList<>();
        knightUpgrades.add(new Upgrade("knight_hp", "Knight Fortitude", "Increase Knight's max HP", 
                                      Upgrade.Type.STAT_HP, 0, 3, 150, 35, 200, 1.7));
        knightUpgrades.add(new Upgrade("knight_defense", "Knight Protection", "Increase Knight's defense", 
                                      Upgrade.Type.STAT_DEFENSE, 0, 3, 15, 3, 250, 1.8));
        knightUpgrades.add(new Upgrade("knight_mana", "Knight Honor", "Increase Knight's mana pool", 
                                      Upgrade.Type.STAT_MANA, 0, 3, 40, 10, 160, 1.5));
        knightUpgrades.add(new Upgrade("knight_mana_regen", "Knight Discipline", "Increase Knight's mana regeneration", 
                                      Upgrade.Type.STAT_MANA_REGEN, 0, 3, 4, 1, 180, 1.6));
        upgradesByCharacter.put("Knight", knightUpgrades);
        
        // Ranger upgrades
        List<Upgrade> rangerUpgrades = new ArrayList<>();
        rangerUpgrades.add(new Upgrade("ranger_hp", "Ranger Stamina", "Increase Ranger's max HP", 
                                      Upgrade.Type.STAT_HP, 0, 3, 110, 25, 180, 1.6));
        rangerUpgrades.add(new Upgrade("ranger_attack", "Ranger Mastery", "Increase Ranger's attack power", 
                                      Upgrade.Type.STAT_ATTACK, 0, 3, 16, 3, 240, 1.8));
        rangerUpgrades.add(new Upgrade("ranger_defense", "Ranger Agility", "Increase Ranger's defense through agility", 
                                      Upgrade.Type.STAT_DEFENSE, 0, 3, 8, 2, 200, 1.7));
        rangerUpgrades.add(new Upgrade("ranger_mana", "Ranger Focus", "Increase Ranger's mana pool", 
                                      Upgrade.Type.STAT_MANA, 0, 3, 55, 10, 200, 1.6));
        upgradesByCharacter.put("Ranger", rangerUpgrades);
        
        // Master upgrades
        List<Upgrade> masterUpgrades = new ArrayList<>();
        masterUpgrades.add(new Upgrade("master_hp", "Master Vitality", "Increase Master's max HP", 
                                      Upgrade.Type.STAT_HP, 0, 3, 200, 40, 400, 2.0));
        masterUpgrades.add(new Upgrade("master_attack", "Master Power", "Increase Master's attack power", 
                                      Upgrade.Type.STAT_ATTACK, 0, 3, 20, 4, 500, 2.2));
        masterUpgrades.add(new Upgrade("master_defense", "Master Defense", "Increase Master's defense", 
                                      Upgrade.Type.STAT_DEFENSE, 0, 3, 12, 2, 350, 1.9));
        masterUpgrades.add(new Upgrade("master_mana", "Master Spirit", "Increase Master's mana pool", 
                                      Upgrade.Type.STAT_MANA, 0, 3, 80, 20, 300, 1.8));
        upgradesByCharacter.put("Master", masterUpgrades);
        
        // Global ability upgrades
        List<Upgrade> abilityUpgrades = new ArrayList<>();
        abilityUpgrades.add(new Upgrade("ability_cooldown", "Quick Casting", "Reduce ability cooldowns", 
                                       Upgrade.Type.ABILITY_COOLDOWN, 0, 3, 0, -1, 400, 2.5));
        abilityUpgrades.add(new Upgrade("ability_mana_cost", "Efficient Casting", "Reduce ability mana costs", 
                                       Upgrade.Type.ABILITY_MANA_COST, 0, 3, 0, -2, 350, 2.0));
        abilityUpgrades.add(new Upgrade("ability_damage", "Enhanced Abilities", "Increase ability damage", 
                                       Upgrade.Type.ABILITY_DAMAGE, 0, 3, 0, 3, 450, 2.2));
        upgradesByCharacter.put("Global", abilityUpgrades);
    }
    
    public static List<Upgrade> getUpgradesForCharacter(String characterType) {
        return upgradesByCharacter.getOrDefault(characterType, new ArrayList<>());
    }
    
    public static List<Upgrade> getAllUpgrades() {
        List<Upgrade> allUpgrades = new ArrayList<>();
        upgradesByCharacter.values().forEach(allUpgrades::addAll);
        return allUpgrades;
    }
    
    public static List<String> getAvailableCharacterTypes() {
        return new ArrayList<>(upgradesByCharacter.keySet());
    }
    
    public static Upgrade findUpgradeById(String upgradeId) {
        return getAllUpgrades().stream()
                .filter(u -> u.getId().equals(upgradeId))
                .findFirst()
                .orElse(null);
    }
}
