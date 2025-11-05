package com.amin.battlearena.uifx;

// Entry point for Battle Arena GUI with JavaFX initialization
public class GameLauncher {
    
    public static void main(String[] args) {
        try {
            System.setProperty("javafx.animation.fullspeed", "true");
            System.setProperty("javafx.animation.pulse", "60");
            System.setProperty("prism.vsync", "true");
            System.setProperty("prism.lcdtext", "true");
            
            MainApp.launch(MainApp.class, args);
            
        } catch (Exception e) {
            System.err.println("Failed to launch Battle Arena GUI: " + e.getMessage());
            System.exit(1);
        }
    }
}