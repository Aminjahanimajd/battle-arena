package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.uifx.MainApp;

import javafx.fxml.FXML;

public class HelpController {
    private MainApp app;
    private String currentPlayerName = "Player";

    public void setApp(MainApp app) {
        this.app = app;
    }
    
    public void setPlayerName(String playerName) {
        this.currentPlayerName = playerName;
    }

    @FXML public void onBack() {
        if (app != null) {
            app.switchToMainMenu(currentPlayerName);
        }
        // Simple fallback - if no app reference, just return silently
    }
}
