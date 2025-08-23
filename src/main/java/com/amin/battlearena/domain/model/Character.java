package com.amin.battlearena.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.amin.battlearena.domain.abilities.Ability;
import com.amin.battlearena.infra.DeadCharacterException;

/**
 * Core abstract character model.
 *
 * Design notes:
 * - Uses composition (Stats) for numeric state.
 * - Exposes a tight public API: move, attack, takeDamage, abilities, temporary buffs.
 * - Provides both baseDamage() and getBaseDamage() for compatibility with subclasses.
 */
public abstract class Character {

    private final String name;
    private final Stats stats;
    private Position position;
    private final List<Ability> abilities = new ArrayList<>();

    // transient/temporary defensive buff (reset each turn)
    private int temporaryDefense = 0;

    // transient/temporary evasion chance for this turn (0.0 - 1.0)
    private double temporaryEvasion = 0.0;

    protected Character(String name, Stats stats, Position pos) {
        this.name = Objects.requireNonNull(name);
        this.stats = Objects.requireNonNull(stats);
        this.position = pos;
    }

    public String getName() { return name; }
    public Stats getStats() { return stats; }
    public Position getPosition() { return position; }
    public void setPosition(Position p) { this.position = p; }

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

    public boolean isAlive() { return stats.getHp() > 0; }

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
    public int getBaseDamage() { return 0; } // subclasses may override

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
        for (Ability a : abilities) a.reduceCooldown();
    }

    @Override
    public String toString() {
        return String.format("%s[hp=%d/%d atk=%d def=%d pos=%s]", name,
                stats.getHp(), stats.getMaxHp(), stats.getAttack(), stats.getDefense(), position);
    }
}
