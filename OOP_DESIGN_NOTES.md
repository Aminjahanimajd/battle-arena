# Deep Customization and OOP in Battle Arena

Battle Arena is architected from the ground up to showcase deep customization through object-oriented programming (OOP) principles. The project leverages interfaces, abstract classes, and extensible factory patterns to allow developers and modders to introduce new abilities, items, characters, and levels without modifying the core engine. By decoupling object creation from usage and supporting runtime registration, the system enables dynamic extension and flexible gameplay evolution, making it easy to add or modify features as requirements change.

This extensibility is achieved through a combination of runtime registries, modular handler classes, and clear separation of concerns. Factories such as `AbilityFactory`, `ConsumableFactory`, and `CharacterFactory` allow new types to be registered and instantiated on demand, while handler and controller classes manage UI and game logic in a loosely coupled manner. This design empowers both developers and advanced users to deeply customize the game, supporting everything from new gameplay mechanics to user interface enhancements, all while maintaining code safety, maintainability, and scalability.

## Handlers: OOP Strength
Handlers in Battle Arena encapsulate specific responsibilities, such as rendering the game board, processing user input, or managing upgrades. By isolating these concerns in dedicated classes (e.g., `BoardRenderHandler`, `UpgradePurchaseHandler`), the project adheres to the single responsibility principle and makes the codebase easier to test, maintain, and extend. Handlers can be developed, tested, and replaced independently, supporting modularity and flexibility throughout the system.

## Delegators: OOP Strength
Delegators are used to offload work to specialized classes, promoting loose coupling and code reuse. For example, `ShopController` delegates upgrade and consumable purchase logic to `UpgradePurchaseHandler` and `ConsumablePurchaseHandler`, while the game engine delegates event publishing and turn management to dedicated classes. This approach allows for flexible and maintainable code, as changes in one component do not require changes in the delegator, and new behaviors can be introduced through new delegate classes.

## Factories: OOP Strength
Factories in Battle Arena centralize object creation logic, making the system highly extensible. Patterns like `AbilityFactory`, `ConsumableFactory`, and `CharacterFactory` allow new types to be registered and instantiated dynamically, without modifying existing code. This supports scalability and maintainability, enabling the project to grow and adapt to new requirements with minimal risk of breaking existing functionality.

## Other Efficient and Interesting OOP Designs
Battle Arena also employs the Strategy pattern (for AI and validation), event-driven architecture (using an event bus and event classes), and immutability for value objects. These patterns, along with a strong focus on the single responsibility principle, make the project robust, maintainable, and a strong example of object-oriented programming in practice.

# Object-Oriented Design Concepts in Battle Arena

This document explains key object-oriented programming (OOP) concepts as implemented in the Battle Arena project. For each concept, you will find at least two code examples (with file/class/method references) and a detailed explanation.

---

## Encapsulation

Encapsulation is the bundling of data (fields) and methods that operate on that data within a single unit, typically a class. It restricts direct access to some of an object's components, which is fundamental for maintaining control over the state and behavior of objects. By grouping related variables and functions together, encapsulation helps to organize code, reduce complexity, and prevent accidental interference with the internal workings of an object.

In the Battle Arena project, encapsulation is a core design principle. It ensures that the internal state of objects can only be changed through well-defined interfaces, which means that the logic for updating or retrieving data is always consistent and predictable. This not only protects the integrity of the data but also makes the codebase easier to maintain and refactor, since changes to the internal implementation do not affect code that uses the object.

**Example 1:**
- `src/main/java/com/amin/battlearena/domain/model/Character.java` — The `Character` class encapsulates fields like `stats`, `abilities`, and `position`, exposing only methods such as `getStats()`, `addAbility()`, and `moveTo(Position)` for controlled access.

**Example 2:**
- `src/main/java/com/amin/battlearena/persistence/PlayerData.java` — The `PlayerData` class encapsulates player-related data (gold, upgrades, settings) and provides methods like `addGold(int amount)` and `getUpgrades()` to interact with this data safely.

Encapsulation helps prevent unintended interference and misuse of object state, making the codebase more robust and easier to maintain. By exposing only necessary methods, the project ensures that objects are used in a controlled and predictable manner.

---

## Information Hiding

Information hiding is the principle of concealing the internal implementation details of a class or module, exposing only what is necessary for other parts of the program to interact with it. This is typically achieved using access modifiers (private, protected, public) and by designing clear interfaces. The goal is to minimize the amount of knowledge that other parts of the system need about a component, so that changes to the implementation do not ripple through the codebase.

In Battle Arena, information hiding is used to create clear boundaries between different parts of the system. By hiding the details of how data is stored or how operations are performed, the project allows developers to change or optimize the implementation without breaking other code. This leads to more robust, modular, and maintainable software, as each component can be understood and modified independently.

**Example 1:**
- `src/main/java/com/amin/battlearena/economy/Wallet.java` — The internal balance is private, and only methods like `add(int amount)` and `getBalance()` are exposed, hiding how the balance is managed internally.

**Example 2:**
- `src/main/java/com/amin/battlearena/engine/core/GameState.java` — The internal maps and lists for characters and players are private, and only methods like `addCharacter(Character)` and `getPlayers()` are public, hiding the actual data structures used.

Information hiding reduces system complexity and increases modularity by minimizing interdependencies between components. It allows developers to change the internal implementation without affecting other parts of the code, as long as the interface remains consistent.

---

## Encapsulation vs. Information Hiding

While encapsulation and information hiding are closely related, they are not the same. Encapsulation is about bundling data and methods together into a single unit, such as a class, to organize code and manage complexity. Information hiding, on the other hand, is about restricting access to the internal details of those units, so that only the necessary aspects are visible to the outside world.

In practice, encapsulation provides the structure for grouping related functionality, while information hiding enforces boundaries that protect the internal state and implementation. For example, a class may encapsulate data and methods, but if all fields are public, it does not achieve information hiding. Conversely, information hiding is achieved by making fields private and exposing only necessary methods, regardless of how the class is structured internally. Both concepts work together to create robust, maintainable, and flexible software.

**Example 1:**
- `src/main/java/com/amin/battlearena/domain/model/Stats.java` — Encapsulates health, attack, defense, etc., and hides them with private fields, exposing only getter/setter methods.

**Example 2:**
- `src/main/java/com/amin/battlearena/domain/items/Consumable.java` — The interface exposes only the `use()` method, hiding the implementation details of each consumable item.

---

## Avoidance of Switch and Nested If (and OOP Principles)

The project deliberately avoids using `switch` statements and deeply nested `if` blocks, as these are procedural constructs that can make code rigid and hard to extend. Switches and nested ifs often lead to code that is difficult to read, maintain, and extend, especially as the number of cases grows. They also centralize decision logic, which can violate the open/closed principle and make it harder to introduce new behaviors without modifying existing code.

Instead, Battle Arena uses object-oriented techniques such as polymorphism, delegation, and the Strategy pattern to handle behavior variations. By distributing logic across specialized classes and interfaces, the project enables new features to be added by creating new classes, rather than modifying existing ones. This approach leads to more modular, extensible, and maintainable code, and is a hallmark of good OOP design.

**Example 1:**
- `src/main/java/com/amin/battlearena/domain/abilities/Ability.java` and its subclasses — Each ability implements its own logic in the `activate()` method, so the engine does not need a switch or if-else chain to determine ability effects.

**Example 2:**
- `src/main/java/com/amin/battlearena/engine/ai/AIStrategy.java` and `SimpleAIStrategy.java` — Different AI behaviors are implemented as separate strategy classes, avoiding switch statements for AI decision-making.

Switches and nested ifs are against OOP because they centralize logic and make it hard to add new behaviors without modifying existing code. OOP encourages distributing behavior across classes, making the system more extensible and maintainable.

---

## Extensibility of the Project

Extensibility refers to how easily a system can be extended with new features or behaviors without modifying existing code. In object-oriented design, extensibility is achieved by designing systems that are open for extension but closed for modification, following the open/closed principle. This allows developers to add new functionality by creating new classes or methods, rather than changing existing code, which reduces the risk of introducing bugs.

The Battle Arena project is highly extensible due to its use of interfaces, abstract classes, and design patterns. By defining clear contracts and separating concerns, the project allows new abilities, items, or behaviors to be added with minimal impact on the rest of the system. This makes it easier to evolve the project over time and adapt to new requirements or gameplay features.

**Example 1:**
- `src/main/java/com/amin/battlearena/domain/abilities/Ability.java` — New abilities can be added by implementing this interface, without changing the engine or other abilities.

**Example 2:**
- `src/main/java/com/amin/battlearena/domain/items/Consumable.java` — New consumable items can be introduced by implementing this interface, and the shop or inventory systems will work with them automatically.

This extensibility allows the project to grow and adapt to new requirements with minimal risk of breaking existing functionality. It also encourages code reuse and modularity.

---

## Handlers and OOP Strength

Handlers in the project (such as UI handlers, animation handlers, and event handlers) encapsulate specific responsibilities, promoting separation of concerns and the single responsibility principle. Each handler is designed to manage a particular aspect of the application, such as rendering the game board, processing user input, or managing upgrades. This clear division of labor makes the codebase easier to understand and maintain.

By isolating responsibilities in dedicated handler classes, Battle Arena ensures that changes in one part of the system do not affect others. Handlers can be developed, tested, and replaced independently, which supports modularity and flexibility. This design also makes it easier to introduce new features or modify existing ones without risking unintended side effects elsewhere in the code.

**Example 1:**
- `src/main/java/com/amin/battlearena/uifx/handler/BoardRenderHandler.java` — Handles all board rendering logic, keeping UI code separate from game logic.

**Example 2:**
- `src/main/java/com/amin/battlearena/uifx/handler/UpgradePurchaseHandler.java` — Manages upgrade purchase logic, decoupling it from UI controllers and shop logic.

Handlers make the project strong in OOP by isolating responsibilities, making the codebase easier to test, maintain, and extend. Each handler can be modified or replaced independently.

---

## Delegators and OOP Strength

Delegators are classes or methods that delegate work to other objects, promoting loose coupling and code reuse. Instead of implementing all logic themselves, delegators rely on other classes to perform specific tasks. This approach allows for greater flexibility, as the behavior of a delegator can be changed by substituting the objects it delegates to.

In Battle Arena, delegators are used to offload responsibilities to specialized classes, making the system more modular and maintainable. Delegation supports the open/closed principle by allowing new behaviors to be introduced through new delegate classes, rather than modifying existing code. It also encourages code reuse, as the same delegate can be used in multiple contexts.

**Example 1:**
- `src/main/java/com/amin/battlearena/uifx/controller/ShopController.java` — Delegates upgrade and consumable purchase logic to `UpgradePurchaseHandler` and `ConsumablePurchaseHandler`.

**Example 2:**
- `src/main/java/com/amin/battlearena/engine/core/GameEngine.java` — Delegates event publishing to `EventPublisher` and turn management to `TurnManager`.

Delegation allows for flexible and maintainable code, as changes in one component do not require changes in the delegator. It also supports the open/closed principle.

---

## Factories and OOP Strength

Factories are used to create objects without specifying the exact class of the object to be created. In object-oriented design, the Factory pattern abstracts the process of object creation, allowing code to work with interfaces or base classes rather than concrete implementations. This promotes loose coupling and makes it easy to introduce new types or change implementations without affecting client code.

In Battle Arena, factories centralize object creation logic, making the system more flexible and extensible. By using factories, the project can support new abilities, items, or characters simply by registering them with the appropriate factory, without changing the code that uses those objects. This design supports scalability and maintainability, especially as the project grows in complexity.

**Example 1:**
- `src/main/java/com/amin/battlearena/domain/abilities/AbilityFactory.java` — Creates ability instances based on a string key, allowing new abilities to be added without changing the factory interface.

**Example 2:**
- `src/main/java/com/amin/battlearena/domain/items/ConsumableFactory.java` — Instantiates consumable items, supporting easy addition of new item types.

Factories make the project strong in OOP by centralizing object creation logic and supporting extensibility and maintainability.

---

## Game UI Implementation

The user interface (UI) of the Battle Arena game is implemented using JavaFX, which provides a modern, event-driven framework for building rich desktop applications. The UI is structured around FXML files (such as `main_menu.fxml`, `game.fxml`, and `shop.fxml` in `src/main/resources/uifx/`), which define the layout and components of each screen. These FXML files are paired with controller classes (like `MainApp.java`, `ShopController.java`, and `CampaignController.java`) that handle user interactions, update the display, and coordinate with the underlying game logic.

A key strength of the UI implementation is its separation of concerns. Visual components and layout are defined declaratively in FXML, while all logic and event handling are managed in dedicated controller and handler classes. For example, `BoardRenderHandler.java` is responsible for rendering the game board and updating the display in response to game state changes, while `UpgradePurchaseHandler.java` manages the logic for purchasing upgrades. This modular approach makes the UI code easier to maintain, test, and extend, as changes to the visual layout or interaction logic can be made independently.

The UI also leverages the observer and event-driven patterns to provide a responsive and interactive experience. User actions such as button clicks, drag-and-drop, or menu selections trigger events that are handled by controllers and handlers, which then update the game state and refresh the UI as needed. Animations, visual effects, and feedback (such as floating damage numbers or health bar updates) are managed by specialized handler classes, ensuring a smooth and engaging user experience. This design not only enhances usability but also aligns with OOP principles by encapsulating UI responsibilities in focused, reusable components.

---

## Other Efficient and Interesting OOP Designs

- **Strategy Pattern:** Used in AI (`AIStrategy`, `SimpleAIStrategy`) and validation (`ActionValidator` and its subclasses) to encapsulate algorithms and allow them to be swapped easily.
  - Example: `src/main/java/com/amin/battlearena/engine/ai/AIStrategy.java`, `src/main/java/com/amin/battlearena/infra/ActionValidator.java`

- **Event-Driven Architecture:** The use of `EventBus` and event classes (`CharacterKilled`, `BattleEnded`) decouples event producers from consumers, making the system more modular.
  - Example: `src/main/java/com/amin/battlearena/domain/events/EventBus.java`, `CharacterKilled.java`

- **Immutability:** Many value objects (like `Upgrade`, `GameMemento.CharacterSnapshot`) are immutable, which improves safety and predictability.
  - Example: `src/main/java/com/amin/battlearena/economy/Upgrade.java`, `engine/memento/GameMemento.java`

- **Single Responsibility Principle:** Most classes have a focused responsibility, such as `ShopService` for shop logic or `PlayerDataManager` for persistence.

These design choices collectively make the Battle Arena project robust, maintainable, and a strong example of object-oriented programming in practice.

---

## SOLID Principles in Battle Arena

The Battle Arena project demonstrates all five SOLID principles of object-oriented design, ensuring maintainability, flexibility, and robustness:

- **Single Responsibility Principle (SRP):** Each class has one clear responsibility. For example, `src/main/java/com/amin/battlearena/uifx/handler/UpgradePurchaseHandler.java` is solely responsible for handling upgrade purchases, keeping UI and business logic separate.

- **Open/Closed Principle (OCP):** The system is open for extension but closed for modification. New abilities can be added by implementing `Ability.java` and registering them in `AbilityFactory.java` without changing existing code.

- **Liskov Substitution Principle (LSP):** Subtypes can be used wherever their base types are expected. For instance, any class implementing `src/main/java/com/amin/battlearena/domain/abilities/Ability.java` can be used in the game engine without special handling.

- **Interface Segregation Principle (ISP):** Interfaces are kept focused and minimal. `src/main/java/com/amin/battlearena/domain/items/Consumable.java` defines only the methods needed for consumable items, so classes only implement what they need.

- **Dependency Inversion Principle (DIP):** High-level modules depend on abstractions, not concrete implementations. For example, the shop and inventory systems interact with the `Consumable` interface, not specific item classes, allowing for easy extension and testing.
