package com.amin.battlearena.model;

// Abstraction of location, encapsulates distance logic (information hiding).
public record Position(int x, int y) {
    public int distanceTo(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public Position stepTowards(Position target) {
        int dx = Integer.compare(target.x(), this.x);
        int dy = Integer.compare(target.y(), this.y);
        if (Math.abs(target.x() - this.x) >= Math.abs(target.y() - this.y)) {
            return new Position(this.x + dx, this.y);
        } else {
            return new Position(this.x, this.y + dy);
        }
    }
}