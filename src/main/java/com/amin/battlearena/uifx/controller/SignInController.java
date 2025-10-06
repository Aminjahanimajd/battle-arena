package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.uifx.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignInController {

    private MainApp app;

    @FXML private TextField nicknameField;
    @FXML private Label welcomeLabel;
    @FXML private Label instructionLabel;

    public void setApp(MainApp app) {
        this.app = app;
        initializeUI();
    }
    
    @FXML
    public void initialize() {
        initializeUI();
    }
    
    private void initializeUI() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("⚔ Welcome to Battle Arena ⚔");
        }
        if (instructionLabel != null) {
            instructionLabel.setText("Enter your player name to continue. You must have an account to play.");
        }
    }

    @FXML
    public void onLogin(ActionEvent e) {
        String name = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a player name");
            return;
        }
        
        if (name.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Player name must be at least 3 characters long");
            return;
        }
        
        if (name.length() > 20) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Player name must be no more than 20 characters long");
            return;
        }
        
        // Check if account exists WITHOUT creating it first, to show correct message
        boolean existed = PlayerDataManager.getInstance().playerExists(name);
        PlayerData playerData = PlayerDataManager.getInstance().loadPlayerData(name);
        
        if (!existed) {
            showAlert(Alert.AlertType.INFORMATION, "Account Created", 
                "New account created for " + name + "! Welcome to Battle Arena!");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", 
                "Welcome back, " + name + "!");
        }
        
        // Update last login time
        playerData.updateLastLogin();
        PlayerDataManager.getInstance().savePlayerData(playerData);
        
        app.switchToMainMenu(name);
    }
    
    @FXML
    public void onCreateAccount(ActionEvent e) {
        String name = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a player name");
            return;
        }
        
        if (name.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Player name must be at least 3 characters long");
            return;
        }
        
        if (name.length() > 20) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Player name must be no more than 20 characters long");
            return;
        }
        
        // Check if account already exists
        PlayerData existingData = PlayerDataManager.getInstance().loadPlayerData(name);
        if (existingData != null && existingData.getAccountId() != null) {
            showAlert(Alert.AlertType.ERROR, "Account Exists", 
                "An account with name '" + name + "' already exists. Please use a different name or login instead.");
            return;
        }
        
        // Create new account
        PlayerData newPlayerData = new PlayerData(name);
        PlayerDataManager.getInstance().savePlayerData(newPlayerData);
        
        showAlert(Alert.AlertType.INFORMATION, "Account Created", 
            "Account created successfully for " + name + "! You can now login and play.");
        
        app.switchToMainMenu(name);
    }

    @FXML
    public void onExit(ActionEvent e) {
        var window = nicknameField.getScene() != null ? nicknameField.getScene().getWindow() : null;
        if (window instanceof Stage stage) {
            stage.close();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


