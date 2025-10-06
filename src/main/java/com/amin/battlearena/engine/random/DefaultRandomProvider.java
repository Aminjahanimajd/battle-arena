package com.amin.battlearena.engine.random;

import java.util.Random;

import com.amin.battlearena.persistence.Config;

public final class DefaultRandomProvider implements RandomProvider {
    private final Random random = createRandom();

    public DefaultRandomProvider() {}

    private static Random createRandom() {
        String seedStr = Config.get("battlearena.seed", "BATTLE_ARENA_SEED", "");
        if (seedStr != null && !seedStr.isBlank()) {
            try {
                long seed = Long.parseLong(seedStr.trim());
                return new Random(seed);
            } catch (NumberFormatException ignored) { /* fall through */ }
        }
        return new Random();
    }

    @Override
    public int nextInt(int minInclusive, int maxInclusive) {
        if (maxInclusive < minInclusive) throw new IllegalArgumentException("max < min");
        int bound = (maxInclusive - minInclusive) + 1;
        return minInclusive + random.nextInt(bound);
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}


