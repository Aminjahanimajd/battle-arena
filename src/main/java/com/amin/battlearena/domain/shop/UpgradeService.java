package com.amin.battlearena.domain.shop;

import java.util.HashMap;
import java.util.Map;

public final class UpgradeService {
    private static final Map<Integer, Integer> UPGRADE_COSTS = new HashMap<>();
    private static final Map<Integer, String> UPGRADE_NAMES = new HashMap<>();
    
    static {
        // Warrior upgrades: 0=Health, 1=Attack, 2=Defense
        UPGRADE_COSTS.put(0, 100);   // Health Boost
        UPGRADE_COSTS.put(1, 150);   // Attack Power
        UPGRADE_COSTS.put(2, 120);   // Armor Boost
        
        // Archer upgrades: 3=Range, 4=Speed, 5=Attacks Per Turn
        UPGRADE_COSTS.put(3, 200);   // Eagle Eye (Range)
        UPGRADE_COSTS.put(4, 180);   // Swift Steps (Speed)
        UPGRADE_COSTS.put(5, 150);   // Attacks Per Turn
        
        // Mage upgrades: 6=Mana, 7=SpellPower, 8=Cooldown
        UPGRADE_COSTS.put(6, 100);   // Mana Pool
        UPGRADE_COSTS.put(7, 150);   // Spell Power
        UPGRADE_COSTS.put(8, 250);   // Quick Cast (Cooldown)
        
        // Upgrade names
        UPGRADE_NAMES.put(0, "Health Boost");
        UPGRADE_NAMES.put(1, "Attack Power");
        UPGRADE_NAMES.put(2, "Armor Boost");
        UPGRADE_NAMES.put(3, "Eagle Eye");
        UPGRADE_NAMES.put(4, "Swift Steps");
        UPGRADE_NAMES.put(5, "Attacks Per Turn");
        UPGRADE_NAMES.put(6, "Mana Pool");
        UPGRADE_NAMES.put(7, "Spell Power");
        UPGRADE_NAMES.put(8, "Quick Cast");
    }
    
    public int getUpgradeCost(int upgradeType) {
        return UPGRADE_COSTS.getOrDefault(upgradeType, 0);
    }
    
    public boolean canAffordUpgrade(int playerGold, int upgradeType) {
        return playerGold >= getUpgradeCost(upgradeType);
    }
    
    public String getUpgradeName(int upgradeType) {
        return UPGRADE_NAMES.getOrDefault(upgradeType, "Unknown");
    }
}
