package com.amin.battlearena.domain.campaign;

import java.util.Map;

import com.amin.battlearena.domain.account.Player;

public final class RewardService {
    
    public void grantLevelReward(Player player, int level) {
        if (player == null || !LevelConfig.isLevelValid(level)) {
            return;
        }
        
        Reward reward = LevelConfig.getReward(level);
        
        // Grant gold
        player.addGold(reward.getGoldAmount());
        
        // Grant consumables
        Map<String, Integer> consumables = reward.getConsumables();
        for (Map.Entry<String, Integer> entry : consumables.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            for (int i = 0; i < quantity; i++) {
                player.addItem(itemName);
            }
        }
    }
}
