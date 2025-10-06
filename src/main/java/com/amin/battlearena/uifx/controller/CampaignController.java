package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.uifx.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Controller for the Campaign Map interface
 * Handles level selection and campaign progression
 */
public class CampaignController {
    
    private MainApp app;
    private PlayerData playerData;
    private String currentPlayerName;
    
    // Header elements
    @FXML private Label playerGold;
    @FXML private Label playerLevel;
    @FXML private Label victoriesCount;
    @FXML private ProgressBar campaignProgress;
    @FXML private Label progressText;
    
    // Level info panel
    @FXML private VBox levelInfoPanel;
    @FXML private Circle selectedLevelIcon;
    @FXML private Label selectedLevelName;
    @FXML private Label selectedLevelChapter;
    @FXML private Label selectedLevelDescription;
    @FXML private HBox difficultyStars;
    @FXML private Label enemyCount;
    @FXML private Label rewardAmount;
    @FXML private Button startLevelBtn;
    
    private int selectedLevelNum = 1;
    
    // Level data - Balanced rewards for progressive upgrades
    private static final LevelInfo[] LEVELS = {
        new LevelInfo(1, "First Steps", "Training Grounds", "Learn the basics of combat. Master movement and basic attacks.", 1, "2-3", 80),
        new LevelInfo(2, "Warrior's Trial", "Training Grounds", "Face basic enemies and test your warrior skills.", 2, "3-4", 120),
        new LevelInfo(3, "Archer's Test", "Training Grounds", "Master ranged combat with precision and tactics.", 2, "3-4", 160),
        new LevelInfo(4, "Forest Ambush", "Dark Forest", "Survive the darkness and enemy ambushes in the forest.", 3, "4-5", 200),
        new LevelInfo(5, "Ancient Guardian", "Dark Forest", "Face the ancient forest guardian in epic combat.", 3, "1 Boss", 250),
        new LevelInfo(6, "Mystic Clearing", "Dark Forest", "Magical enemies await in this mystical battleground.", 3, "4-6", 300),
        new LevelInfo(7, "Rocky Ascent", "Mountain Peak", "Climb the treacherous mountain paths while fighting foes.", 4, "5-6", 400),
        new LevelInfo(8, "Dragon's Lair", "Mountain Peak", "Enter the dragon's domain and face the ancient beast.", 4, "1 Dragon", 500),
        new LevelInfo(9, "Summit Battle", "Mountain Peak", "Reach the mountain peak in this ultimate test.", 4, "6-8", 650),
        new LevelInfo(10, "Arena Champion", "Grand Arena", "Face the reigning arena champion in single combat.", 5, "1 Champion", 800),
        new LevelInfo(11, "Master's Trial", "Grand Arena", "The ultimate test of skill against multiple masters.", 5, "3 Masters", 1000),
        new LevelInfo(12, "ARENA MASTER", "Grand Arena", "Become the ultimate champion of the battle arena!", 5, "Final Boss", 1500)
    };
    
    public void setApp(MainApp app) {
        this.app = app;
        loadPlayerData();
        updatePlayerStats();
        updateCampaignProgress();
        // Language listener removed for simplified architecture
        updateLabels();
    }
    
    public void setPlayerName(String playerName) {
        this.currentPlayerName = playerName;
        loadPlayerData();
        updatePlayerStats();
        updateCampaignProgress();
    }
    
    private void loadPlayerData() {
        if (currentPlayerName != null && !currentPlayerName.isEmpty()) {
            this.playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
        }
    }
    
    @FXML
    public void initialize() {
        // Initialize UI state
        updatePlayerStats();
        updateCampaignProgress();
        
        // Initialize with level 1 selected (this ensures the level info is populated)
        selectLevel(1);
    }
    
    @FXML
    public void onBack() {
        if (app != null) {
            app.switchToMainMenu(currentPlayerName != null ? currentPlayerName : "Player");
        }
    }

    // Level selection handlers from FXML
    @FXML void onLevel1(MouseEvent e) { selectLevel(1); }
    @FXML void onLevel2(MouseEvent e) { selectLevel(2); }
    @FXML void onLevel3(MouseEvent e) { selectLevel(3); }
    @FXML void onLevel4(MouseEvent e) { selectLevel(4); }
    @FXML void onLevel5(MouseEvent e) { selectLevel(5); }
    @FXML void onLevel6(MouseEvent e) { selectLevel(6); }
    @FXML void onLevel7(MouseEvent e) { selectLevel(7); }
    @FXML void onLevel8(MouseEvent e) { selectLevel(8); }
    @FXML void onLevel9(MouseEvent e) { selectLevel(9); }
    @FXML void onLevel10(MouseEvent e) { selectLevel(10); }
    @FXML void onLevel11(MouseEvent e) { selectLevel(11); }
    @FXML void onLevel12(MouseEvent e) { selectLevel(12); }

    @FXML void onStartSelectedLevel(ActionEvent e) {
        if (app == null) return;
        int lvl = Math.max(1, Math.min(selectedLevelNum, LEVELS.length));
        app.switchToGameWithLevel(currentPlayerName != null ? currentPlayerName : "Player", lvl);
    }



    private void selectLevel(int level) {
        selectedLevelNum = level;
        int unlockedUpTo = playerData != null ? Math.max(1, playerData.getCurrentLevel()) : 1;
        if (level <= unlockedUpTo) {
            showLevelInfo(LEVELS[level - 1]);
        } else {
            showLockedLevelInfo(level);
        }
        if (levelInfoPanel != null) levelInfoPanel.setVisible(true);
    }
    
    private void showLevelInfo(LevelInfo info) {
        if (selectedLevelName != null) selectedLevelName.setText(info.name);
        if (selectedLevelChapter != null) selectedLevelChapter.setText(info.chapter);
        if (selectedLevelDescription != null) selectedLevelDescription.setText(info.description);
        if (enemyCount != null) enemyCount.setText(info.enemies);
        if (rewardAmount != null) rewardAmount.setText(String.valueOf(info.reward));
        
        // Update difficulty stars
        updateDifficultyDisplay(info.difficulty);
        
        // Enable start button
        if (startLevelBtn != null) {
            startLevelBtn.setDisable(false);
            startLevelBtn.setText("⚔ START BATTLE");
        }
        
        // Style the level icon
        if (selectedLevelIcon != null) {
            selectedLevelIcon.getStyleClass().clear();
            selectedLevelIcon.getStyleClass().add("level-icon");
            selectedLevelIcon.getStyleClass().add("available");
        }
    }
    
    private void showLockedLevelInfo(int levelNum) {
        LevelInfo info = LEVELS[levelNum - 1];
        
        if (selectedLevelName != null) selectedLevelName.setText("🔒 " + info.name);
        if (selectedLevelChapter != null) selectedLevelChapter.setText(info.chapter);
        if (selectedLevelDescription != null) {
            selectedLevelDescription.setText("Complete previous levels to unlock this challenge!");
        }
        if (enemyCount != null) enemyCount.setText("???");
        if (rewardAmount != null) rewardAmount.setText("???");
        
        // Clear difficulty stars
        if (difficultyStars != null) {
            difficultyStars.getChildren().clear();
        }
        
        // Disable start button
        if (startLevelBtn != null) {
            startLevelBtn.setDisable(true);
            startLevelBtn.setText("🔒 LOCKED");
        }
        
        // Style the level icon as locked
        if (selectedLevelIcon != null) {
            selectedLevelIcon.getStyleClass().clear();
            selectedLevelIcon.getStyleClass().add("level-icon");
            selectedLevelIcon.getStyleClass().add("locked");
        }
        
        if (levelInfoPanel != null) {
            levelInfoPanel.setVisible(true);
        }
    }
    
    private void updateDifficultyDisplay(int difficulty) {
        if (difficultyStars != null) {
            difficultyStars.getChildren().clear();
            
            for (int i = 0; i < 5; i++) {
                Label star = new Label();
                star.getStyleClass().add("difficulty-star");
                if (i < difficulty) {
                    star.setText("⭐");
                    star.getStyleClass().add("filled");
                } else {
                    star.setText("☆");
                    star.getStyleClass().add("empty");
                }
                difficultyStars.getChildren().add(star);
            }
        }
    }
    
    private void updatePlayerStats() {
        // Language manager removed for simplified architecture
        if (playerData != null) {
            if (playerGold != null) playerGold.setText(String.valueOf(playerData.getGold()));
            if (playerLevel != null) playerLevel.setText("Level " + playerData.getCurrentLevel());
            if (victoriesCount != null) victoriesCount.setText(playerData.getVictories() + " wins");
        } else {
            // Fallback values
            if (playerGold != null) playerGold.setText("1000");
            if (playerLevel != null) playerLevel.setText("Level 1");
            if (victoriesCount != null) victoriesCount.setText("0 wins");
        }
    }
    
    private void updateCampaignProgress() {
        int currentLevel = playerData != null ? playerData.getCurrentLevel() : 1;
        double progress = (double) (currentLevel - 1) / LEVELS.length;
        if (campaignProgress != null) {
            campaignProgress.setProgress(progress);
        }
        if (progressText != null) {
            progressText.setText((currentLevel - 1) + "/" + LEVELS.length + " complete");
        }
    }

    // Language manager removed for simplified architecture

    private void updateLabels() {
        // Add keys as needed; showing example using available keys
        // For now, keep static labels; hook exists for future FXML label key mapping
        updatePlayerStats();
        updateCampaignProgress();
    }
    
    // Helper class for level information
    private static class LevelInfo {
        final String name;
        final String chapter;
        final String description;
        final int difficulty;
        final String enemies;
        final int reward;
        
        LevelInfo(int number, String name, String chapter, String description, 
                 int difficulty, String enemies, int reward) {
            this.name = name;
            this.chapter = chapter;
            this.description = description;
            this.difficulty = difficulty;
            this.enemies = enemies;
            this.reward = reward;
        }
    }
    
    /**
     * Update the player's current level progress
     */
    public void setCurrentLevel(int level) {
        if (playerData != null) {
            playerData.setCurrentLevel(Math.min(level, LEVELS.length));
            updateCampaignProgress();
        }
    }
    
    /**
     * Get the currently selected level number
     */
    public int getSelectedLevel() {
        return selectedLevelNum;
    }
    
    // Removed unused method showAlert
}
