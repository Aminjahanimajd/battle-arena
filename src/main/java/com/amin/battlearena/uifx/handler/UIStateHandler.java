package com.amin.battlearena.uifx.handler;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Handles UI state management for game interaction modes
public class UIStateHandler {
    
    private final VBox informationContainer;
    private String currentMode = "SELECT"; // SELECT, MOVE, ATTACK, ABILITY, CONSUMABLE
    private String statusMessage = "Select a character to start";
    
    public UIStateHandler(VBox informationContainer) {
        this.informationContainer = informationContainer;
        updateDisplay();
    }
    
    public void setMode(String mode) {
        this.currentMode = mode;
        updateDisplay();
    }
    
    public void setStatusMessage(String message) {
        this.statusMessage = message;
        updateDisplay();
    }
    
    private void updateDisplay() {
        if (informationContainer != null) {
            informationContainer.getChildren().clear();
            
            // Mode indicator
            Label modeLabel = new Label("Mode: " + currentMode);
            modeLabel.getStyleClass().add("mode-indicator");
            modeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            
            // Status message
            Label statusLabel = new Label(statusMessage);
            statusLabel.getStyleClass().add("status-message");
            statusLabel.setWrapText(true);
            statusLabel.setMaxWidth(200);
            
            // Mode-specific instructions
            Label instructionLabel = new Label(getModeInstructions());
            instructionLabel.getStyleClass().add("mode-instructions");
            instructionLabel.setWrapText(true);
            instructionLabel.setMaxWidth(200);
            
            informationContainer.getChildren().addAll(modeLabel, statusLabel, instructionLabel);
        }
    }
    
    private String getModeInstructions() {
        return switch (currentMode) {
            case "SELECT" -> "Click on a character to select them";
            case "MOVE" -> "Click on a highlighted tile to move";
            case "ATTACK" -> "Click on an enemy to attack";
            case "ABILITY" -> "Select ability, then click target";
            case "CONSUMABLE" -> "Select item, then click target";
            default -> "Follow the highlighted options";
        };
    }
    
    public String getCurrentMode() {
        return currentMode;
    }
    
    public boolean isInMode(String mode) {
        return currentMode.equals(mode);
    }
    
    public void resetToSelectMode() {
        setMode("SELECT");
        setStatusMessage("Select a character to start");
    }
}