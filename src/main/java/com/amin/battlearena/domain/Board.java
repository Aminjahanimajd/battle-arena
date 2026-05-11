package com.amin.battlearena.domain;

import java.util.ArrayList;
import java.util.List;

public final class Board {
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(int x, int y) {
        if (isValid(x, y)) {
            return tiles[x][y];
        }
        return null;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();
        int x = tile.getX();
        int y = tile.getY();
        
        addNeighbor(neighbors, x + 1, y);
        addNeighbor(neighbors, x - 1, y);
        addNeighbor(neighbors, x, y + 1);
        addNeighbor(neighbors, x, y - 1);
        
        return neighbors;
    }

    private void addNeighbor(List<Tile> list, int x, int y) {
        if (isValid(x, y)) {
            list.add(tiles[x][y]);
        }
    }
}
