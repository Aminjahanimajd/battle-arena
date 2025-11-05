package com.amin.battlearena.domain.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

// Lightweight Board utility for grid bounds and occupancy checks
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

    public boolean isPositionOccupied(Position p, Collection<Character> characters) {
        Objects.requireNonNull(p);
        if (characters == null || characters.isEmpty()) return false;
        return characters.stream()
                .filter(Objects::nonNull)
                .filter(Character::isAlive)
                .anyMatch(c -> p.equals(c.getPosition()));
    }

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
