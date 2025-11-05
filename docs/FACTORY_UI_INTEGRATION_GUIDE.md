# Factory UI Integration Guide

## Overview

This guide explains how the factory classes **automatically integrate** with the game's UI components. When you create new content using the factories, it will automatically appear in the appropriate UI screens without additional code.

---

## 🎮 Level Integration (Campaign UI)

### How It Works

When you register a level using `LevelFactory`, it automatically becomes available through `LevelRepository`, which means it will appear in the campaign selection screen.

### Step-by-Step Integration

```java
// 1. Create a level using the factory
LevelSpec customLevel = LevelFactory.builder("custom_dungeon")
    .name("Mystic Dungeon")
    .addEnemy("warrior", 5, 2)
    .addEnemy("mage", 5, 4)
    .withRewards(300, 150)
    .requiresPlayerLevel(5)
    .withNote("Explore the mystic dungeon")
    .build();

// 2. Register it with campaign metadata for UI display
LevelFactory.registerLevelForCampaign(
    customLevel,
    13,                    // Display order (after level 12)
    "Custom Dungeons"      // Chapter name for grouping
);

// That's it! The level now appears in:
// ✓ Campaign selection screen
// ✓ Level repository queries
// ✓ Game controller level loading
```

### What Happens Automatically

1. **LevelRepository Integration**
   - `LevelRepository.all()` now includes your custom level
   - `LevelRepository.require("custom_dungeon")` retrieves it
   - No need to modify JSON files

2. **Campaign UI Integration**
   - Level appears in campaign map (if controller refreshes)
   - Sorted by display order you specified
   - Grouped under chapter name
   - Shows name, rewards, and difficulty

3. **Game Controller Integration**
   - Can be started like any other level
   - Uses TeamFactory to create enemies
   - Applies rewards on completion

### Complete Example: Adding a Boss Rush Mode

```java
public class BossRushInitializer {
    public static void initializeBossRushLevels() {
        // Boss Rush 1: Knight Commander
        LevelSpec bossRush1 = LevelFactory.builder("boss_rush_1")
            .name("Boss Rush: Knight Commander")
            .addEnemy("knight", 7, 3)  // Single powerful boss
            .withRewards(800, 400)
            .requiresPlayerLevel(10)
            .withNote("Face the legendary Knight Commander in single combat!")
            .withWinCondition("DEFEAT_ALL_ENEMIES")
            .build();
        
        LevelFactory.registerLevelForCampaign(bossRush1, 13, "Boss Rush");
        
        // Boss Rush 2: Archmage Duel
        LevelSpec bossRush2 = LevelFactory.builder("boss_rush_2")
            .name("Boss Rush: Archmage Duel")
            .addPrerequisite("boss_rush_1")
            .addEnemy("mage", 7, 3)
            .withRewards(1000, 500)
            .requiresPlayerLevel(12)
            .withNote("Challenge the Archmage in a battle of magic!")
            .build();
        
        LevelFactory.registerLevelForCampaign(bossRush2, 14, "Boss Rush");
        
        System.out.println("Boss Rush mode initialized with 2 levels!");
    }
}
```

---

## 🛒 Consumable Integration (Shop UI)

### How It Works

When you register a consumable with `ShopConsumableRegistry`, it becomes available for purchase in the shop UI automatically.

### Step-by-Step Integration

```java
// 1. Create your custom consumable class
public class RevivalPotion implements Consumable {
    private final int reviveHp;
    
    public RevivalPotion(int reviveHp) {
        this.reviveHp = Math.max(1, reviveHp);
    }
    
    @Override public String key() { return "REVIVAL_POTION_" + reviveHp; }
    @Override public String displayName() { return "Revival Potion"; }
    @Override public String description() { return "Revive fallen ally with " + reviveHp + " HP"; }
    @Override public int getCost() { return 200; }
    
    @Override
    public void use(GameEngine engine, Character user, Character target) {
        if (!target.isAlive()) {
            target.getStats().setHp(reviveHp);
            engine.log(target.getName() + " has been revived!");
        }
    }
}

// 2. Register with factory
ConsumableFactory.registerType("revival_potion", RevivalPotion::new);

// 3. Register in shop with UI information
ShopConsumableRegistry.registerShopItem(
    "revival_potion",           // Type ID
    200,                        // Shop price in gold
    "Revival Potion",           // Display name in shop
    "Revive a fallen ally",     // Description in shop
    50                          // Default amount parameter
);

// OR use the convenience method (one step):
ConsumableFactory.registerTypeWithShop(
    "revival_potion",
    RevivalPotion::new,
    200,                        // Price
    "Revival Potion",          // Display name
    "Revive a fallen ally",    // Description
    50                         // Default amount
);

// That's it! The consumable now appears in:
// ✓ Shop purchase menu
// ✓ Player inventory system
// ✓ Battle consumable selection
```

### What Happens Automatically

1. **ConsumableFactory Integration**
   - `ConsumableFactory.create("revival_potion", 50)` creates instances
   - Available for inventory management
   - Can be used in battle

2. **Shop Integration**
   - Appears in shop UI consumables section
   - Shows price and description
   - Purchase button enabled if player has gold
   - Added to player inventory on purchase

3. **Inventory Integration**
   - Stored in player data
   - Available during battle
   - Can be used through UI or game logic

### Complete Example: Elemental Potions Set

```java
public class ElementalPotionsInitializer {
    public static void registerElementalPotions() {
        // Fire Potion - Offensive
        ConsumableFactory.registerTypeWithShop(
            "fire_potion",
            FirePotion::new,
            75,
            "Fire Potion",
            "Deal 30 fire damage to target",
            30  // damage amount
        );
        
        // Ice Potion - Defensive
        ConsumableFactory.registerTypeWithShop(
            "ice_potion",
            IcePotion::new,
            60,
            "Ice Potion",
            "Freeze enemy for 1 turn",
            1  // freeze duration
        );
        
        // Lightning Potion - Speed
        ConsumableFactory.registerTypeWithShop(
            "lightning_potion",
            LightningPotion::new,
            50,
            "Lightning Potion",
            "Gain extra turn immediately",
            1  // extra turns
        );
        
        System.out.println("Elemental Potions registered in shop!");
    }
}

// Potion implementations
class FirePotion implements Consumable {
    private final int damage;
    public FirePotion(int damage) { this.damage = damage; }
    @Override public String key() { return "FIRE_POTION"; }
    @Override public String displayName() { return "Fire Potion"; }
    @Override public String description() { return "Deal " + damage + " fire damage"; }
    @Override public int getCost() { return 75; }
    @Override public void use(GameEngine engine, Character user, Character target) {
        target.takeDamage(damage);
        engine.log(user.getName() + " hurls fire at " + target.getName() + " for " + damage + " damage!");
    }
}

class IcePotion implements Consumable {
    private final int duration;
    public IcePotion(int duration) { this.duration = duration; }
    @Override public String key() { return "ICE_POTION"; }
    @Override public String displayName() { return "Ice Potion"; }
    @Override public String description() { return "Freeze enemy"; }
    @Override public int getCost() { return 60; }
    @Override public void use(GameEngine engine, Character user, Character target) {
        // Add frozen status effect (implementation depends on your status system)
        engine.log(target.getName() + " is frozen solid!");
    }
}

class LightningPotion implements Consumable {
    private final int extraTurns;
    public LightningPotion(int turns) { this.extraTurns = turns; }
    @Override public String key() { return "LIGHTNING_POTION"; }
    @Override public String displayName() { return "Lightning Potion"; }
    @Override public String description() { return "Gain extra turn"; }
    @Override public int getCost() { return 50; }
    @Override public void use(GameEngine engine, Character user, Character target) {
        // Implementation for extra turns
        engine.log(user.getName() + " moves with lightning speed!");
    }
}
```

---

## 🎯 Character Integration (Game & Campaign UI)

### How It Works

Custom characters registered with `CharacterFactory` can be used anywhere in the game, including in custom levels and enemy teams.

### Using Custom Characters in Levels

```java
// 1. Register custom character type
CharacterFactory.registerType("assassin", Assassin::new);

// 2. Use in a level
LevelSpec assassinLevel = LevelFactory.builder("assassin_guild")
    .name("Assassin's Guild")
    .addEnemy("assassin", 5, 2)  // Uses your custom type!
    .addEnemy("assassin", 5, 4)
    .addEnemy("warrior", 6, 3)   // Mix with built-in types
    .withRewards(400, 200)
    .build();

LevelFactory.registerLevelForCampaign(assassinLevel, 15, "Special Missions");

// The TeamFactory will automatically create Assassin instances when this level loads!
```

### What Happens Automatically

1. **TeamFactory Integration**
   - `TeamFactory.buildEnemies()` uses CharacterFactory
   - Your custom characters spawn correctly in battles
   - Stats and abilities work as defined

2. **Game Controller Integration**
   - Battle UI renders custom characters
   - AI can control them
   - Players can fight against them

3. **Character Customization**
   - Can be used in player teams (with modifications)
   - Appears in character selection (if added to UI)
   - Works with all game systems (abilities, items, etc.)

---

## 🎨 Ability Integration (Character & Shop UI)

### How It Works

Custom abilities registered with `AbilityFactory` can be added to characters and purchased through the shop system.

### Using Custom Abilities

```java
// 1. Register ability
AbilityFactory.registerAbility("shadow_strike", ShadowStrike::new);

// 2. Add to characters programmatically
Character assassin = CharacterFactory.create("assassin", "Shadow", new Position(0, 0));
Ability shadowStrike = AbilityFactory.create("shadow_strike");
assassin.addAbility(shadowStrike);

// 3. Make it purchasable in shop (if using ShopService)
// The ShopService already uses AbilityFactory.create() internally
// So abilities are automatically available for purchase if configured
```

---

## 🔄 Complete Integration Flow

### Scenario: Adding a New Game Mode

Let's create a complete "Survival Mode" with custom levels, enemies, and rewards:

```java
public class SurvivalModeInitializer {
    
    public static void initializeSurvivalMode() {
        System.out.println("Initializing Survival Mode...");
        
        // Step 1: Register custom character types
        CharacterFactory.registerType("zombie", Zombie::new);
        CharacterFactory.registerType("skeleton", Skeleton::new);
        
        // Step 2: Register custom abilities for undead
        AbilityFactory.registerAbility("life_drain", LifeDrain::new);
        AbilityFactory.registerAbility("bone_throw", BoneThrow::new);
        
        // Step 3: Register custom consumables for survival
        ConsumableFactory.registerTypeWithShop(
            "antidote",
            Antidote::new,
            40,
            "Antidote",
            "Cure poison and disease",
            1
        );
        
        ConsumableFactory.registerTypeWithShop(
            "holy_water",
            HolyWater::new,
            60,
            "Holy Water",
            "Extra damage vs undead",
            50
        );
        
        // Step 4: Create survival levels
        for (int wave = 1; wave <= 5; wave++) {
            LevelSpec survivalLevel = LevelFactory.builder("survival_wave_" + wave)
                .name("Survival Wave " + wave)
                .addEnemy("zombie", 5, 2)
                .addEnemy("zombie", 5, 4)
                .addEnemy("skeleton", 6, 3)
                .withRewards(100 * wave, 50 * wave)
                .requiresPlayerLevel(wave * 2)
                .withNote("Survive wave " + wave + " of the undead horde!")
                .build();
            
            LevelFactory.registerLevelForCampaign(
                survivalLevel,
                100 + wave,  // Order after main campaign
                "Survival Mode"
            );
        }
        
        System.out.println("✓ Survival Mode initialized!");
        System.out.println("  - 2 new character types");
        System.out.println("  - 2 new abilities");
        System.out.println("  - 2 new shop items");
        System.out.println("  - 5 new survival levels");
    }
}

// Call this during game initialization:
// SurvivalModeInitializer.initializeSurvivalMode();
```

---

## 📋 Integration Checklist

When adding new content, follow this checklist:

### For New Levels
- [ ] Create LevelSpec using LevelFactory.builder()
- [ ] Register with LevelFactory.registerLevelForCampaign()
- [ ] Specify display order and chapter name
- [ ] Test level appears in campaign UI
- [ ] Verify enemies spawn correctly
- [ ] Check rewards are granted

### For New Consumables
- [ ] Create Consumable implementation
- [ ] Register with ConsumableFactory.registerType()
- [ ] Register in shop with ShopConsumableRegistry
- [ ] Or use ConsumableFactory.registerTypeWithShop()
- [ ] Test appears in shop UI
- [ ] Verify purchase works
- [ ] Check effect applies correctly in battle

### For New Characters
- [ ] Create Character subclass
- [ ] Register with CharacterFactory.registerType()
- [ ] Test creation via factory
- [ ] Use in levels via addEnemy()
- [ ] Verify renders in battle UI
- [ ] Check abilities and stats work

### For New Abilities
- [ ] Create Ability implementation
- [ ] Register with AbilityFactory.registerAbility()
- [ ] Add to characters as needed
- [ ] Test activation in battle
- [ ] Verify cooldown and mana cost
- [ ] Check visual feedback

---

## 🎯 Best Practices for UI Integration

1. **Initialize Early**
   - Register all custom content during game startup
   - Before any UI controllers load
   - Ensures everything is available

2. **Use Descriptive Names**
   - Clear display names for shop items
   - Intuitive level names for campaign
   - Helpful descriptions

3. **Set Appropriate Metadata**
   - Correct display order for levels
   - Logical chapter grouping
   - Reasonable prices for consumables

4. **Test Integration**
   - Verify items appear in UI
   - Check purchase flow works
   - Test level loading and completion

5. **Document Custom Content**
   - Comment your registration code
   - Explain any special interactions
   - Note balance considerations

---

## 🔍 Troubleshooting

### "Level not appearing in campaign"
- ✓ Check you called `registerLevelForCampaign()`, not just `registerLevel()`
- ✓ Ensure level ID is unique
- ✓ Verify prerequisite levels exist
- ✓ Check LevelRepository.all() includes your level

### "Consumable not in shop"
- ✓ Verify registered with ConsumableFactory first
- ✓ Check ShopConsumableRegistry.isAvailableInShop()
- ✓ Ensure ID matches exactly (case-insensitive)
- ✓ Try using `registerTypeWithShop()` convenience method

### "Custom character not spawning"
- ✓ Verify CharacterFactory.isRegistered() returns true
- ✓ Check level uses correct type name
- ✓ Ensure character constructor doesn't throw exceptions
- ✓ Test character creation manually first

### "Ability not working"
- ✓ Check AbilityFactory.isRegistered() returns true
- ✓ Verify ability implements all required methods
- ✓ Test mana cost and cooldown are reasonable
- ✓ Check for exceptions in activate() method

---

## 🎊 Summary

The factory pattern provides **automatic UI integration** for all custom content:

| Content Type | Factory | UI Integration | What You Get |
|-------------|---------|----------------|--------------|
| **Levels** | LevelFactory | Campaign UI | Auto-appears in level selection |
| **Consumables** | ConsumableFactory + Shop Registry | Shop UI | Auto-purchasable in shop |
| **Characters** | CharacterFactory | Battle UI | Auto-spawns in levels |
| **Abilities** | AbilityFactory | Character/Shop | Auto-available for characters |

**No UI code changes needed** - just register your content and it works! 🚀

For complete code examples, see:
- `ExtensionExample.java` - Full working example
- `FactoryTest.java` - Integration tests
- `FACTORY_EXTENSIBILITY_GUIDE.md` - Detailed factory usage
