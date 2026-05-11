package com.amin.battlearena.domain.campaign;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Reward {
    private final int goldAmount;
    private final Map<String, Integer> consumables;
    
    public Reward(int goldAmount, Map<String, Integer> consumables) {
        this.goldAmount = goldAmount;
        this.consumables = new HashMap<>(consumables);
    }
    
    public Reward(int goldAmount) {
        this(goldAmount, new HashMap<>());
    }
    
    public int getGoldAmount() {
        return goldAmount;
    }
    
    public Map<String, Integer> getConsumables() {
        return Collections.unmodifiableMap(consumables);
    }
}
