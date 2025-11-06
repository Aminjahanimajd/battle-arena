package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.level.LevelRepository;
import com.amin.battlearena.domain.level.LevelSpec;
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

import java.util.List;

// Controller for the Campaign Map interface (data-driven from LevelRepository)
public class CampaignController {
    
    private MainApp app;
    private PlayerData playerData;
    private String currentPlayerName;
    private final LevelRepository levelRepository;
    private final List<LevelSpec> allLevels;
    
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
    
    public CampaignController() {
        this.levelRepository = new LevelRepository();
        this.allLevels = levelRepository.jsonLevelsOnly();
    }
    
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
        int lvl = Math.max(1, Math.min(selectedLevelNum, allLevels.size()));
        app.switchToGameWithLevel(currentPlayerName != null ? currentPlayerName : "Player", lvl);
    }

    private void selectLevel(int level) {
        selectedLevelNum = level;
        int unlockedUpTo = playerData != null ? Math.max(1, playerData.getCurrentLevel()) : 1;
        if (level <= unlockedUpTo && level <= allLevels.size()) {
            showLevelInfo(allLevels.get(level - 1));
        } else {
            showLockedLevelInfo(level);
        }
        if (levelInfoPanel != null) levelInfoPanel.setVisible(true);
    }
    
    private void showLevelInfo(LevelSpec level) {
        if (selectedLevelName != null) selectedLevelName.setText(level.name());
        if (selectedLevelChapter != null) selectedLevelChapter.setText(level.chapter());
        if (selectedLevelDescription != null) selectedLevelDescription.setText(level.description());
        if (enemyCount != null) enemyCount.setText(level.getEnemyCountLabel());
        if (rewardAmount != null) rewardAmount.setText(String.valueOf(level.rewards().winGold()));
        
        // Update difficulty stars
        updateDifficultyDisplay(level.difficulty());
        
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
        if (levelNum > allLevels.size()) {
            if (selectedLevelName != null) selectedLevelName.setText("🔒 Coming Soon");
            if (selectedLevelChapter != null) selectedLevelChapter.setText("Future Content");
            if (selectedLevelDescription != null) {
                selectedLevelDescription.setText("This level is not yet available.");
            }
        } else {
            LevelSpec level = allLevels.get(levelNum - 1);
            if (selectedLevelName != null) selectedLevelName.setText("🔒 " + level.name());
            if (selectedLevelChapter != null) selectedLevelChapter.setText(level.chapter());
            if (selectedLevelDescription != null) {
                selectedLevelDescription.setText("Complete previous levels to unlock this challenge!");
            }
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
        double progress = (double) (currentLevel - 1) / allLevels.size();
        if (campaignProgress != null) {
            campaignProgress.setProgress(progress);
        }
        if (progressText != null) {
            progressText.setText((currentLevel - 1) + "/" + allLevels.size() + " complete");
        }
    }

    private void updateLabels() {
        updatePlayerStats();
        updateCampaignProgress();
    }
    
    public void setCurrentLevel(int level) {
        if (playerData != null) {
            playerData.setCurrentLevel(Math.min(level, allLevels.size()));
            updateCampaignProgress();
        }
    }
    
    public int getSelectedLevel() {
        return selectedLevelNum;
    }
}
