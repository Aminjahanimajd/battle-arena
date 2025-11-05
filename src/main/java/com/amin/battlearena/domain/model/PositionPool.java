package com.amin.battlearena.domain.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Object pool for Position objects to reduce garbage collection
public final class PositionPool {
    
    private static final Map<String, Position> pool = new ConcurrentHashMap<>();
    private static final int MAX_POOL_SIZE = 1000;
    
    private PositionPool() {}
    
    public static Position get(int x, int y) {
        String key = x + "," + y;
        
        if (pool.size() > MAX_POOL_SIZE) {
            pool.clear();
        }
        
        return pool.computeIfAbsent(key, k -> new Position(x, y));
    }
    
    public static Position get(Integer x, Integer y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        return get(x.intValue(), y.intValue());
    }
    
    public static int getPoolSize() {
        return pool.size();
    }
    
    public static void clearPool() {
        pool.clear();
    }
    
    public static String getPoolStats() {
        return String.format("PositionPool{size=%d, maxSize=%d}", pool.size(), MAX_POOL_SIZE);
    }
}
