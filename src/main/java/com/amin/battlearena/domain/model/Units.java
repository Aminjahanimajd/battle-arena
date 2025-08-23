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
        return switch (kind) {
            case WARRIOR -> new Warrior(name, pos);
            case MAGE    -> new Mage(name, pos);
            case ARCHER  -> new Archer(name, pos);
            case KNIGHT  -> new Knight(name, pos);
            case RANGER  -> new Ranger(name, pos);
            case MASTER  -> new Master(name, pos);
        };
    }

    public static Character spawn(String typeKey, String name, Position pos) {
        return spawn(Kind.parse(typeKey), name, pos);
    }
}
