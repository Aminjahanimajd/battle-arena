package com.amin.battlearena.domain.model;

// Immutable 2D grid position with distance utilities
public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    public int distanceTo(Position other) {
        int dx = other.x - this.x;
        int dy = other.y - this.y;
        double d = Math.hypot(dx, dy);
        return (int) Math.ceil(d);
    }

    // Returns a neighboring Position one step towards target (8-directional)
    public Position stepTowards(Position target) {
        if (target == null) return this;
        int nx = this.x;
        int ny = this.y;
        if (target.x > nx) nx++;
        else if (target.x < nx) nx--;
        if (target.y > ny) ny++;
        else if (target.y < ny) ny--;
        return new Position(nx, ny);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) * 31 + Integer.hashCode(y);
    }
}
