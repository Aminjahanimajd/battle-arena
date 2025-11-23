package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.Player;
import com.amin.battlearena.infra.SceneManager;
import com.amin.battlearena.persistence.AccountRepository;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CampaignController {

    @FXML private Label playerGold;
    @FXML private Label playerLevel;
    @FXML private Label victoriesCount;
    @FXML private ProgressBar campaignProgress;
    @FXML private Label progressText;
    
    @FXML private VBox levelInfoPanel;
    @FXML private Label selectedLevelName;
    @FXML private Label selectedLevelChapter;
    @FXML private Label selectedLevelDescription;
    @FXML private Label enemyCount;
    @FXML private Label rewardAmount;
    @FXML private Button startLevelBtn;
    @FXML private HBox difficultyStars;

    private int selectedLevelId = -1;

    @FXML
    public void initialize() {
        updatePlayerStats();
        levelInfoPanel.setVisible(false);
    }

    private void updatePlayerStats() {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            playerGold.setText(String.valueOf(p.getGold()));
            playerLevel.setText("Level " + p.getLevel());
            victoriesCount.setText(p.getVictories() + " Wins");
            
            int progress = p.getCampaignProgress();
            campaignProgress.setProgress((double) (progress - 1) / 12.0);
            progressText.setText((progress - 1) + "/12 Complete");
            
            // Logic to lock/unlock levels in UI would go here if we had references to them
            // Since we don't have direct references to all VBox level cards, we rely on FXML structure
            // In a real app, we'd iterate or bind them. For now, we assume the user clicks what they can.
        }
    }

    @FXML
    public void onLevelSelected(MouseEvent event) {
        Node source = (Node) event.getSource();
        Object userData = source.getUserData();
        if (userData != null) {
            int levelId = Integer.parseInt(userData.toString());
            Player p = AccountRepository.getInstance().getCurrentUser();
            
            // Check if level is unlocked
            if (p != null && levelId <= p.getCampaignProgress()) {
                selectLevel(levelId);
            } else {
                // Locked
                selectedLevelName.setText("LOCKED");
                selectedLevelChapter.setText("Complete previous levels first");
                selectedLevelDescription.setText("");
                startLevelBtn.setDisable(true);
                levelInfoPanel.setVisible(true);
            }
        }
    }

    private void selectLevel(int levelId) {
        this.selectedLevelId = levelId;
        levelInfoPanel.setVisible(true);
        startLevelBtn.setDisable(false);
        
        selectedLevelName.setText("Level " + levelId);
        
        if (levelId <= 3) selectedLevelChapter.setText("Chapter 1: Training Grounds");
        else if (levelId <= 6) selectedLevelChapter.setText("Chapter 2: Dark Forest");
        else if (levelId <= 9) selectedLevelChapter.setText("Chapter 3: Mountain Peak");
        else selectedLevelChapter.setText("Chapter 4: Final Arena");
        
        selectedLevelDescription.setText("Defeat all enemies to claim victory!");
        enemyCount.setText(String.valueOf(2 + (levelId / 2))); // Simple formula
        rewardAmount.setText(String.valueOf(50 + (levelId * 25)));
        
        difficultyStars.getChildren().clear();
        int stars = (levelId + 2) / 3;
        for (int i = 0; i < stars; i++) {
            Label star = new Label("⭐");
            difficultyStars.getChildren().add(star);
        }
    }

    @FXML
    public void onStartSelectedLevel() {
        if (selectedLevelId > 0) {
            // Pass level ID to GameController/Engine
            // We'll use a static holder in GameController for simplicity
            GameController.setTargetLevel(selectedLevelId);
            SceneManager.getInstance().switchScene("/uifx/game.fxml");
        }
    }

    @FXML
    public void onBack() {
        SceneManager.getInstance().switchScene("/uifx/main_menu.fxml");
    }
}
