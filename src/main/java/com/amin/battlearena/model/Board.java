package com.amin.battlearena.model;

import com.amin.battlearena.exceptions.InvalidActionException;
import java.util.HashMap;
import java.util.Map;

// Information hiding: bounds & occupancy managed here.
public final class Board {
    private final int width, height;
    private final Map<Character, Position> locs = new HashMap<>();

    public Board(int width, int height) {
        this.width = width; this.height = height;
    }

    public void place(Character c, Position p) throws InvalidActionException {
        requireInside(p);
        locs.put(c, p);
        c.setPosition(p);
    }

    public boolean isInside(Position p) {
        return p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height;
    }

    public void move(Character c, Position next) throws InvalidActionException {
        if (!isInside(next)) throw new InvalidActionException("Move out of bounds.");
        locs.put(c, next);
        c.setPosition(next);
    }

    private void requireInside(Position p) throws InvalidActionException {
        if (!isInside(p)) throw new InvalidActionException("Position out of bounds: " + p);
    }
}