# 🏰 Battle Arena

A **Turn-Based Strategy Game** designed in **Java** using the **Booch Object-Oriented Design Methodology**.  
This project serves both as a learning platform for advanced OOP concepts and as a foundation for building scalable, extensible game architectures.

---

## 🎮 Game Concept
**Battle Arena** is a turn-based combat game where players control heroes, monsters, and units on a grid.  
Each unit has **attributes** (health, attack, defense, agility) and can perform **actions** (attack, defend, use skills).  

Future features will include:
- Multiple heroes with unique abilities ⚔️  
- AI-controlled opponents 🤖  
- Different arenas and maps 🗺️  
- Power-ups and inventory system 🎒  

---

## 🧩 Design Principles
This project emphasizes **clean design** and **software engineering best practices**:

- **Booch OOP Methodology** → modular object-oriented design.  
- **SOLID Principles** → maintainable and extensible code.  
- **Parnas Principle** → information hiding for clean module boundaries.  
- **Reuse Mechanisms** → inheritance, composition, and delegation.  
- **Design Patterns** → Factory, Strategy, Observer (planned).  

---

## 🛠️ Tech Stack
- **Language**: Java 17  
- **Build Tool**: Maven  
- **Version Control**: Git + GitHub  
- **Testing**: JUnit (planned)  

---

## 📂 Project Structure
com/amin/battlearena
├── app/                 # application bootstrap, Main, wiring (DI-lite)
├── engine/              # GameEngine, turn loop, orchestration (no domain rules)
├── domain/
│   ├── model/           # Character, Stats, Position, Board, Units (domain objects)
│   ├── actions/         # Command objects: Action, AttackAction, etc.
│   ├── abilities/       # Ability implementations
│   └── events/          # GameEvent classes + EventBus
├── players/             # Player, HumanPlayer, AIPlayer, strategies
├── progression/         # Level, CampaignMap, PlayerProgress, LevelNode
├── levels/              # LevelService, EnemySpawner, LevelLoader (config)
├── economy/             # Wallet, EconomyManager, ShopService
├── persistence/         # DAO, ProgressService, MigrationUtility
├── ui/                  # CLI, Menu, LevelSelectCLI, ShopCLI
└── infra/               # Logging, utilities, adapters (e.g., JSON helpers)


---

## 🚀 Getting Started

### 1. Clone repository

git clone https://github.com/USERNAME/battle-arena.git
cd battle-arena

### 2. Build
mvn clean compile

### 3. Run
mvn exec:java -Dexec.mainClass="com.amin.battlearena.Game"

---

## 📌 Roadmap
 Initial project skeleton

 Core game loop

 Hero vs Monster combat

 Turn manager & battle rules

 Expand with skills, inventory, and AI

---

## 👤 Author
Amin – Data Analysis student , University of Messina
