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
        int effective = Math.max(0, amount);
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

    @Override
    public String toString() {
        return String.format("%s[hp=%d/%d atk=%d def=%d pos=%s mana=%d/%d]", name,
                stats.getHp(), stats.getMaxHp(), stats.getAttack(), stats.getDefense(), 
                position, currentMana, maxMana);
    }
}
