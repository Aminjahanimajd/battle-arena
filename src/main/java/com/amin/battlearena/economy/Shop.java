package com.amin.battlearena.economy;

import java.util.List;

import com.amin.battlearena.domain.model.Character;

/**
 * Shop system for purchasing upgrades and applying them to characters.
 */
public final class Shop {
    private final UpgradeCatalog catalog;

    public Shop(UpgradeCatalog catalog) { 
        this.catalog = catalog; 
    }

    /**
     * Get all available upgrades for a specific character type.
     */
    public List<Upgrade> getUpgradesForCharacter(String characterType) {
        return UpgradeCatalog.getUpgradesForCharacter(characterType);
    }

    /**
     * Get all available upgrades.
     */
    public List<Upgrade> getAllUpgrades() {
        return UpgradeCatalog.getAllUpgrades();
    }

    /**
     * Purchase and apply an upgrade to a character.
     */
    public boolean purchaseUpgrade(Wallet wallet, Character character, String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        if (upgrade == null) return false;
        
        if (wallet.gold() < upgrade.getUpgradeCost()) return false;
        
        // Spend the gold
        wallet.add(-upgrade.getUpgradeCost());
        
        // Apply the upgrade to the character
        applyUpgradeToCharacter(character, upgrade);
        
        return true;
    }

    /**
     * Apply an upgrade to a character's stats or abilities.
     */
    private void applyUpgradeToCharacter(Character character, Upgrade upgrade) {
        switch (upgrade.getType()) {
            case STAT_HP -> {
                // Increase max HP and restore current HP proportionally
                int currentHp = character.getStats().getHp();
                int maxHp = character.getStats().getMaxHp();
                int newMaxHp = upgrade.getCurrentValue();
                character.getStats().setMaxHp(newMaxHp);
                // Restore HP proportionally
                if (maxHp > 0) {
                    int newHp = (int) ((double) currentHp / maxHp * newMaxHp);
                    character.getStats().setHp(newHp);
                }
            }
            case STAT_ATTACK -> character.getStats().setAttack(upgrade.getCurrentValue());
            case STAT_DEFENSE -> character.getStats().setDefense(upgrade.getCurrentValue());
            case STAT_SPEED -> character.getStats().setSpeed(upgrade.getCurrentValue());
            case STAT_MANA -> {
                // Increase max mana and restore current mana proportionally
                int maxMana = character.getMaxMana();
                int newMaxMana = upgrade.getCurrentValue();
                // Note: We can't directly set max mana, so we'll just restore current mana
                character.restoreMana(newMaxMana - maxMana);
            }
            case STAT_MANA_REGEN -> {
                // Note: We can't directly modify mana regen in the current system
                // This would require adding a method to Character class
            }
            case ABILITY_COOLDOWN -> {
                // Note: We can't directly modify ability cooldowns in the current system
                // This would require adding methods to Ability classes
            }
            case ABILITY_MANA_COST -> {
                // Note: We can't directly modify ability mana costs in the current system
                // This would require adding methods to Ability classes
            }
            case ABILITY_DAMAGE -> {
                // Note: We can't directly modify ability damage in the current system
                // This would require adding methods to Ability classes
            }
        }
    }

    /**
     * Get the cost of an upgrade.
     */
    public int getUpgradeCost(String upgradeId) {
        Upgrade upgrade = UpgradeCatalog.findUpgradeById(upgradeId);
        return upgrade != null ? upgrade.getUpgradeCost() : Integer.MAX_VALUE;
    }
}
