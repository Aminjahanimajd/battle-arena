package com.amin.battlearena.model;

import com.amin.battlearena.abilities.Ability;
import com.amin.battlearena.exceptions.DeadCharacterException;
import com.amin.battlearena.exceptions.InvalidActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Core abstract character model.
 *
 * Design notes:
 * - Uses composition (Stats) for character numeric state (encapsulation, information hiding).
 * - Exposes a small, well-documented public API (move, attack, takeDamage, abilities).
 * - Defines extension points: subclasses override baseDamage() and may add abilities.
 * - Keeps fields protected only when subclass access is required; otherwise private.
 */
public abstract class Character {

    private final String name;
    private final Stats stats;           // composition: encapsulated numeric state
    private Position position;
    private final List<Ability> abilities = new ArrayList<>();

    // transient/temporary defensive buff (reset each turn or by duration system)
    private int temporaryDefense = 0;

    protected Character(String name, Stats stats, Position position) {
        this.name = Objects.requireNonNull(name, "name");
        this.stats = Objects.requireNonNull(stats, "stats");
        this.position = Objects.requireNonNull(position, "position");
    }

    // --------- Basic accessors ---------
    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public Position getPosition() { return position; }
    protected void setPosition(Position position) { this.position = Objects.requireNonNull(position); }

    public boolean isAlive() { return !stats.isDead(); }

    /**
     * Amount of extra defense provided temporarily (e.g. by abilities).
     * Managed here to keep defense calculation centralized.
     */
    public int getTemporaryDefense() { return temporaryDefense; }
    public void addTemporaryDefense(int amount) { this.temporaryDefense += Math.max(0, amount); }
    public void clearTemporaryDefense() { this.temporaryDefense = 0; }

    // Public accessor to expose subclass baseDamage to other packages (read-only).
    // Keeps protected baseDamage() as the extension point while allowing external callers to read it.
    public int getBaseDamage() {
        return baseDamage();
    }

    // --------- Movement ---------
    /**
     * Move the character to a new position.
     * Subclasses or external engine can validate movement range before calling.
     */
    public void moveTo(Position newPosition) throws InvalidActionException {
        if (!isAlive()) throw new InvalidActionException(name + " is dead and cannot move.");
        if (newPosition == null) throw new InvalidActionException("newPosition is null");
        setPosition(newPosition);
    }

    // --------- Combat ---------
    /**
     * Default attack: calculates damage using attacker's attack + baseDamage,
     * minus target defense (including temporaryDefense).
     *
     * Subclasses can override if they need special attack mechanics.
     */
    public void attack(Character target) throws InvalidActionException, DeadCharacterException {
        if (!isAlive()) throw new InvalidActionException(name + " is dead and cannot attack.");
        if (target == null) throw new InvalidActionException("target is null");
        if (!target.isAlive()) throw new InvalidActionException("target " + target.getName() + " is already dead.");

        int raw = this.stats.getAttack() + baseDamage();
        int effectiveDefense = target.stats.getDefense() + target.getTemporaryDefense();
        int damage = Math.max(0, raw - effectiveDefense);

        target.takeDamage(damage);
    }

    /**
     * Reduces HP by given amount (after defense resolution). Throws DeadCharacterException when killed.
     */
    public void takeDamage(int amount) throws DeadCharacterException {
        if (amount <= 0) return;
        stats.damage(amount);
        if (stats.isDead()) {
            throw new DeadCharacterException(name + " has been slain.");
        }
    }

    /**
     * Subclasses define their damage flavor (scales or fixed).
     */
    protected abstract int baseDamage();

    // --------- Abilities ---------
    public List<Ability> getAbilities() { return List.copyOf(abilities); }

    public void addAbility(Ability ability) {
        if (ability != null) abilities.add(ability);
    }

    /**
     * Called at end of character's turn to count down cooldowns and clear per-turn temporary buffs.
     * Keeping this method here centralizes per-character housekeeping.
     */
    public void endTurnHousekeeping() {
        clearTemporaryDefense(); // simple model; extend to timed statuses later
        for (Ability a : abilities) a.reduceCooldown();
    }

    @Override
    public String toString() {
        return String.format("%s[hp=%d/%d atk=%d def=%d pos=%s]", name,
                stats.getHp(), stats.getMaxHp(), stats.getAttack(), stats.getDefense(), position);
    }
}
