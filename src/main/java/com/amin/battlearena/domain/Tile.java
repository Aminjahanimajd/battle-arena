package com.amin.battlearena.domain;

import com.amin.battlearena.domain.character.Character;

// Represents a board tile
public final class Tile {
    private final int x;
    private final int y;
    private Character occupant;

    // Tile constructor
    public Tile(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.occupant = null;
    }

    // Returns X coordinate
    public final int getX() { return x; }

    // Returns Y coordinate
    public final int getY() { return y; }

    // Returns occupied status
    public final boolean isOccupied() { return occupant != null; }

    // Returns the occupant character
    public final Character getOccupant() { return occupant; }

    // Sets the occupant character
    public final void setOccupant(final Character occupant) { this.occupant = occupant; }
}
