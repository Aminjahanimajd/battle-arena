package com.amin.battlearena.app;

import java.io.IOException;
import java.util.List;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Knight;
import com.amin.battlearena.domain.model.Mage;
import com.amin.battlearena.domain.model.Master;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Warrior;
import com.amin.battlearena.economy.Upgrade;
import com.amin.battlearena.economy.UpgradeCatalog;
import com.amin.battlearena.levels.LevelLoader;

/**
 * Test runner to verify all game mechanics work correctly.
 */
public final class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== Battle Arena Game Test Runner ===\n");
        
        try {
            testCharacterCreation();
            testManaSystem();
            testAbilities();
            testUpgradeSystem();
            testLevelLoading();
            
            System.out.println("\n✅ All tests passed! Game is ready for GUI development.");
            
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            // Log error details for debugging
            System.err.println("Error details: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private static void testCharacterCreation() {
        System.out.println("Testing Character Creation...");
        
        // Test Warrior
        Warrior warrior = new Warrior("TestWarrior", new Position(0, 0));
        assert warrior.getCurrentMana() == 10 : "Warrior should start with 10 mana";
        assert warrior.getMaxMana() == 30 : "Warrior should have max 30 mana";
        assert warrior.getManaRegenPerTurn() == 3 : "Warrior should regen 3 mana per turn";
        assert warrior.getMovementRange() == 1 : "Warrior should move 1 space";
        System.out.println("✅ Warrior creation test passed");
        
        // Test Archer
        Archer archer = new Archer("TestArcher", new Position(0, 0));
        assert archer.getCurrentMana() == 20 : "Archer should start with 20 mana";
        assert archer.getMaxMana() == 45 : "Archer should have max 45 mana";
        assert archer.getMovementRange() == 2 : "Archer should move 2 spaces";
        System.out.println("✅ Archer creation test passed");
        
        // Test Mage
        Mage mage = new Mage("TestMage", new Position(0, 0));
        assert mage.getCurrentMana() == 25 : "Mage should start with 25 mana";
        assert mage.getMaxMana() == 60 : "Mage should have max 60 mana";
        assert mage.getManaRegenPerTurn() == 8 : "Mage should regen 8 mana per turn";
        System.out.println("✅ Mage creation test passed");
        
        // Test Knight
        Knight knight = new Knight("TestKnight", new Position(0, 0));
        assert knight.getCurrentMana() == 15 : "Knight should start with 15 mana";
        assert knight.getMaxMana() == 40 : "Knight should have max 40 mana";
        System.out.println("✅ Knight creation test passed");
        
        System.out.println("✅ All character creation tests passed\n");
    }
    
    private static void testManaSystem() {
        System.out.println("Testing Mana System...");
        
        Warrior warrior = new Warrior("TestWarrior", new Position(0, 0));
        
        // Test mana spending
        assert warrior.canSpendMana(5) : "Should be able to spend 5 mana";
        assert warrior.spendMana(5) : "Should successfully spend 5 mana";
        assert warrior.getCurrentMana() == 5 : "Should have 5 mana remaining";
        
        // Test insufficient mana
        assert !warrior.canSpendMana(10) : "Should not be able to spend 10 mana";
        assert !warrior.spendMana(10) : "Should not successfully spend 10 mana";
        assert warrior.getCurrentMana() == 5 : "Mana should remain unchanged";
        
        // Test mana restoration
        warrior.restoreMana(10);
        assert warrior.getCurrentMana() == 15 : "Should have 15 mana after restoration";
        
        // Test mana regeneration
        warrior.endTurnHousekeeping();
        assert warrior.getCurrentMana() == 18 : "Should have 18 mana after regeneration";
        
        System.out.println("✅ Mana system test passed\n");
    }
    
    private static void testAbilities() {
        System.out.println("Testing Abilities...");
        
        Warrior warrior = new Warrior("TestWarrior", new Position(0, 0));
        List<Ability> abilities = warrior.getAbilities();
        
        assert abilities.size() == 1 : "Warrior should have 1 ability";
        Ability powerStrike = abilities.get(0);
        
        assert powerStrike.getName().equals("Power Strike") : "Should be Power Strike ability";
        assert powerStrike.getManaCost() == 15 : "Power Strike should cost 15 mana";
        assert powerStrike.isReady() : "Ability should be ready initially";
        assert powerStrike.canUse(warrior) : "Should be able to use ability";
        
        // Test ability cooldown by using the ability (which will trigger cooldown)
        // We'll need a target, but since we can't create a full game engine here,
        // we'll just test the basic properties
        assert powerStrike.getCooldown() == 3 : "Power Strike should have 3 turn cooldown";
        assert powerStrike.getRemainingCooldown() == 0 : "Should start with 0 remaining cooldown";
        
        System.out.println("✅ Abilities test passed\n");
    }
    
    private static void testUpgradeSystem() {
        System.out.println("Testing Upgrade System...");
        
        // Test Warrior upgrades
        List<Upgrade> warriorUpgrades = UpgradeCatalog.getUpgradesForCharacter("Warrior");
        assert warriorUpgrades.size() == 4 : "Warrior should have 4 upgrade options";
        
        Upgrade hpUpgrade = warriorUpgrades.get(0);
        assert hpUpgrade.getType() == Upgrade.Type.STAT_HP : "Should be HP upgrade";
        assert hpUpgrade.getCurrentValue() == 100 : "Should start with base 100 HP";
        assert hpUpgrade.getNextStageValue() == 125 : "Next stage should be 125 HP";
        assert hpUpgrade.getUpgradeCost() == 150 : "First upgrade should cost 150 gold";
        
        // Test upgrade progression
        Upgrade upgraded = hpUpgrade.upgrade();
        assert upgraded.getCurrentStage() == 1 : "Should be at stage 1";
        assert upgraded.getCurrentValue() == 125 : "Should have 125 HP";
        assert upgraded.getUpgradeCost() == 225 : "Second upgrade should cost 225 gold";
        
        System.out.println("✅ Upgrade system test passed\n");
    }
    
    private static void testLevelLoading() {
        System.out.println("Testing Level Loading...");
        
        try {
            // Test level 1 loading
            List<Character> enemies = LevelLoader.createEnemiesForLevel(1);
            assert enemies.size() == 1 : "Level 1 should have 1 enemy";
            assert enemies.get(0) instanceof Warrior : "Level 1 enemy should be a Warrior";
            
            // Test level 10 loading (boss level)
            List<Character> bossEnemies = LevelLoader.createEnemiesForLevel(10);
            assert bossEnemies.size() == 1 : "Level 10 should have 1 enemy";
            assert bossEnemies.get(0) instanceof Master : "Level 10 enemy should be a Master";
            
            // Test player starting positions
            List<Position> playerPositions = LevelLoader.getPlayerStartingPositions();
            assert playerPositions.size() == 4 : "Should have 4 player starting positions";
            
            System.out.println("✅ Level loading test passed\n");
            
        } catch (IOException e) {
            throw new RuntimeException("Level loading test failed: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Level loading test failed: " + e.getMessage(), e);
        }
    }
}
