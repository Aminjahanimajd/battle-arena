package com.amin.battlearena.domain.campaign;

public final class LevelData {
    private final String[] types;
    private final float multiplier;
    private final Reward reward;
    
    public LevelData(String[] types, float multiplier, Reward reward) {
        this.types = types;
        this.multiplier = multiplier;
        this.reward = reward;
    }
    
    public String[] getTypes() {
        return types;
    }
    
    public float getMultiplier() {
        return multiplier;
    }
    
    public Reward getReward() {
        return reward;
    }
}
