# OOP Principles Applied (Summary)

## Modularity — 5 Criteria
1. **Decomposability:** split into engine, model, actions, player, exceptions.
2. **Composability:** components combine via interfaces (Action, Character).
3. **Understandability:** small classes with single responsibilities.
4. **Continuity:** isolated changes (add a new Character) require local edits.
5. **Protection:** errors confined (exceptions; Board encapsulates bounds).

## Modularity — 5 Rules
1. **High Cohesion per module.**
2. **Low coupling via interfaces/abstract base types.**
3. **Information hiding:** private fields, controlled methods.
4. **Separation of concerns:** engine vs model vs UI.
5. **Stable interfaces, variable implementations.**

## SOLID
- **S (SRP):** each class has one responsibility.
- **O (OCP):** add new Character by subclassing; no engine edits.
- **L (LSP):** Character subtypes substitutable where Character is used.
- **I (ISP):** narrow interfaces (Action).
- **D (DIP):** depend on abstractions (Player uses Action; engine on Player).

## Program to Interfaces
- `Action` interface; `Character` abstract; `Player` abstract.

## Reuse Mechanisms
- **Composition over inheritance:** Board owns positions; Team owns characters.
- **Delegation:** AIPlayer delegates to greedy decision logic.
- **Parametrized types:** planned generics (e.g., repositories or lists).

## Parnas Information Hiding
- Internal state hidden (`Stats` private; `Board` bounds/locations private).
- Only necessary operations exposed (move/place/attack with validation).