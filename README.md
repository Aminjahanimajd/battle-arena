# Battle Arena

Battle Arena is a JavaFX strategy game built with Maven. The app starts at a sign-in screen, then moves through a main menu, campaign selection, battle gameplay, shop upgrades, and help screens.

## Features

- Account sign-in with a nickname-based player profile
- Main menu with campaign, shop, help, account switch, and exit actions
- Campaign progression with selectable levels, locked or unlocked state, and rewards
- Turn-based grid combat with movement, attacks, abilities, consumables, and a battle timer
- In-game shop for character upgrades and consumable purchases
- Full-screen desktop UI built from FXML views and CSS styling

## Tech Stack

- Java 17
- JavaFX 17.0.6
- Maven

## Project Structure

- src/main/java/com/amin/battlearena/Main.java - JavaFX application entry point
- src/main/java/com/amin/battlearena/infra/SceneManager.java - scene switching helper
- src/main/java/com/amin/battlearena/uifx/controller/ - UI controllers
- src/main/java/com/amin/battlearena/domain/ - game logic, characters, abilities, consumables, teams, campaign, and shop models
- src/main/java/com/amin/battlearena/engine/ - game and AI engines
- src/main/resources/uifx/ - FXML views and stylesheet

## Requirements

- JDK 17 installed locally
- Maven 3.8+ recommended
- JavaFX runtime handled through the Maven dependencies in pom.xml

## Run the Game

From the project root:

```bash
mvn javafx:run
```

If you want to build first:

```bash
mvn clean package
```

## Gameplay Flow

1. Launch the app and enter a nickname on the sign-in screen.
2. Open the campaign screen and choose an unlocked level.
3. Start the battle, move units, attack enemies, and use abilities or consumables.
4. Return to the main menu to visit the shop, read help, or switch accounts.

## Notes

- The application starts in full-screen mode.
- The current Maven configuration targets Java 17 and uses the com.amin.battlearena.Main entry point.