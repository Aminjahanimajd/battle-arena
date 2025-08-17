# Project Proposal — Battle Arena

**Methodology:** Booch (identify objects, define semantics, define relationships, implement).

## Objects (abstraction level)
- GameEngine, Board, Player (Human/AI), Character hierarchy (Warrior/Archer/Mage), Actions (Attack, later Defend/Skill), Stats, Position.

## Semantics (behavior)
- Turn loop, move/attack, win condition, AI greedy heuristic.

## Relationships
- Composition: Team has Characters; Board manages Positions.
- Specialization: Character <- Warrior/Archer/Mage; Player <- Human/AI.
- Delegation: AIPlayer → AI decision logic.

## Implementation
- Java 17, Maven, VS Code.
- No DB, no web APIs. Console initially; JavaFX GUI later.