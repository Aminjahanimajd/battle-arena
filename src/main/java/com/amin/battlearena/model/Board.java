package com.amin.battlearena.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Lightweight Board utility for grid bounds and occupancy checks.
 *
 * Responsibilities:
 * - Know board dimensions.
 * - Validate positions (within bounds).
 * - Provide occupancy checks against a collection of characters (players' teams).
 *
 * Note: This class intentionally does not mutate Character positions directly.
 * Use GameEngine.move(...) which will consult Board when needed before calling character.moveTo(...).
 */
public final class Board {
    private final int width;
    private final int height;

    public Board(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Board dimensions must be positive");
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isWithinBounds(Position p) {
        Objects.requireNonNull(p);
        return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
    }

    /**
     * Check whether any alive character in the provided collection occupies the given position.
     */
    public boolean isPositionOccupied(Position p, Collection<Character> characters) {
        Objects.requireNonNull(p);
        if (characters == null || characters.isEmpty()) return false;
        return characters.stream()
                .filter(Objects::nonNull)
                .filter(Character::isAlive)
                .anyMatch(c -> p.equals(c.getPosition()));
    }

    /**
     * Find a character located at the given position among the provided collection.
     */
    public Optional<Character> getCharacterAt(Position p, Collection<Character> characters) {
        Objects.requireNonNull(p);
        if (characters == null || characters.isEmpty()) return Optional.empty();
        return characters.stream()
                .filter(Objects::nonNull)
                .filter(Character::isAlive)
                .filter(c -> p.equals(c.getPosition()))
                .findFirst();
    }

    @Override
    public String toString() {
        return "Board{" + width + "x" + height + "}";
    }
}
