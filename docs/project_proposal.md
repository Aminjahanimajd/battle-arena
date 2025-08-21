# Project Proposal — Battle Arena

**Methodology:** Booch (identify objects, define semantics, define relationships, implement).

## Objects (abstraction level)
- GameEngine, Board, Player (Human/AI), Character hierarchy (Warrior/Archer/Mage/knight , Bosses:Ranger and Master), Actions (Attack, later Defend/Skill), Stats, Position , campaign in 10 levels slowely difficulty increses and bosses at stage 6 and 10 , earn gold from killing in each stage and out of match can go to shop and upgrade with gold to pass harder stages, each character have one special abilities or more ,

## Semantics (behavior)
- Turn loop, move/attack, win condition, AI greedy heuristic , upgrades

## Relationships
- Composition: Team has Characters; Board manages Positions.
- Specialization: Character <- Warrior/Archer/Mage; Player <- Human/AI.
- Delegation: AIPlayer → AI decision logic.

## Implementation
- Java 17, Maven, VS Code, DB included .
- no web APIs. Console initially; JavaFX GUI later.