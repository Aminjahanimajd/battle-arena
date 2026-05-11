# Battle Arena

Battle Arena is a JavaFX strategy game built with Maven. It opens in full-screen mode and guides the player through sign-in, campaign selection, tactical battles, shop upgrades, and help screens.

## Overview

The game is designed around a simple loop:

1. Sign in with a nickname.
2. Choose a campaign level.
3. Fight turn-based battles on a grid.
4. Spend gold in the shop to improve your team.
5. Continue progressing through unlocked levels.

## Highlights

- Account-based progress with a nickname-driven player profile
- Campaign map with locked and unlocked levels
- Tactical combat with movement, attacks, abilities, and consumables
- Battle timer and turn management
- In-game shop for upgrades and items
- FXML-based UI with a dedicated scene manager

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| UI | JavaFX 17.0.6 |
| Build Tool | Maven |

## Run Locally

### Prerequisites

- JDK 17
- Maven 3.8 or newer

### Start the game

```bash
mvn javafx:run
```

### Build the project

```bash
mvn clean package
```

## Project Layout

- `src/main/java/com/amin/battlearena/Main.java` - JavaFX application entry point
- `src/main/java/com/amin/battlearena/infra/SceneManager.java` - scene switching helper
- `src/main/java/com/amin/battlearena/engine/` - game loop and AI logic
- `src/main/java/com/amin/battlearena/domain/` - board, characters, abilities, consumables, shop, campaign, and team models
- `src/main/java/com/amin/battlearena/uifx/controller/` - FXML controllers for the screens
- `src/main/resources/uifx/` - FXML layouts and styles

## Gameplay Flow

- The sign-in screen stores or loads the active player profile.
- The main menu connects to campaign, shop, help, account switching, and exit.
- Campaign mode tracks unlocks, rewards, and level difficulty.
- Battle mode renders the board, handles character selection, and supports actions such as move, attack, and ability usage.
- Shop mode applies upgrades and consumable purchases using gold.

## Notes

- The application is configured to launch `com.amin.battlearena.Main`.
- The current Maven setup resolves JavaFX dependencies through `pom.xml`.