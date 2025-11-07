# Domain Model & Validation Duplication Analysis
**Date**: November 7, 2025  
**Scope**: Validator/Calculator/State pattern files + UI coupling analysis  
**Related**: Continuation of 4-step UI/backend duplication elimination plan

---

## Executive Summary

**CRITICAL FINDINGS**: 3 major hardcoding/duplication issues discovered:

1. **CRITICAL - DamageCalculator: Hardcoded Character Bonuses** (Priority: HIGH)
   - All 6 character types have hardcoded damage/defense bonuses (+2, +1, +3, etc.)
   - **UNUSED CLASS** - Zero usages found in entire codebase
   - Duplicates legacy bonuses already in balance.json (_legacy_bonuses_deprecated)
   
2. **CRITICAL - AbilityUIHandler: Hardcoded Damage Estimation** (Priority: HIGH)
   - UI layer hardcodes ability damage formulas (manaCost + 10, manaCost + 20, etc.)
   - String-based heuristics instead of querying ability definitions
   - Should use actual ability damage from balance.json or ability classes
   
3. **MEDIUM - State Pattern: Hardcoded Error Messages** (Priority: MEDIUM)
   - DeadState/AliveState have hardcoded error messages
   - No centralized message configuration
   - Minor issue but inconsistent with config-driven approach

**NO ISSUES FOUND** (Already Well-Designed):
- ✅ AliveValidator: Uses character.isAlive() correctly, no hardcoding
- ✅ Stats class: Pure data model, no hardcoded values
- ✅ StatusEffect interface: Pure contract, no implementation
- ✅ CharacterState/CharacterVisitor: Pure patterns, no hardcoding
- ✅ DeadCharacterException: Simple exception, appropriate design

---

## Issue #5: DamageCalculator - Hardcoded Character Class Bonuses

### Problem Analysis

**File**: `src/main/java/com/amin/battlearena/domain/model/DamageCalculator.java`

**Hardcoded Values** (6 character types × 2 bonuses = 12 hardcoded values):
```java
public void visit(Warrior warrior) {
    totalDamage += warrior.getStats().getAttack() + 2;   // ← HARDCODED +2
    totalDefense += warrior.getStats().getDefense() + 1; // ← HARDCODED +1
}

public void visit(Mage mage) {
    totalDamage += mage.getStats().getAttack() + 3;      // ← HARDCODED +3
    totalDefense += mage.getStats().getDefense() + (-1); // ← HARDCODED -1
}

public void visit(Archer archer) {
    totalDamage += archer.getStats().getAttack() + 1;    // ← HARDCODED +1
    totalDefense += archer.getStats().getDefense() + 0;  // ← HARDCODED +0
}

public void visit(Knight knight) {
    totalDamage += knight.getStats().getAttack() + 1;    // ← HARDCODED +1
    totalDefense += knight.getStats().getDefense() + 2;  // ← HARDCODED +2
}

public void visit(Ranger ranger) {
    totalDamage += ranger.getStats().getAttack() + 2;    // ← HARDCODED +2
    totalDefense += ranger.getStats().getDefense() + 0;  // ← HARDCODED +0
}

public void visit(Master master) {
    totalDamage += master.getStats().getAttack() + 4;    // ← HARDCODED +4
    totalDefense += master.getStats().getDefense() + 1;  // ← HARDCODED +1
}
```

**Bonus Table** (Hardcoded in DamageCalculator):
| Character | Damage Bonus | Defense Bonus |
|-----------|--------------|---------------|
| Warrior   | +2           | +1            |
| Mage      | +3           | -1            |
| Archer    | +1           | +0            |
| Knight    | +1           | +2            |
| Ranger    | +2           | +0            |
| Master    | +4           | +1            |

**Critical Discovery**: These bonuses ALREADY EXIST in `balance.json`:
```json
"_legacy_bonuses_deprecated": {
  "warrior.damageBonus": 2,
  "warrior.defenseBonus": 1,
  "archer.damageBonus": 1,
  "archer.defenseBonus": 0,
  "mage.damageBonus": 3,
  "mage.defenseBonus": -1,
  "knight.damageBonus": 1,
  "knight.defenseBonus": 2,
  "ranger.damageBonus": 2,
  "ranger.defenseBonus": 0,
  "master.damageBonus": 4,
  "master.defenseBonus": 1
}
```

**Usage Analysis**: 
```bash
grep -r "new DamageCalculator" → NO RESULTS
grep -r "DamageCalculator" → ONLY the class definition itself
```
**Verdict**: **DEAD CODE** - Class is completely unused throughout the codebase!

### Root Cause
- Visitor pattern implementation created but never integrated
- Duplicate of balance.json legacy bonuses
- Class exists but has zero callers
- Violates single source of truth principle

### Impact
- **Code Bloat**: 72 lines of unused code
- **Maintenance Burden**: Developers might use this class thinking it's active
- **Duplication**: Same bonuses exist in balance.json
- **Confusion**: Why have a DamageCalculator if it's never used?

### Solution Options

**OPTION A: DELETE THE CLASS** (RECOMMENDED)
- Remove entirely since it has zero usages
- Bonuses already in balance.json if ever needed
- Simplest solution: delete dead code

**OPTION B: Migrate to Balance.json + Activate**
- Move bonuses from hardcoded to balance.json "characterBonuses" section
- Extend CharacterBalanceConfig with getBonuses(characterType) API
- Update DamageCalculator to query config
- Integrate into GameEngine or combat calculations
- **Problem**: Requires finding WHERE to use this class (currently unused)

**OPTION C: Keep Legacy Section**
- Leave as-is in `_legacy_bonuses_deprecated`
- Delete DamageCalculator class
- Document why bonuses are deprecated

**RECOMMENDATION**: **OPTION A - Delete DamageCalculator.java**
- Class has zero usages (verified via grep search)
- Bonuses preserved in balance.json legacy section if ever needed
- Follows YAGNI principle (You Aren't Gonna Need It)
- Can recreate from git history if needed later

---

## Issue #6: AbilityUIHandler - Hardcoded Damage Estimation Formulas

### Problem Analysis

**File**: `src/main/java/com/amin/battlearena/uifx/handler/AbilityUIHandler.java`

**Hardcoded Damage Estimation Logic** (Lines 160-181):
```java
if (name.contains("heal")) {
    return String.valueOf(manaCost * 2); // ← HARDCODED 2x multiplier
} else if (name.contains("burst") || name.contains("arcane")) {
    return String.valueOf(manaCost + 10); // ← HARDCODED +10
} else if (name.contains("strike") || name.contains("power")) {
    return String.valueOf(manaCost + 5); // ← HARDCODED +5
} else if (name.contains("master")) {
    return String.valueOf(manaCost + 20); // ← HARDCODED +20
} else if (name.contains("charge")) {
    return String.valueOf(manaCost + 8); // ← HARDCODED +8
} else if (name.contains("shot") || name.contains("arrow")) {
    return String.valueOf(manaCost + 6); // ← HARDCODED +6
}

return String.valueOf(manaCost + 3); // ← HARDCODED +3 (default)
```

**Hardcoded Damage Formula Table**:
| Ability Type           | Formula         | Comment                        |
|------------------------|-----------------|--------------------------------|
| Healing abilities      | manaCost × 2    | "2 HP per mana"                |
| Arcane/Burst (Mage)    | manaCost + 10   | "Magic abilities high damage"  |
| Strike/Power (Warrior) | manaCost + 5    | "Physical reliable"            |
| Master abilities       | manaCost + 20   | "Master devastating"           |
| Charge (Knight)        | manaCost + 8    | "Damage plus utility"          |
| Shot/Arrow (Archer)    | manaCost + 6    | "Ranged precision"             |
| Default                | manaCost + 3    | Fallback                       |

### Root Cause
- **UI layer duplicates game logic** - Damage calculations belong in domain/abilities
- **String-based heuristics** - Uses `name.contains()` instead of ability type system
- **Inconsistent with actual ability damage** - These are guesses, not real damage values
- **No single source of truth** - Damage formulas scattered in UI instead of balance.json

### Actual Ability Damage Sources

**PowerStrike.java** (Warrior):
```java
int baseDamage = caster.getStats().getAttack(); // Uses actual attack stat
int bonusDamage = 10; // ← HARDCODED bonus in ability class
int totalDamage = baseDamage + bonusDamage;
```

**MasterStrike.java** (Master):
```java
int baseDamage = caster.getStats().getAttack();
int totalDamage = baseDamage * 2; // ← HARDCODED 2x multiplier in ability class
```

**ArcaneBurst.java** (Mage):
```java
int damage = caster.getStats().getAttack() + 15; // ← HARDCODED +15 in ability class
```

**Problem**: UI guesses damage (manaCost + 10) while actual damage is in ability classes (attack + 15)

### Impact
- **Incorrect damage display** - UI shows estimates, not actual damage
- **Logic duplication** - Damage formulas exist in both UI and ability classes
- **Maintenance burden** - Must update UI when ability damage changes
- **Misleading to players** - Tooltip shows wrong damage values

### Solution

**Migrate to Config-Driven Damage Display**:

1. **Add damage info to balance.json abilities**:
```json
"abilities": {
  "PowerStrike": {
    "name": "Power Strike",
    "description": "A powerful melee attack that deals bonus damage",
    "cooldown": 3,
    "manaCost": 15,
    "range": 2,
    "damageFormula": "attack + 10",  // ← NEW: For UI display
    "damageType": "physical"          // ← NEW: For categorization
  },
  "MasterStrike": {
    "name": "Master Strike",
    "description": "A devastating attack that deals massive damage",
    "cooldown": 8,
    "manaCost": 50,
    "range": 2,
    "damageFormula": "attack * 2",    // ← NEW
    "damageType": "physical"
  }
}
```

2. **Extend CharacterBalanceConfig.AbilityConfig**:
```java
public static class AbilityConfig {
    private final String name;
    private final String description;
    private final int cooldown;
    private final int manaCost;
    private final int range;
    private final String damageFormula; // ← NEW
    private final String damageType;    // ← NEW
    
    public String getDamageFormula() { return damageFormula; }
    public String getDamageType() { return damageType; }
}
```

3. **Update AbilityUIHandler to query config**:
```java
// BEFORE (hardcoded guesses):
if (name.contains("master")) {
    return String.valueOf(manaCost + 20);
}

// AFTER (query balance.json):
AbilityConfig config = CharacterBalanceConfig.getInstance().getAbilityConfig(abilityId);
return config.getDamageFormula(); // Returns "attack * 2" or "attack + 10"
```

4. **Benefits**:
- ✅ Single source of truth: balance.json
- ✅ Accurate damage display: Matches actual ability formulas
- ✅ Easy balancing: Change JSON, UI updates automatically
- ✅ Consistent pattern: Same as Issues #1-4

---

## Issue #7: State Pattern - Hardcoded Error Messages

### Problem Analysis

**Files**: 
- `src/main/java/com/amin/battlearena/domain/model/DeadState.java`
- `src/main/java/com/amin/battlearena/domain/model/AliveState.java`

**Hardcoded Error Messages**:
```java
// DeadState.java
throw new InvalidActionException(
    (character != null ? character.getName() : "Character") 
    + " is dead and cannot attack"); // ← HARDCODED MESSAGE

throw new InvalidActionException(
    (character != null ? character.getName() : "Character") 
    + " is dead and cannot move");   // ← HARDCODED MESSAGE

public String getStateDescription() {
    return "Character is dead and cannot perform any actions"; // ← HARDCODED
}

// AliveState.java
throw new InvalidActionException("Character and target cannot be null"); // ← HARDCODED
public String getStateDescription() {
    return "Character is alive and can perform all actions"; // ← HARDCODED
}
```

### Impact
- **Minor issue** - Error messages are UI-facing strings
- **No localization support** - Can't translate to other languages
- **No customization** - Can't change messages without recompiling

### Solution Options

**OPTION A: Leave as-is** (RECOMMENDED for now)
- Low priority - error messages are rarely changed
- State pattern implementation is otherwise clean
- Focus on higher-priority issues (#5, #6) first

**OPTION B: Extract to Messages.properties**
- Create `src/main/resources/messages.properties`
- Move all error messages to resource bundle
- Enable i18n (internationalization) support
- **Trade-off**: More complexity for minimal benefit

**RECOMMENDATION**: **OPTION A - Leave as-is**
- Error messages are stable and rarely change
- State pattern implementation is already clean
- Focus engineering effort on Issues #5 and #6

---

## Well-Designed Components (No Changes Needed)

### ✅ AliveValidator
**Status**: **GOOD DESIGN** - No hardcoding, proper delegation

```java
public boolean validate(Character actor, Character target, GameEngine engine) {
    if (!actor.isAlive()) { // ← Queries character state, no hardcoding
        if (engine != null) engine.log("Actor " + actor.getName() + " is dead and cannot act");
        return false;
    }
    return validateNext(actor, target, engine);
}
```

**Why it's good**:
- Uses `character.isAlive()` instead of hardcoded checks
- Chain of Responsibility pattern properly implemented
- No hardcoded logic, delegates to character domain model

---

### ✅ Stats Class
**Status**: **GOOD DESIGN** - Pure data model

```java
public final class Stats {
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private double critChance = 0.0;   // ← Default value is acceptable
    private double critMultiplier = 1.5; // ← Default value is acceptable
    
    // Only default values for new instances, no game balance logic
}
```

**Why it's good**:
- Pure value object with controlled mutators
- No hardcoded game balance (default crit multiplier 1.5 is a reasonable default)
- Validation in setters (min/max bounds)
- Immutable critical stats through controlled APIs

**Note**: `critChance = 0.0` and `critMultiplier = 1.5` are **initialization defaults**, not game balance configs. These are overridden when characters are created from balance.json.

---

### ✅ StatusEffect Interface
**Status**: **GOOD DESIGN** - Pure contract, no implementation

```java
public interface StatusEffect {
    String name();
    int remainingTurns();
    void onTurnStart(Character character);
    void onTurnEnd(Character character);
    int modifyOutgoingDamage(int baseDamage);
    int modifyIncomingDamage(int incomingDamage);
    StatusEffect tick();
    boolean isExpired();
}
```

**Why it's good**:
- Pure interface, no hardcoded values
- Strategy pattern for status effects
- Implementations will contain specific logic (not the interface)

---

### ✅ CharacterState & CharacterVisitor Interfaces
**Status**: **GOOD DESIGN** - Pure pattern implementations

**CharacterState**:
```java
public interface CharacterState {
    void attack(Character character, Character target) throws InvalidActionException;
    void move(Character character, Position newPosition) throws InvalidActionException;
    boolean canAct();
    String getStateName();
    String getStateDescription();
}
```

**CharacterVisitor**:
```java
public interface CharacterVisitor {
    void visit(Warrior warrior);
    void visit(Archer archer);
    void visit(Mage mage);
    void visit(Knight knight);
    void visit(Ranger ranger);
    void visit(Master master);
}
```

**Why they're good**:
- Classic design patterns correctly implemented
- State pattern for character behavior (Alive/Dead states)
- Visitor pattern for double-dispatch operations
- No hardcoded logic in interfaces (implementations may have issues - see DamageCalculator)

---

### ✅ DeadCharacterException
**Status**: **GOOD DESIGN** - Standard exception

```java
public class DeadCharacterException extends Exception {
    public DeadCharacterException() { super(); }
    public DeadCharacterException(String message) { super(message); }
    public DeadCharacterException(String message, Throwable cause) { super(message, cause); }
}
```

**Why it's good**:
- Standard exception with constructors
- No hardcoded logic
- Properly extends Exception
- Simple and appropriate for domain events

---

## Priority Ranking

| Priority | Issue | Severity | Effort | Impact | Recommendation |
|----------|-------|----------|--------|--------|----------------|
| **1** | #5: DamageCalculator Dead Code | HIGH | LOW | HIGH | **DELETE CLASS** - Zero usages, duplicate of balance.json |
| **2** | #6: AbilityUIHandler Hardcoded Damage | HIGH | MEDIUM | HIGH | **Migrate to balance.json** - Add damageFormula field |
| **3** | #7: State Error Messages | LOW | LOW | LOW | **Leave as-is** - Low priority, stable code |

---

## Recommended Action Plan

### Phase 1: Delete Dead Code (Issue #5)
**Effort**: 5 minutes

1. Delete `src/main/java/com/amin/battlearena/domain/model/DamageCalculator.java`
2. Verify build passes: `mvn clean compile`
3. Git commit: "refactor: delete unused DamageCalculator class"
4. **Rationale**: Class has zero usages, bonuses already in balance.json

### Phase 2: Ability Damage Config Migration (Issue #6)
**Effort**: 30-45 minutes

1. **Add to balance.json**:
   - Add `damageFormula` field to each ability
   - Add `damageType` field (physical/magical/utility)
   - Document formula syntax (e.g., "attack + 10", "attack * 2")

2. **Extend CharacterBalanceConfig.AbilityConfig**:
   - Add `String damageFormula` field
   - Add `String damageType` field
   - Update parser to load new fields
   - Add getter methods

3. **Update AbilityUIHandler**:
   - Replace hardcoded `if (name.contains())` logic
   - Query `config.getDamageFormula()` instead
   - Display formula or calculate estimate if character provided

4. **Verify**:
   - Build: `mvn clean compile`
   - Manual test: Check ability tooltips show correct damage
   - Git commit: "refactor: migrate ability damage formulas to balance.json"

### Phase 3: Optional - State Messages (Issue #7)
**Effort**: 15 minutes (if needed)

- Consider only if localization becomes a requirement
- Low priority for now

---

## Connection Analysis: UI ↔ Domain Model

### Current UI-Backend Connections

**GOOD Connections** (Use domain model correctly):
```
GameController → Character.isAlive() ✅
TurnFlowHandler → Character.isAlive() ✅
CombatActionHandler → Character.isAlive() ✅
AliveValidator → Character.isAlive() ✅
```
These all delegate to domain model, no hardcoding.

**BAD Connections** (Duplicate logic in UI):
```
AbilityUIHandler → hardcoded damage formulas ❌ (Issue #6)
  Should query: AbilityConfig.getDamageFormula()
  
AbilityUIHandler → hardcoded ability categorization ❌
  Uses: name.contains("strike")
  Should use: AbilityConfig.getDamageType()
```

**DEAD Code** (No connections):
```
DamageCalculator → UNUSED ❌ (Issue #5)
  Zero usages in entire codebase
  Should: DELETE
```

---

## Comparison with Previous 4 Issues

| Issue | Type | Hardcoded Values | Solution | Status |
|-------|------|------------------|----------|--------|
| #1 | Shop Upgrades | 8 upgrades × prices | balance.json shop section | ✅ DONE (1bfb128) |
| #2 | Consumable Prices | 3 consumables × prices | balance.json consumables | ✅ DONE (452f9cf) |
| #3 | Character Stats | 6 characters × 10 stats | balance.json characters | ✅ DONE (5838820) |
| #4 | Ability Cooldowns | 7 abilities × 3 stats | balance.json abilities | ✅ DONE (9c8910b) |
| **#5** | **Character Bonuses** | **6 characters × 2 bonuses** | **DELETE (unused)** | **⏳ PENDING** |
| **#6** | **Ability Damage UI** | **7 damage formulas** | **balance.json formulas** | **⏳ PENDING** |
| #7 | State Messages | 4 error messages | Leave as-is | ⏳ LOW PRIORITY |

---

## Files Analyzed

### ✅ Domain Model (Checked)
- `AliveState.java` - State pattern, minor hardcoded messages
- `CharacterState.java` - Pure interface ✅
- `CharacterVisitor.java` - Pure interface ✅
- `DamageCalculator.java` - **UNUSED, hardcoded bonuses** ❌
- `DeadState.java` - State pattern, minor hardcoded messages
- `Stats.java` - Pure data model ✅
- `StatusEffect.java` - Pure interface ✅

### ✅ Infrastructure (Checked)
- `AliveValidator.java` - Good design, delegates to domain ✅
- `DeadCharacterException.java` - Standard exception ✅

### ✅ UI Handlers (Checked)
- `AbilityUIHandler.java` - **Hardcoded damage formulas** ❌

### ✅ Engine (Checked)
- `GameEngine.java` - Clean, delegates to domain ✅

---

## Conclusion

**Summary**: 2 actionable issues found (Issues #5 and #6), 5 components well-designed.

**Next Steps**:
1. **DELETE** `DamageCalculator.java` (dead code)
2. **MIGRATE** AbilityUIHandler damage formulas to balance.json
3. **SKIP** state error messages (low priority)

**Pattern Consistency**: Issues #5 and #6 follow the same pattern as Issues #1-4:
- Identify hardcoded values
- Migrate to balance.json
- Extend config loader
- Update classes to query config
- Verify build
- Commit with detailed message

**Total Session Progress** (if Issues #5-6 completed):
- 6 major refactorings across codebase
- ~3,000+ lines of improved code
- Complete config-driven game balance system
- Single source of truth: balance.json

---

**End of Analysis**
