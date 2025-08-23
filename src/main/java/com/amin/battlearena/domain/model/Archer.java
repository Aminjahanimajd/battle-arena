package com.amin.battlearena.domain.model;

import com.amin.battlearena.domain.abilities.DoubleShot;

/**
 * Ranged damage dealer. Has a range field; range checks should be enforced by GameEngine/Board.
 */
public class Archer extends Character {

    private final int range;

    public Archer(String name, Position position) {
        this(name, position, 3); // default range 3
    }

    public Archer(String name, Position position, int range) {
        super(name, new Stats(80, 15, 5, 3), position);
        this.range = Math.max(1, range);
        addAbility(new DoubleShot());
    }

    public int getRange() { return range; }

    public boolean inRangeOf(Character other) {
        return this.getPosition().distanceTo(other.getPosition()) <= range;
    }

    @Override
    public int baseDamage() {
        return 2;
    }
}
