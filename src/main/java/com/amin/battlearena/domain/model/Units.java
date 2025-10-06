package com.amin.battlearena.domain.model;

import java.util.Locale;

/**
 * Single entry point for spawning characters by type.
 * Replaces the previous factory-based approach (centralized, type-safe).
 */
public final class Units {

    public enum Kind {
        WARRIOR, MAGE, ARCHER, KNIGHT, RANGER, MASTER;

        public static Kind parse(String raw) {
            return Kind.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        }
    }

    private Units() {}

    public static Character spawn(Kind kind, String name, Position pos) {
        if (kind == Kind.WARRIOR) {
            return new Warrior(name, pos);
        } else if (kind == Kind.MAGE) {
            return new Mage(name, pos);
        } else if (kind == Kind.ARCHER) {
            return new Archer(name, pos);
        } else if (kind == Kind.KNIGHT) {
            return new Knight(name, pos);
        } else if (kind == Kind.RANGER) {
            return new Ranger(name, pos);
        } else if (kind == Kind.MASTER) {
            return new Master(name, pos);
        } else {
            throw new IllegalArgumentException("Unknown character kind: " + kind);
        }
    }

    public static Character spawn(String typeKey, String name, Position pos) {
        return spawn(Kind.parse(typeKey), name, pos);
    }
}
