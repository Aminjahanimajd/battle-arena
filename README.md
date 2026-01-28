# Battle Arena

A turn-based tactical combat game built with JavaFX featuring strategic gameplay, character progression, and a comprehensive campaign mode.

## 🎮 Game Overview

Battle Arena is a grid-based tactical RPG where players command a team of three unique character classes (Warrior, Archer, and Mage) through progressively challenging campaign levels. The game features a deep upgrade system, inventory management, and persistent player progression through a save/load system.

## ✨ Key Features

- **Turn-Based Tactical Combat**: Strategic grid-based battles with movement and attack mechanics
- **Three Unique Character Classes**:
  - **Warrior**: High HP, melee specialist with powerful slash attacks
  - **Archer**: Ranged combatant with superior mobility and multi-attack capabilities
  - **Mage**: Spell caster with devastating area-of-effect abilities
- **10-Level Campaign**: Progressive difficulty with unique enemy compositions
- **Comprehensive Upgrade System**: 9 different upgrade types across all character classes
- **Inventory & Consumables**: Health Potions, Mana Potions, and Haste Potions
- **Player Progression**: Persistent account system with save/load functionality
- **Reward System**: Earn gold and items by completing campaign levels
- **Shop System**: Purchase upgrades and consumables between battles
- **AI-Driven Enemies**: Smart enemy behavior with tactical decision-making

## 🏗️ Architecture

The project follows Domain-Driven Design principles with a clean separation of concerns:

### Domain Layer
Core business logic organized into cohesive subdomains:

#### Character System
- Factory pattern for character creation
- Interface-based design for extensibility
- Base stats with upgrade modifiers
- Team-based architecture (HumanTeam, EnemyTeam)

#### Ability System
- Specialized abilities for each character class
- Cooldown and mana cost management
- Target validation and range checking

#### Campaign System
- 10 progressive levels with configurable difficulty
- Reward distribution (gold + consumables)
- Level unlocking based on player progress

#### Shop & Upgrades
- Upgrade service with 9 upgrade types
- Consumable shop with dynamic pricing
- Player gold management

#### Account Management
- Repository pattern for data persistence
- File-based save/load system
- Player profile with statistics and inventory

### Presentation Layer (JavaFX)
- FXML-based UI design
- MVC pattern with dedicated controllers
- Scene management for navigation
- Responsive grid-based game board

### Engine Layer
- Game state management
- Turn processing
- AI decision engine
- Combat calculations

## 📁 Project Structure

```
battle-arena/
├── src/
│   ├── main/
│   │   ├── java/com/amin/battlearena/
│   │   │   ├── Main.java                          # Application entry point
│   │   │   ├── domain/                            # Core domain logic
│   │   │   │   ├── Board.java                     # Game board representation
│   │   │   │   ├── Tile.java                      # Individual tile/cell
│   │   │   │   ├── Inventory.java                 # Inventory management
│   │   │   │   ├── ability/                       # Ability system
│   │   │   │   │   ├── Ability.java               # Base ability class
│   │   │   │   │   ├── AbilityFactory.java        # Factory for abilities
│   │   │   │   │   ├── AbilityInterface.java      # Ability contract
│   │   │   │   │   ├── Slash.java                 # Warrior ability
│   │   │   │   │   ├── Shot.java                  # Archer ability
│   │   │   │   │   └── Fireball.java              # Mage ability
│   │   │   │   ├── account/                       # Account management
│   │   │   │   │   ├── Player.java                # Player entity
│   │   │   │   │   └── AccountRepository.java     # Player data persistence
│   │   │   │   ├── campaign/                      # Campaign system
│   │   │   │   │   ├── LevelConfig.java           # Level configurations
│   │   │   │   │   ├── LevelData.java             # Level data model
│   │   │   │   │   ├── Reward.java                # Reward entity
│   │   │   │   │   └── RewardService.java         # Reward distribution
│   │   │   │   ├── character/                     # Character system
│   │   │   │   │   ├── Character.java             # Base character class
│   │   │   │   │   ├── CharacterFactory.java      # Factory for characters
│   │   │   │   │   ├── CharacterInterface.java    # Character contract
│   │   │   │   │   ├── Warrior.java               # Warrior implementation
│   │   │   │   │   ├── Archer.java                # Archer implementation
│   │   │   │   │   └── Mage.java                  # Mage implementation
│   │   │   │   ├── consumable/                    # Consumable items
│   │   │   │   │   ├── Consumable.java            # Base consumable class
│   │   │   │   │   ├── ConsumableFactory.java     # Factory for consumables
│   │   │   │   │   ├── ConsumableInterface.java   # Consumable contract
│   │   │   │   │   ├── HealthPotion.java          # HP restoration
│   │   │   │   │   ├── ManaPotion.java            # Mana restoration
│   │   │   │   │   └── HastePotion.java           # Speed boost
│   │   │   │   ├── shop/                          # Shop system
│   │   │   │   │   ├── Shop.java                  # Main shop facade
│   │   │   │   │   ├── UpgradeService.java        # Upgrade management
│   │   │   │   │   └── ConsumableShop.java        # Consumable purchases
│   │   │   │   └── team/                          # Team management
│   │   │   │       ├── Team.java                  # Base team class
│   │   │   │       ├── HumanTeam.java             # Player team
│   │   │   │       └── EnemyTeam.java             # AI team
│   │   │   ├── engine/                            # Game engine
│   │   │   │   ├── GameEngine.java                # Core game logic
│   │   │   │   ├── AiEngine.java                  # AI decision-making
│   │   │   │   └── balance/                       # Balance configurations
│   │   │   ├── infra/                             # Infrastructure
│   │   │   │   └── SceneManager.java              # Scene navigation
│   │   │   ├── persistence/                       # Data persistence
│   │   │   │   └── AccountRepository.java         # Save/load implementation
│   │   │   └── uifx/                              # JavaFX UI layer
│   │   │       └── controller/                    # FXML controllers
│   │   │           ├── MainMenuController.java    # Main menu
│   │   │           ├── SignInController.java      # Player login
│   │   │           ├── CampaignController.java    # Campaign map
│   │   │           ├── GameController.java        # Battle screen
│   │   │           ├── ShopController.java        # Shop interface
│   │   │           └── HelpController.java        # Help screen
│   │   └── resources/
│   │       └── uifx/                              # FXML & CSS resources
│   │           ├── main_menu.fxml                 # Main menu layout
│   │           ├── signin.fxml                    # Sign-in layout
│   │           ├── campaign.fxml                  # Campaign layout
│   │           ├── game.fxml                      # Game board layout
│   │           ├── shop.fxml                      # Shop layout
│   │           ├── help.fxml                      # Help layout
│   │           └── styles.css                     # Global styles
│   └── test/
│       └── java/                                  # Unit tests (to be added)
├── target/                                        # Compiled classes
├── pom.xml                                        # Maven configuration
├── battlearena.iml                                # IntelliJ module file
└── README.md                                      # This file
```

## 🎯 Upgrade System

### Warrior Upgrades
- **Health Boost** (100 gold): +20 HP per level
- **Attack Power** (150 gold): +5 ATK per level
- **Armor Boost** (120 gold): +3 DEF per level

### Archer Upgrades
- **Eagle Eye** (200 gold): +1 Range per level
- **Swift Steps** (180 gold): +1 Speed per level
- **Rapid Assault** (150 gold): +1 Attack per turn

### Mage Upgrades
- **Mana Pool** (100 gold): +20 Mana per level
- **Spell Power** (150 gold): +8 Spell damage per level
- **Quick Cast** (250 gold): Reduced ability cooldowns

## 🛒 Shop System

### Consumables
- **Health Potion** (50 gold): Restores 50 HP
- **Mana Potion** (30 gold): Restores 30 Mana
- **Haste Potion** (75 gold): +2 Speed for duration

## 🎮 Campaign Progression

| Level | Enemies | Difficulty | Gold Reward |
|-------|---------|------------|-------------|
| 1 | 2x Warrior | 1.0x | 50 |
| 2 | 2x Warrior, 1x Archer | 1.2x | 75 |
| 3 | 1x Warrior, 2x Archer | 1.4x | 100 |
| 4 | 1x Warrior, 1x Archer, 1x Mage | 1.6x | 125 |
| 5 | 2x Archer, 1x Mage | 1.8x | 150 |
| 6 | 1x Warrior, 1x Archer, 1x Mage | 2.0x | 200 |
| 7 | 1x Warrior, 2x Mage | 2.2x | 250 |
| 8 | 2x Archer, 2x Mage | 2.4x | 300 |
| 9 | 1x Warrior, 1x Archer, 2x Mage | 2.6x | 400 |
| 10 | 2x Warrior, 1x Archer, 2x Mage (Final Boss) | 3.0x | 500 |

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- JavaFX SDK

### Building the Project
```bash
mvn clean compile
```

### Running the Application
```bash
mvn javafx:run
```

### Creating an Executable JAR
```bash
mvn clean package
java -jar target/battlearena-1.0-SNAPSHOT.jar
```

## 🎮 How to Play

1. **Sign In**: Enter your nickname to create/load your profile
2. **Campaign Map**: Select an unlocked level to begin battle
3. **Combat Phase**:
   - Move your characters within their movement range
   - Attack enemies within range
   - Use abilities (costs mana)
   - Use consumables from inventory
4. **Victory**: Defeat all enemies to earn rewards and unlock the next level
5. **Shop**: Spend gold on upgrades and consumables between battles
6. **Progression**: Level up your team and conquer all 10 campaign levels

## 🛠️ Technologies Used

- **Java 17+**: Core programming language
- **JavaFX**: UI framework for desktop application
- **FXML**: Declarative UI markup
- **CSS**: Styling and theming
- **Maven**: Build automation and dependency management
- **Factory Pattern**: Object creation and abstraction
- **Repository Pattern**: Data persistence
- **MVC Pattern**: Separation of presentation and logic

## 📊 Design Patterns

- **Factory Pattern**: CharacterFactory, AbilityFactory, ConsumableFactory
- **Repository Pattern**: AccountRepository for data persistence
- **Strategy Pattern**: Different AI behaviors and abilities
- **Template Method**: Base character and ability classes
- **Singleton**: AccountRepository, SceneManager
- **MVC**: Clear separation between UI (FXML), Controllers, and Domain logic

## 🔮 Future Enhancements

- [ ] Multiplayer support (PvP mode)
- [ ] Additional character classes (Healer, Assassin, etc.)
- [ ] More campaign levels and difficulty modes
- [ ] Achievement system
- [ ] Character customization and skins
- [ ] Sound effects and background music
- [ ] Animation improvements
- [ ] Unit testing coverage
- [ ] Cloud-based save system
- [ ] Leaderboards and statistics

## 👤 Author

**Amin Jahani Majd**
- GitHub: [@Aminjahanimajd](https://github.com/Aminjahanimajd)

## 📝 License

This project is created for educational purposes.

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- Design pattern resources and best practices

---

**Version**: 1.0.0  
**Last Updated**: January 28, 2026  
**Status**: Active Development
