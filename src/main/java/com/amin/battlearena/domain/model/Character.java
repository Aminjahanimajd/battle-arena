package com.amin.battlearena.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;

/**
 * Core abstract character model.
 *
 * Design notes:
 * - Uses composition (Stats) for numeric state.
 * - Exposes a tight public API: move, attack, takeDamage, abilities, temporary buffs.
 * - Provides both baseDamage() and getBaseDamage() for compatibility with subclasses.
 * - Includes mana system for ability usage.
 */
public abstract class Character {

    private final String name;
    private final Stats stats;
    private Position position;
    private final List<Ability> abilities = new ArrayList<>();

    // Mana system
    private int currentMana;
    private final int maxMana;
    private final int manaRegenPerTurn;
    private final int startingMana;

    // transient/temporary defensive buff (reset each turn)
    private int temporaryDefense = 0;

    // transient/temporary evasion chance for this turn (0.0 - 1.0)
    private double temporaryEvasion = 0.0;
    
    // State pattern implementation
    private CharacterState currentState = AliveState.getInstance();
    
    // Caching for expensive calculations
    private transient Integer cachedDamage;
    private transient Position lastPosition;
    private final List<StatusEffect> statusEffects = new ArrayList<>();

    protected Character(String name, Stats stats, Position pos, int maxMana, int manaRegenPerTurn, int startingMana) {
        this.name = Objects.requireNonNull(name);
        this.stats = Objects.requireNonNull(stats);
        this.position = pos;
        this.maxMana = Math.max(0, maxMana);
        this.manaRegenPerTurn = Math.max(0, manaRegenPerTurn);
        this.startingMana = Math.min(startingMana, maxMana);
        this.currentMana = this.startingMana;
    }

    public String getName() { return name; }
    /**
     * Expose stats for engine integration. Returning direct reference is acceptable
     * as Stats is an encapsulated value holder with controlled mutators.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP")
    public Stats getStats() { return stats; }
    public Position getPosition() { return position; }
    public void setPosition(Position p) { 
        // Direct position setting without state validation to avoid recursion
        this.position = p;
    }
    
    /**
     * Validate and set position using state pattern.
     * This method should be used for external movement validation.
     */
    public void moveTo(Position p) throws InvalidActionException {
        // Validate move through state pattern
        currentState.move(this, p);
        this.position = p;
    }

    // Mana system methods
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return maxMana; }
    public int getManaRegenPerTurn() { return manaRegenPerTurn; }
    public int getStartingMana() { return startingMana; }
    
    public boolean canSpendMana(int amount) { return currentMana >= amount; }
    
    public boolean spendMana(int amount) {
        if (amount <= 0 || !canSpendMana(amount)) return false;
        currentMana -= amount;
        return true;
    }
    
    public void restoreMana(int amount) {
        if (amount <= 0) return;
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    public int getTemporaryDefense() { return temporaryDefense; }
    public void addTemporaryDefense(int amount) { this.temporaryDefense += Math.max(0, amount); }
    public void clearTemporaryDefense() { this.temporaryDefense = 0; }

    /** Evasion: turn-limited chance to fully avoid incoming damage (0.0 - 1.0) */
    public double getTemporaryEvasion() { return temporaryEvasion; }
    public void addTemporaryEvasion(double frac) {
        if (frac <= 0.0) return;
        this.temporaryEvasion = Math.min(1.0, this.temporaryEvasion + frac);
    }
    public void clearTemporaryEvasion() { this.temporaryEvasion = 0.0; }

    public void addAbility(Ability a) { if (a != null) abilities.add(a); }
    public List<Ability> getAbilities() { return List.copyOf(abilities); }

    public boolean isAlive() { 
        boolean alive = stats.getHp() > 0;
        // Update state based on alive status
        if (alive && currentState instanceof DeadState) {
            currentState = AliveState.getInstance();
        } else if (!alive && currentState instanceof AliveState) {
            currentState = DeadState.getInstance();
        }
        return alive; 
    }
    
    /**
     * Check if the character can perform actions in their current state.
     */
    public boolean canAct() {
        return currentState.canAct();
    }
    
    /**
     * Get the current state of the character.
     */
    public CharacterState getCurrentState() {
        return currentState;
    }

    /**
     * Backwards-compatible accessor used widely in subclasses.
     * Subclasses can override baseDamage() to customize.
     */
    public int baseDamage() {
        return getBaseDamage();
    }

    /**
     * Modern accessor name. Subclasses may override this instead,
     * but for maximum compatibility both methods are provided.
     */
    public int getBaseDamage() { 
        // Use cached value if available and position hasn't changed
        if (cachedDamage != null && lastPosition != null && lastPosition.equals(position)) {
            return cachedDamage;
        }
        
        // Calculate and cache
        int damage = calculateBaseDamage();
        for (StatusEffect se : statusEffects) {
            damage = se.modifyOutgoingDamage(damage);
        }
        cachedDamage = damage;
        lastPosition = position;
        return damage;
    }
    
    /**
     * Calculate the base damage for this character.
     * Subclasses should override this method.
     */
    protected int calculateBaseDamage() {
        return 0; // Default implementation
    }

    /**
     * Apply damage to this Character. Implementations should use Stats and
     * throw DeadCharacterException when the character dies as a result.
     */
    public void takeDamage(int amount) throws DeadCharacterException {
        int modified = amount;
        for (StatusEffect se : statusEffects) {
            modified = se.modifyIncomingDamage(modified);
        }
        int effective = Math.max(0, modified);
        int before = stats.getHp();
        int after = Math.max(0, before - effective);
        stats.setHp(after);
        if (after == 0) throw new DeadCharacterException(name + " has been slain.");
    }

    /**
     * Called at end of character's turn to count down cooldowns and clear per-turn temporary buffs.
     * Keeping this method here centralizes per-character housekeeping.
     */
    public void endTurnHousekeeping() {
        for (StatusEffect se : List.copyOf(statusEffects)) se.onTurnEnd(this);
        statusEffects.replaceAll(StatusEffect::tick);
        statusEffects.removeIf(StatusEffect::isExpired);
        clearTemporaryDefense();
        clearTemporaryEvasion();
        // Regenerate mana
        restoreMana(manaRegenPerTurn);
        // Reduce cooldowns
        for (Ability a : abilities) a.reduceCooldown();
        // Invalidate cache since turn has changed
        invalidateCache();
    }
    
    /**
     * Invalidate the damage cache.
     */
    public void invalidateCache() {
        cachedDamage = null;
        lastPosition = null;
    }

    public void addStatusEffect(StatusEffect effect) { if (effect != null) statusEffects.add(effect); }
    public List<StatusEffect> getStatusEffects() { return List.copyOf(statusEffects); }

    @Override
    public String toString() {
        return String.format("%s[hp=%d/%d atk=%d def=%d pos=%s mana=%d/%d]", name,
                stats.getHp(), stats.getMaxHp(), stats.getAttack(), stats.getDefense(), 
                position, currentMana, maxMana);
    }

    /**
     * Fluent builder for creating Character subclasses with optional abilities.
     */
    public static class Builder {
        private String name;
        private Position position;
        private final java.util.List<com.amin.battlearena.domain.abilities.Ability> abilities = new java.util.ArrayList<>();

        public Builder name(String name) {
            this.name = java.util.Objects.requireNonNull(name, "Name cannot be null");
            return this;
        }

        public Builder position(Position position) {
            this.position = java.util.Objects.requireNonNull(position, "Position cannot be null");
            return this;
        }

        public Builder addAbility(com.amin.battlearena.domain.abilities.Ability ability) {
            if (ability != null) abilities.add(ability);
            return this;
        }

        public Builder abilities(java.util.List<com.amin.battlearena.domain.abilities.Ability> abilities) {
            this.abilities.clear();
            if (abilities != null) this.abilities.addAll(abilities);
            return this;
        }

        public com.amin.battlearena.domain.model.Warrior buildWarrior() {
            requireBasics();
            var w = new com.amin.battlearena.domain.model.Warrior(name, position);
            abilities.forEach(w::addAbility);
            return w;
        }

        public com.amin.battlearena.domain.model.Archer buildArcher() {
            requireBasics();
            var a = new com.amin.battlearena.domain.model.Archer(name, position);
            abilities.forEach(a::addAbility);
            return a;
        }

        public com.amin.battlearena.domain.model.Mage buildMage() {
            requireBasics();
            var m = new com.amin.battlearena.domain.model.Mage(name, position);
            abilities.forEach(m::addAbility);
            return m;
        }

        public com.amin.battlearena.domain.model.Knight buildKnight() {
            requireBasics();
            var k = new com.amin.battlearena.domain.model.Knight(name, position);
            abilities.forEach(k::addAbility);
            return k;
        }

        public com.amin.battlearena.domain.model.Ranger buildRanger() {
            requireBasics();
            var r = new com.amin.battlearena.domain.model.Ranger(name, position);
            abilities.forEach(r::addAbility);
            return r;
        }

        public Builder reset() {
            this.name = null;
            this.position = null;
            this.abilities.clear();
            return this;
        }

        public static Builder create() { return new Builder(); }

        private void requireBasics() {
            if (name == null) throw new IllegalStateException("Name must be set");
            if (position == null) throw new IllegalStateException("Position must be set");
        }
    }
}
