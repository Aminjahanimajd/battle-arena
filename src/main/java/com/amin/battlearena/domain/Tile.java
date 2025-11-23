package com.amin.battlearena.domain;

import com.amin.battlearena.domain.character.GameCharacter;

public class Tile {
    private final int x;
    private final int y;
    private GameCharacter occupant;

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

    public GameCharacter getOccupant() {
        return occupant;
    }

    public void setOccupant(GameCharacter occupant) {
        this.occupant = occupant;
    }

    public boolean isOccupied() {
        return occupant != null;
    }
}
