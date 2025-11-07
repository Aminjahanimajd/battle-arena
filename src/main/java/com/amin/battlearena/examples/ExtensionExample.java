package com.amin.battlearena.examples;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.domain.abilities.AbilityFactory;
import com.amin.battlearena.domain.abilities.AbstractAbility;
import com.amin.battlearena.domain.items.Consumable;
import com.amin.battlearena.domain.items.ConsumableFactory;
import com.amin.battlearena.domain.level.LevelFactory;
import com.amin.battlearena.domain.level.LevelSpec;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.CharacterFactory;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Stats;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

// Example showing how to extend the game with custom character, ability, consumable, and level
public final class ExtensionExample {
    
    private ExtensionExample() {}
    
    public static void registerCustomContent() {
        // Register custom character type
        CharacterFactory.registerType("assassin", Assassin::new);
        
        // Register custom ability
        AbilityFactory.registerAbility("shadowstrike", ShadowStrike::new);
        
        // Register custom consumable
        ConsumableFactory.registerType("invisibility_potion", InvisibilityPotion::new);
        
        // Register custom level
        LevelSpec assassinLevel = LevelFactory.builder("assassin_hideout")
            .name("Assassin's Hideout")
            .addEnemy("assassin", 5, 2)
            .addEnemy("assassin", 5, 4)
            .addEnemy("archer", 6, 3)
            .withRewards(350, 175)
            .requiresPlayerLevel(7)
            .withNote("Infiltrate the assassin's hideout and defeat their elite members")
            .build();
        
        LevelFactory.registerLevel(assassinLevel);
        
        System.out.println("Custom content registered successfully!");
    }
    
    // Custom character: high damage, low defense
    public static class Assassin extends Character {
        public Assassin(String name, Position pos) {
            super(
                name,
                new Stats(80, 40, 8),  // HP: 80, Attack: 40, Defense: 8
                pos,
                60,   // Max mana: 60
                8,    // Mana regen per turn: 8
                60    // Starting mana: 60
            );
        }
        
        @Override
        public int baseDamage() {
            return (int) (getStats().getAttack() * 1.2);
        }
        
        @Override
        public int getMovementRange() {
            return 2; // Assassins are nimble, can move 2 spaces
        }
    }
    
    // Custom ability: high damage with armor penetration
    public static class ShadowStrike extends AbstractAbility {
        public ShadowStrike() {
            super(
                "Shadow Strike",
                "Strike from the shadows, ignoring 50% of target's defense",
                3,  // 3 turn cooldown
                25  // 25 mana cost
            );
        }
        @Override
        public void activate(Character user, Character target, GameEngine engine) 
                throws InvalidActionException, DeadCharacterException {
            if (!canUse(user)) {
                throw new InvalidActionException("Cannot use Shadow Strike");
            }
            
            int baseDamage = user.baseDamage();
            int targetDefense = target.getStats().getDefense();
            int penetratedDefense = targetDefense / 2;
            int damage = Math.max(1, baseDamage - penetratedDefense);
            
            target.takeDamage(damage);
            
            engine.log(user.getName() + " strikes " + target.getName() 
                      + " from the shadows for " + damage + " damage!");
            
            startCooldown();
        }
    }
    
    // Custom consumable: provides defense boost
    public static class InvisibilityPotion implements Consumable {
        private final int defenseBoost;
        
        public InvisibilityPotion(int defenseBoost) {
            this.defenseBoost = Math.max(1, defenseBoost);
        }
        
        @Override
        public String key() {
            return "INVISIBILITY_POTION_" + defenseBoost;
        }
        
        @Override
        public String displayName() {
            return "Invisibility Potion";
        }
        
        @Override
        public String description() {
            return "Become invisible, gaining +" + defenseBoost + " defense for this turn";
        }
        @Override
        public int getCost() {
            return defenseBoost * 3;
        }
        
        @Override
        public void use(GameEngine engine, Character user, Character target) {
            Character receiver = (target != null) ? target : user;
            
            int oldDefense = receiver.getStats().getDefense();
            receiver.getStats().setDefense(oldDefense + defenseBoost);
            
            engine.log(receiver.getName() + " drinks an Invisibility Potion and gains +" 
                      + defenseBoost + " defense!");
        }
    }
    
    public static void demonstrateUsage() {
        registerCustomContent();
        
        Character assassin = CharacterFactory.create("assassin", "Silent Shadow", new Position(0, 0));
        System.out.println("Created: " + assassin.getName() + " (Assassin)");
        
        Ability shadowStrike = AbilityFactory.create("shadowstrike");
        assassin.addAbility(shadowStrike);
        System.out.println("Assassin learned: " + shadowStrike.getName());
        
        Consumable potion = ConsumableFactory.create("invisibility_potion", 10);
        System.out.println("Created: " + potion.displayName());
        
        LevelSpec level = LevelFactory.getLevel("assassin_hideout");
        System.out.println("Level available: " + level.name());
        
        System.out.println("\n=== Registered Content ===");
        System.out.println("Character types: " + String.join(", ", CharacterFactory.getRegisteredTypes()));
        System.out.println("Abilities: " + String.join(", ", AbilityFactory.getRegisteredAbilities()));
        System.out.println("Consumables: " + String.join(", ", ConsumableFactory.getRegisteredTypes()));
        System.out.println("Custom levels: " + String.join(", ", LevelFactory.getRegisteredLevelIds()));
    }
    
    public static void main(String[] args) {
        System.out.println("=== Battle Arena Extension Example ===\n");
        demonstrateUsage();
        System.out.println("\n=== Extension Example Complete ===");
    }
}
