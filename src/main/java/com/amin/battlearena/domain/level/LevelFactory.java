package com.amin.battlearena.domain.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Factory for creating and registering levels with builder pattern
public final class LevelFactory {
    
    private static final Map<String, LevelSpec> CUSTOM_LEVELS = new HashMap<>();
    
    private LevelFactory() {}
    
    public static LevelBuilder builder(String id) {
        return new LevelBuilder(id);
    }
    
    // Register a level (available through LevelRepository)
    public static void registerLevel(LevelSpec level) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }
        if (level.id() == null || level.id().trim().isEmpty()) {
            throw new IllegalArgumentException("Level ID cannot be null or empty");
        }
        if (CUSTOM_LEVELS.containsKey(level.id())) {
            throw new IllegalArgumentException("Level with ID '" + level.id() + "' is already registered");
        }
        CUSTOM_LEVELS.put(level.id(), level);
    }
    
    // Register level with campaign display metadata
    public static void registerLevelForCampaign(LevelSpec level, int campaignDisplayOrder, String campaignChapter) {
        registerLevel(level);
        CAMPAIGN_METADATA.put(level.id(), new CampaignMetadata(campaignDisplayOrder, campaignChapter));
    }
    
    private static final Map<String, CampaignMetadata> CAMPAIGN_METADATA = new HashMap<>();
    
    public static class CampaignMetadata {
        private final int displayOrder;
        private final String chapter;
        
        public CampaignMetadata(int displayOrder, String chapter) {
            this.displayOrder = displayOrder;
            this.chapter = (chapter != null) ? chapter : "Custom Levels";
        }
        
        public int getDisplayOrder() { return displayOrder; }
        public String getChapter() { return chapter; }
    }
    
    public static CampaignMetadata getCampaignMetadata(String levelId) {
        return CAMPAIGN_METADATA.get(levelId);
    }
    
    public static LevelSpec getLevel(String id) {
        if (id == null) return null;
        return CUSTOM_LEVELS.get(id);
    }
    
    public static boolean isRegistered(String id) {
        if (id == null) return false;
        return CUSTOM_LEVELS.containsKey(id);
    }
    
    public static String[] getRegisteredLevelIds() {
        return CUSTOM_LEVELS.keySet().toArray(String[]::new);
    }
    
    public static boolean unregisterLevel(String id) {
        if (id == null) return false;
        return CUSTOM_LEVELS.remove(id) != null;
    }
    
    public static void clearAll() {
        CUSTOM_LEVELS.clear();
    }
    
    // Builder for fluent level construction
    public static class LevelBuilder {
        private final String id;
        private String name;
        private final List<String> prereqs = new ArrayList<>();
        private LevelRewards rewards;
        private int requiresPlayerLevel = 1;
        private final List<String> enemies = new ArrayList<>();
        private final List<List<Integer>> enemyPositions = new ArrayList<>();
        private String note = "";
        private String winConditionType = "DEFEAT_ALL_ENEMIES";
        
        private LevelBuilder(String id) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Level ID cannot be null or empty");
            }
            this.id = id;
            this.name = id; // Default name to ID
        }
        
        public LevelBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public LevelBuilder addPrerequisite(String prereqId) {
            if (prereqId != null && !prereqId.trim().isEmpty()) {
                this.prereqs.add(prereqId);
            }
            return this;
        }
        
        public LevelBuilder withRewards(int gold, int exp) {
            this.rewards = new LevelRewards(gold, exp);
            return this;
        }
        
        public LevelBuilder withRewards(LevelRewards rewards) {
            this.rewards = rewards;
            return this;
        }
        
        public LevelBuilder requiresPlayerLevel(int level) {
            this.requiresPlayerLevel = Math.max(1, level);
            return this;
        }
        
        public LevelBuilder addEnemy(String characterType, int row, int col) {
            if (characterType == null || characterType.trim().isEmpty()) {
                throw new IllegalArgumentException("Character type cannot be null or empty");
            }
            this.enemies.add(characterType);
            this.enemyPositions.add(Arrays.asList(row, col));
            return this;
        }
        
        public LevelBuilder withNote(String note) {
            this.note = (note != null) ? note : "";
            return this;
        }
        
        public LevelBuilder withWinCondition(String winConditionType) {
            this.winConditionType = (winConditionType != null) ? winConditionType : "DEFEAT_ALL_ENEMIES";
            return this;
        }
        
        public LevelSpec build() {
            // Validation
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalStateException("Level name is required");
            }
            if (enemies.isEmpty()) {
                throw new IllegalStateException("Level must have at least one enemy");
            }
            if (enemies.size() != enemyPositions.size()) {
                throw new IllegalStateException("Enemy count must match position count");
            }
            
            // Default rewards if not set
            if (rewards == null) {
                rewards = new LevelRewards(50, 25);
            }
            
            return new LevelSpec(
                id,
                name,
                new ArrayList<>(prereqs),
                rewards,
                requiresPlayerLevel,
                new ArrayList<>(enemies),
                new ArrayList<>(enemyPositions),
                note,
                winConditionType
            );
        }
    }
}
