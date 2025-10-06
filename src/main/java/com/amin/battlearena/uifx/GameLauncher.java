package com.amin.battlearena.uifx;

/**
 * Enhanced Game Launcher with improved graphics support
 * 
 * This class provides a clean entry point for the GUI version of Battle Arena
 * with proper JavaFX initialization and error handling.
 */
public class GameLauncher {
    
    public static void main(String[] args) {
        try {
            // Set system properties for better rendering
            System.setProperty("javafx.animation.fullspeed", "true");
            System.setProperty("javafx.animation.pulse", "60");
            System.setProperty("prism.vsync", "true");
            System.setProperty("prism.lcdtext", "true");
            
            // Launch the JavaFX application
            MainApp.launch(MainApp.class, args);
            
        } catch (Exception e) {
            System.err.println("Failed to launch Battle Arena GUI: " + e.getMessage());
            System.exit(1);
        }
    }
}