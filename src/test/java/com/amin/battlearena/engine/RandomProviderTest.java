package com.amin.battlearena.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.amin.battlearena.engine.random.DefaultRandomProvider;

public class RandomProviderTest {
    @Test
    void deterministicWithSeed() {
        System.setProperty("battlearena.seed", "42");
        DefaultRandomProvider rp1 = new DefaultRandomProvider();
        DefaultRandomProvider rp2 = new DefaultRandomProvider();
        for (int i = 0; i < 10; i++) {
            assertEquals(rp1.nextInt(0, 10), rp2.nextInt(0, 10));
            assertEquals(rp1.nextDouble(), rp2.nextDouble());
        }
        System.clearProperty("battlearena.seed");
    }
}


