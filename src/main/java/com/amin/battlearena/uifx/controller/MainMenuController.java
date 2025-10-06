package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.uifx.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainMenuController {
    private MainApp app;
    private String currentPlayerName;
    @FXML private Label welcomeLabel;
    @FXML private Label goldAmount;

    public void setApp(MainApp app, String playerName) {
        this.app = app;
        this.currentPlayerName = playerName;
        if (this.welcomeLabel != null) {
            this.welcomeLabel.setText(String.format("Welcome, %s!", playerName));
        }
        
        // Load and display actual gold amount
        updateGoldDisplay();
        updateLabels();
    }
    
    private void updateGoldDisplay() {
        if (goldAmount != null && currentPlayerName != null && !currentPlayerName.isEmpty()) {
            PlayerData playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
            goldAmount.setText("Gold: " + playerData.getGold());
        }
    }

    private void updateLabels() {
        if (welcomeLabel != null && currentPlayerName != null) {
            welcomeLabel.setText(String.format("Welcome, %s!", currentPlayerName));
        }
        if (goldAmount != null && currentPlayerName != null) {
            PlayerData playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
            goldAmount.setText("Gold: " + playerData.getGold());
        }
    }
    
    public void refreshGoldDisplay() {
        updateGoldDisplay();
    }
    
    // Audio system removed for simplified architecture

    @FXML public void onCampaign() {

        if (app != null) {
            app.switchToCampaign(currentPlayerName);
        }
    }
    @FXML public void onShop() { 
        if (app != null) {
            app.switchToShop(currentPlayerName);
        }
    }
    
    @FXML public void onHelp() { 
        if (app != null) {
            app.switchToHelp(currentPlayerName);
        }
    }
    @FXML public void onChangeAccount() { 
        if (app != null) {
            app.switchToSignIn();
        }
    }
    @FXML public void onExit() { 
        var window = welcomeLabel != null && welcomeLabel.getScene() != null ? welcomeLabel.getScene().getWindow() : null;
        if (window instanceof Stage stage) {
            stage.close();
        }
    }
}


