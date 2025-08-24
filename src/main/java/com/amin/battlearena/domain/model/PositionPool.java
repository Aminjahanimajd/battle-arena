package com.amin.battlearena.domain.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Object pool for Position objects to reduce garbage collection pressure.
 * Implements object pooling for frequently created coordinate objects.
 */
public final class PositionPool {
    
    private static final Map<String, Position> pool = new ConcurrentHashMap<>();
    private static final int MAX_POOL_SIZE = 1000; // Limit pool size
    
    private PositionPool() {} // Utility class
    
    /**
     * Get a Position object from the pool or create a new one.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a Position object
     */
    public static Position get(int x, int y) {
        String key = x + "," + y;
        
        // Check if pool is getting too large
        if (pool.size() > MAX_POOL_SIZE) {
            // Clear some entries to prevent memory issues
            pool.clear();
        }
        
        return pool.computeIfAbsent(key, k -> new Position(x, y));
    }
    
    /**
     * Get a Position object from the pool or create a new one.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a Position object
     */
    public static Position get(Integer x, Integer y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        return get(x.intValue(), y.intValue());
    }
    
    /**
     * Get the current pool size.
     * @return the number of positions in the pool
     */
    public static int getPoolSize() {
        return pool.size();
    }
    
    /**
     * Clear the pool.
     */
    public static void clearPool() {
        pool.clear();
    }
    
    /**
     * Get pool statistics.
     * @return a string representation of pool statistics
     */
    public static String getPoolStats() {
        return String.format("PositionPool{size=%d, maxSize=%d}", pool.size(), MAX_POOL_SIZE);
    }
}
