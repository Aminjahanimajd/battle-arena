package com.amin.battlearena.domain;

import com.amin.battlearena.domain.character.Character;

public final class Tile {
    private final int x;
    private final int y;
    private Character occupant;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Character getOccupant() {
        return occupant;
    }

    public void setOccupant(Character occupant) {
        this.occupant = occupant;
    }

    public boolean isOccupied() {
        return occupant != null;
    }
}
