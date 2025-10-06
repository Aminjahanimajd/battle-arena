package com.amin.battlearena.engine.random;

public interface RandomProvider {
    int nextInt(int minInclusive, int maxInclusive);
    double nextDouble();
}


