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
battle-arena/
├── pom.xml # Maven configuration
├── src/
│ ├── main/java/com/amin/battlearena/
│ │ ├── Game.java # Entry point
│ │ ├── model/ # Entities: Hero, Monster, Unit, Arena
│ │ ├── service/ # Battle logic, turn management
│ │ ├── ui/ # Console UI (later: GUI)
│ │ └── util/ # Helpers, factories
│ └── test/java/... # Unit tests
├── .gitignore
└── README.md

yaml
Copy
Edit

---

## 🚀 Getting Started

### 1. Clone repository
```bash
git clone https://github.com/USERNAME/battle-arena.git
cd battle-arena
2. Build
bash
Copy
Edit
mvn clean compile
3. Run
bash
Copy
Edit
mvn exec:java -Dexec.mainClass="com.amin.battlearena.Game"
📌 Roadmap
 Initial project skeleton

 Core game loop

 Hero vs Monster combat

 Turn manager & battle rules

 Expand with skills, inventory, and AI

📖 License
MIT License (to be added)

👤 Author
Amin – Data Analysis student aspiring to become a Machine Learning Engineer.
