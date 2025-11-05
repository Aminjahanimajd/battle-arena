# Factory Classes - Extensibility Guide

This document explains how to use and extend the Battle Arena game using the new factory classes. These factories follow the **Open/Closed Principle** - the codebase is open for extension but closed for modification.

## Overview

Four new factory classes have been created to improve extensibility:

1. **CharacterFactory** - Create and register new character types
2. **AbilityFactory** - Create and register new abilities
3. **ConsumableFactory** - Create and register new consumable items
4. **LevelFactory** - Create and register new levels programmatically

---

## 1. CharacterFactory

### Purpose
Provides an extensible way to create character instances without modifying existing code.

### Location
`src/main/java/com/amin/battlearena/domain/model/CharacterFactory.java`

### Usage Examples

#### Basic Character Creation
```java
// Create a warrior
Character warrior = CharacterFactory.create("warrior", "Hero", new Position(0, 0));

// Create a mage
Character mage = CharacterFactory.create("mage", "Wizard", new Position(1, 1));
```

#### Character Creation with Scaled Stats
```java
// Create an enemy with custom stats (useful for difficulty scaling)
Character boss = CharacterFactory.createWithStats(
    "knight",           // type
    "Dragon Knight",    // name
    new Position(5, 5), // position
    500,                // HP
    50,                 // Attack
    30                  // Defense
);
```

#### Register a New Character Type
```java
// Step 1: Create your custom character class
public class Assassin extends Character {
    public Assassin(String name, Position pos) {
        super(name, new Stats(70, 35, 10), pos, 50, 5, 50);
    }
    
    @Override
    public int baseDamage() {
        return getStats().getAttack() + 5; // Bonus damage
    }
}

// Step 2: Register it with the factory
CharacterFactory.registerType("assassin", Assassin::new);

// Step 3: Create instances using the factory
Character assassin = CharacterFactory.create("assassin", "Shadow", new Position(2, 2));
```

#### Check Available Types
```java
// Check if a type is registered
boolean hasAssassin = CharacterFactory.isRegistered("assassin");

// Get all registered types
String[] types = CharacterFactory.getRegisteredTypes();
System.out.println("Available character types: " + Arrays.toString(types));
```

---

## 2. AbilityFactory

### Purpose
Provides an extensible way to create ability instances and supports runtime registration of new abilities.

### Location
`src/main/java/com/amin/battlearena/domain/abilities/AbilityFactory.java`

### Usage Examples

#### Basic Ability Creation
```java
// Create a power strike ability
Ability powerStrike = AbilityFactory.create("powerstrike");

// Create an arcane burst ability
Ability arcaneBurst = AbilityFactory.create("arcaneburst");
```

#### Register a New Ability
```java
// Step 1: Create your custom ability class
public class Firestorm extends AbstractAbility {
    public Firestorm() {
        super("Firestorm", "Unleash a storm of fire", 3, 25);
    }
    
    @Override
    public void activate(Character user, Character target, GameEngine engine) 
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            throw new InvalidActionException("Cannot use Firestorm");
        }
        
        // Deal AOE damage
        int damage = user.getStats().getAttack() * 2;
        target.takeDamage(damage);
        
        engine.log(user.getName() + " unleashes Firestorm on " + target.getName() 
                   + " for " + damage + " damage!");
        
        resetCooldown();
    }
}

// Step 2: Register it with the factory
AbilityFactory.registerAbility("firestorm", Firestorm::new);

// Step 3: Create and use the ability
Ability firestorm = AbilityFactory.create("firestorm");
character.addAbility(firestorm);
```

#### Check Available Abilities
```java
// Check if an ability is registered
boolean hasFirestorm = AbilityFactory.isRegistered("firestorm");

// Get all registered abilities
String[] abilities = AbilityFactory.getRegisteredAbilities();
System.out.println("Available abilities: " + Arrays.toString(abilities));
```

---

## 3. ConsumableFactory

### Purpose
Provides an extensible way to create consumable items and supports custom consumable types.

### Location
`src/main/java/com/amin/battlearena/domain/items/ConsumableFactory.java`

### Usage Examples

#### Basic Consumable Creation
```java
// Create a health potion that heals 50 HP
Consumable healthPotion = ConsumableFactory.create("health_potion", 50);

// Create a mana potion that restores 30 mana
Consumable manaPotion = ConsumableFactory.create("mana_potion", 30);

// Convenience methods
Consumable quickHeal = ConsumableFactory.createHealthPotion(25);
Consumable quickMana = ConsumableFactory.createManaPotion(15);
```

#### Register a New Consumable Type
```java
// Step 1: Create your custom consumable class
public class RevivePotion implements Consumable {
    private final int reviveHp;
    
    public RevivePotion(int reviveHp) {
        this.reviveHp = Math.max(1, reviveHp);
    }
    
    @Override
    public String key() { return "REVIVE_POTION_" + reviveHp; }
    
    @Override
    public String displayName() { return "Revive Potion"; }
    
    @Override
    public String description() { 
        return "Revives a fallen ally with " + reviveHp + " HP"; 
    }
    
    @Override
    public int getCost() { return 200; }
    
    @Override
    public void use(GameEngine engine, Character user, Character target) {
        if (!target.isAlive()) {
            target.getStats().setHp(reviveHp);
            engine.log(target.getName() + " has been revived with " + reviveHp + " HP!");
        }
    }
}

// Step 2: Register it with the factory
ConsumableFactory.registerType("revive_potion", RevivePotion::new);

// Step 3: Create and use the consumable
Consumable revive = ConsumableFactory.create("revive_potion", 50);
player.getInventory().add(revive);
```

#### Check Available Consumables
```java
// Check if a type is registered
boolean hasRevive = ConsumableFactory.isRegistered("revive_potion");

// Get all registered types
String[] types = ConsumableFactory.getRegisteredTypes();
System.out.println("Available consumables: " + Arrays.toString(types));
```

---

## 4. LevelFactory

### Purpose
Provides a fluent API for creating levels programmatically and supports runtime level registration.

### Location
`src/main/java/com/amin/battlearena/domain/level/LevelFactory.java`

### Usage Examples

#### Basic Level Creation
```java
// Create a simple level using the builder
LevelSpec level = LevelFactory.builder("tutorial_1")
    .name("Training Grounds")
    .addEnemy("warrior", 5, 2)
    .addEnemy("archer", 5, 3)
    .withRewards(100, 50)
    .requiresPlayerLevel(1)
    .withNote("Learn the basics of combat")
    .build();

// Register it for use
LevelFactory.registerLevel(level);
```

#### Advanced Level Creation
```java
// Create a complex boss level
LevelSpec bossLevel = LevelFactory.builder("dragon_lair")
    .name("Dragon's Lair")
    .addPrerequisite("mountain_pass")
    .addPrerequisite("ancient_ruins")
    .requiresPlayerLevel(10)
    .addEnemy("warrior", 7, 2)  // Dragon guardian
    .addEnemy("mage", 7, 4)     // Dragon guardian
    .addEnemy("master", 8, 3)   // Dragon boss
    .withRewards(500, 250)
    .withNote("Face the ancient dragon and its guardians")
    .withWinCondition("DEFEAT_ALL_ENEMIES")
    .build();

LevelFactory.registerLevel(bossLevel);
```

#### Using Custom Character Types in Levels
```java
// After registering a custom "assassin" character type
LevelSpec customLevel = LevelFactory.builder("shadow_temple")
    .name("Shadow Temple")
    .addEnemy("assassin", 5, 2)  // Uses your custom character type
    .addEnemy("assassin", 5, 4)
    .addEnemy("knight", 6, 3)
    .withRewards(300, 150)
    .build();

LevelFactory.registerLevel(customLevel);
```

#### Retrieve and Use Levels
```java
// Check if a level is registered
boolean hasLevel = LevelFactory.isRegistered("dragon_lair");

// Get a level
LevelSpec level = LevelFactory.getLevel("dragon_lair");

// Get all custom level IDs
String[] levelIds = LevelFactory.getRegisteredLevelIds();
```

---

## Complete Example: Adding a New Character Class

Here's a complete example showing how to add a new "Necromancer" character class to the game:

```java
// 1. Create the character class
package com.amin.battlearena.domain.model;

public class Necromancer extends Character {
    public Necromancer(String name, Position pos) {
        super(
            name,
            new Stats(90, 25, 8),  // HP, Attack, Defense
            pos,
            100,  // Max mana
            10,   // Mana regen per turn
            100   // Starting mana
        );
    }
    
    @Override
    public int baseDamage() {
        // Necromancers deal magic damage based on attack stat
        return getStats().getAttack();
    }
}

// 2. Create a unique ability for the Necromancer
package com.amin.battlearena.domain.abilities;

public class SummonUndead extends AbstractAbility {
    public SummonUndead() {
        super("Summon Undead", "Summon a skeleton warrior", 4, 30);
    }
    
    @Override
    public void activate(Character user, Character target, GameEngine engine) 
            throws InvalidActionException, DeadCharacterException {
        if (!canUse(user)) {
            throw new InvalidActionException("Cannot summon undead");
        }
        
        // Create a skeleton ally (implementation would create a temporary character)
        engine.log(user.getName() + " summons an undead warrior!");
        
        resetCooldown();
    }
}

// 3. Register the character and ability
public class GameInitializer {
    public static void initializeCustomContent() {
        // Register the necromancer character type
        CharacterFactory.registerType("necromancer", Necromancer::new);
        
        // Register the necromancer's unique ability
        AbilityFactory.registerAbility("summonundead", SummonUndead::new);
        
        // Create a level featuring necromancers
        LevelSpec necroLevel = LevelFactory.builder("necro_crypt")
            .name("Necromancer's Crypt")
            .addEnemy("necromancer", 6, 2)
            .addEnemy("necromancer", 6, 4)
            .addEnemy("warrior", 7, 3)
            .withRewards(400, 200)
            .requiresPlayerLevel(8)
            .withNote("Face the dark necromancers")
            .build();
        
        LevelFactory.registerLevel(necroLevel);
    }
}

// 4. Use the new character
public class Example {
    public void createNecromancer() {
        // Create a necromancer character
        Character necro = CharacterFactory.create("necromancer", "Dark Mage", new Position(0, 0));
        
        // Add the unique ability
        Ability summon = AbilityFactory.create("summonundead");
        necro.addAbility(summon);
        
        // Add to player's team
        player.addToTeam(necro);
    }
}
```

---

## Benefits of This Approach

### 1. **Open/Closed Principle**
- The core factory classes are closed for modification
- New content can be added through registration without touching existing code

### 2. **Extensibility**
- Game designers can add new characters, abilities, consumables, and levels
- Mods and plugins can register custom content at runtime

### 3. **Type Safety**
- All factories use generics and functional interfaces for type safety
- Compiler catches errors early

### 4. **Separation of Concerns**
- Character creation logic is separated from character behavior
- Level creation is separated from level execution

### 5. **Testability**
- Easy to register mock implementations for testing
- Can unregister types after tests

### 6. **Configuration-Driven Design**
- Factories can load from JSON/XML configuration files
- Non-programmers can add content via configuration

---

## Best Practices

1. **Always use factories instead of `new` keyword** when creating game entities
2. **Register custom types during game initialization**, not during gameplay
3. **Use descriptive type identifiers** (e.g., "fire_dragon" instead of "fd")
4. **Document custom character stats** in your character class
5. **Test custom content** before deploying to production
6. **Follow existing naming conventions** for consistency

---

## Migration from Old Code

### Before (Direct Instantiation)
```java
Warrior warrior = new Warrior("Hero", new Position(0, 0));
HealthPotion potion = new HealthPotion(50);
```

### After (Using Factories)
```java
Character warrior = CharacterFactory.create("warrior", "Hero", new Position(0, 0));
Consumable potion = ConsumableFactory.createHealthPotion(50);
```

This allows you to later switch to custom character types without changing the calling code!

---

## Troubleshooting

### "Unknown character type" Error
**Solution**: Make sure you've registered the type using `CharacterFactory.registerType()` before trying to create it.

### "Unknown ability key" Error
**Solution**: Register the ability using `AbilityFactory.registerAbility()` before creation.

### "Level already registered" Error
**Solution**: Each level must have a unique ID. Check if you're accidentally registering the same level twice.

### Type is not showing up
**Solution**: Ensure your registration code runs during game initialization, before any factory methods are called.

---

## Next Steps

1. Review the factory implementations in the source code
2. Try creating a simple custom character type
3. Experiment with creating custom levels programmatically
4. Consider creating a JSON loader that uses these factories

For more information, see the JavaDoc comments in each factory class.
