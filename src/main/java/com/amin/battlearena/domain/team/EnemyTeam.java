package com.amin.battlearena.domain.team;

import java.util.HashMap;
import java.util.Map;

import com.amin.battlearena.domain.campaign.LevelConfig;
import com.amin.battlearena.domain.character.Character;
import com.amin.battlearena.domain.character.CharacterFactory;

public final class EnemyTeam extends Team {
    private static final int[][] BASE_STATS = {
        {80, 40, 10, 3, 1, 2},     // Warrior
        {60, 50, 12, 2, 2, 3},     // Archer
        {50, 80, 15, 1, 2, 2}      // Mage
    };
    
    private static final Map<String, Integer> TYPE_INDICES = new HashMap<>();
    
    static {
        TYPE_INDICES.put("Warrior", 0);
        TYPE_INDICES.put("Archer", 1);
        TYPE_INDICES.put("Mage", 2);
    }
    
    private int level;
    
    public EnemyTeam(int level) {
        super(false);
        this.level = level;
    }
    
    @Override
    public void initialize() {
        if (!LevelConfig.isLevelValid(level)) return;
        
        String[] types = LevelConfig.getEnemyTypes(level);
        float multiplier = LevelConfig.getDifficultyMultiplier(level);
        
        for (String type : types) {
            addMember(createEnemyCharacter(type, multiplier));
        }
    }
    
    private Character createEnemyCharacter(String type, float multiplier) {
        int typeIdx = getTypeIndex(type);
        int[] baseStats = BASE_STATS[typeIdx];
        
        int hp = (int) (baseStats[0] * multiplier);
        int mana = (int) (baseStats[1] * multiplier);
        int atk = (int) (baseStats[2] * multiplier);
        int def = (int) (baseStats[3] * multiplier);
        int range = baseStats[4];
        int spd = baseStats[5];
        
        return CharacterFactory.create(type, hp, mana, atk, def, range, spd, false);
    }
    
    private int getTypeIndex(String type) {
        return TYPE_INDICES.getOrDefault(type, 0);
    }
}
