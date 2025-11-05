package com.amin.battlearena.examples;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.abilities.AbilityFactory;
import com.amin.battlearena.domain.items.Consumable;
import com.amin.battlearena.domain.items.ConsumableFactory;
import com.amin.battlearena.domain.level.LevelFactory;
import com.amin.battlearena.domain.level.LevelSpec;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.CharacterFactory;
import com.amin.battlearena.domain.model.Position;

// Test to verify factory pattern implementation
public final class FactoryTest {
    
    public static void main(String[] args) {
        System.out.println("=== Factory Pattern Test ===\n");
        
        try {
            // Test CharacterFactory
            System.out.println("1. Testing CharacterFactory...");
            Character warrior = CharacterFactory.create("warrior", "Test Warrior", new Position(0, 0));
            Character mage = CharacterFactory.create("mage", "Test Mage", new Position(1, 1));
            Character archer = CharacterFactory.create("archer", "Test Archer", new Position(2, 2));
            System.out.println("   ✓ Created: " + warrior.getName() + ", " + mage.getName() + ", " + archer.getName());
            System.out.println("   ✓ Registered types: " + String.join(", ", CharacterFactory.getRegisteredTypes()));
            
            // Test AbilityFactory
            System.out.println("\n2. Testing AbilityFactory...");
            Ability powerStrike = AbilityFactory.create("powerstrike");
            Ability arcaneBurst = AbilityFactory.create("arcaneburst");
            Ability doubleShot = AbilityFactory.create("doubleshot");
            System.out.println("   ✓ Created: " + powerStrike.getName() + ", " + arcaneBurst.getName() + ", " + doubleShot.getName());
            System.out.println("   ✓ Registered abilities: " + String.join(", ", AbilityFactory.getRegisteredAbilities()));
            
            // Test ConsumableFactory
            System.out.println("\n3. Testing ConsumableFactory...");
            Consumable healthPotion = ConsumableFactory.createHealthPotion(50);
            Consumable manaPotion = ConsumableFactory.createManaPotion(30);
            System.out.println("   ✓ Created: " + healthPotion.displayName() + ", " + manaPotion.displayName());
            System.out.println("   ✓ Registered types: " + String.join(", ", ConsumableFactory.getRegisteredTypes()));
            
            // Test LevelFactory
            System.out.println("\n4. Testing LevelFactory...");
            LevelSpec testLevel = LevelFactory.builder("test_level")
                .name("Test Level")
                .addEnemy("warrior", 5, 2)
                .addEnemy("mage", 5, 3)
                .withRewards(100, 50)
                .build();
            LevelFactory.registerLevel(testLevel);
            LevelSpec retrieved = LevelFactory.getLevel("test_level");
            System.out.println("   ✓ Created and registered level: " + retrieved.name());
            System.out.println("   ✓ Level has " + retrieved.enemies().size() + " enemies");
            
            // Test custom character registration
            System.out.println("\n5. Testing custom character registration...");
            CharacterFactory.registerType("test_custom", (name, pos) -> 
                CharacterFactory.create("warrior", name, pos));
            Character custom = CharacterFactory.create("test_custom", "Custom", new Position(0, 0));
            System.out.println("   ✓ Created custom character: " + custom.getName());
            
            System.out.println("\n=== All Tests Passed! ===");
            System.out.println("\nFactory pattern implementation is working correctly.");
            System.out.println("You can now extend the game by:");
            System.out.println("  • Adding new character types with CharacterFactory.registerType()");
            System.out.println("  • Adding new abilities with AbilityFactory.registerAbility()");
            System.out.println("  • Adding new consumables with ConsumableFactory.registerType()");
            System.out.println("  • Creating new levels with LevelFactory.builder()");
            
        } catch (Exception e) {
            System.err.println("\n=== Test Failed! ===");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
