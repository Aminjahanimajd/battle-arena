package com.amin.battlearena.uifx;

import java.io.IOException;

import com.amin.battlearena.uifx.controller.CampaignController;
import com.amin.battlearena.uifx.controller.GameController;
import com.amin.battlearena.uifx.controller.HelpController;
import com.amin.battlearena.uifx.controller.MainMenuController;
import com.amin.battlearena.uifx.controller.ShopController;
import com.amin.battlearena.uifx.controller.SignInController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    // FPS overlay support
    private javafx.scene.control.Label fpsLabel;
    private javafx.animation.AnimationTimer fpsTimer;
    private long fpsLastNs = 0L;
    private int fpsFrames = 0;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("⚔ Battle Arena - Enhanced GUI");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Load and apply global stylesheet
        try {
            var url = getClass().getResource("/uifx/styles.css");
            if (url != null) {
                // CSS will be applied to scenes when they are created
                url.toExternalForm();
            }
        } catch (Exception e) {
            // Stylesheet loading failed - continue without custom styles
        }
        
        showSignIn();
    }

    private void showSignIn() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/signin.fxml"));
        Parent root = loader.load();
        SignInController controller = loader.getController();
        controller.setApp(this);
        Scene scene = new Scene(root, 600, 450);
        // Apply stylesheets
        try {
            var url = getClass().getResource("/uifx/styles.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load stylesheet: " + e.getMessage());
        }
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        primaryStage.show();
    }

    public void switchToMainMenu(String playerName) {
        try {
            // Save all loaded data before clearing to prevent data loss
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().saveAllLoadedData();
            // Clear any cached data to ensure proper account isolation
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().clearLoadedData();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/main_menu.fxml"));
            Parent root = loader.load();
            MainMenuController controller = loader.getController();
            controller.setApp(this, playerName);
            // Refresh gold display in case it changed
            controller.refreshGoldDisplay();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main menu", e);
        }
    }
    
    /**
     * Switch back to sign-in for account change
     */
    public void switchToSignIn() {
        try {
            // Save all loaded data before clearing to prevent data loss
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().saveAllLoadedData();
            // Clear all loaded data for proper account isolation
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().clearLoadedData();
            showSignIn();
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to sign-in", e);
        }
    }

    // Settings functionality removed for simplified architecture

    public void switchToHelp(String playerName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/help.fxml"));
            Parent root = loader.load();
            HelpController controller = loader.getController();
            controller.setApp(this);
            Scene scene = new Scene(root, 750, 600);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load help scene", e);
        }
    }

    public void switchToCampaign(String playerName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/campaign.fxml"));
            Parent root = loader.load();
            CampaignController controller = loader.getController();
            controller.setApp(this);
            controller.setPlayerName(playerName);
            Scene scene = new Scene(root, 1100, 750);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load campaign scene", e);
        }
    }

    public void switchToShop(String playerName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/shop.fxml"));
            Parent root = loader.load();
            ShopController controller = loader.getController();
            controller.setApp(this);
            controller.setPlayerName(playerName); // ensure per-account isolation
            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        } catch (java.io.IOException e) {
            System.err.println("Failed to load shop FXML: " + e.getMessage());
            throw new RuntimeException("Failed to load shop scene", e);
        } catch (RuntimeException e) {
            System.err.println("Runtime error in shop: " + e.getMessage());
            throw new RuntimeException("Failed to load shop scene", e);
        }
    }

    public void switchToGame(String playerName) {
        switchToGameWithLevel(playerName, 1); // Default to level 1
    }
    
    public void switchToGameWithLevel(String playerName, int levelNumber) {
        try {
            System.out.println("[UI] Loading game.fxml for level " + levelNumber);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifx/game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setApp(this);
            controller.setPlayerName(playerName);
            controller.setLevelNumber(levelNumber);
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
            System.out.println("[UI] Switched to Game scene for level " + levelNumber);
        } catch (IOException e) {
            System.err.println("Failed to load game scene: " + e.getMessage());
            throw new RuntimeException("Failed to load game scene", e);
        }
    }

    // Resolution apply (WxH)
    public void applyResolution(String res) {
        if (res == null) return;
        try {
            String[] parts = res.split("x");
            if (parts.length != 2) return;
            int w = Integer.parseInt(parts[0].trim());
            int h = Integer.parseInt(parts[1].trim());
            primaryStage.setWidth(w);
            primaryStage.setHeight(h);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid resolution setting '" + res + "': " + ex.getMessage());
        }
    }

    // FPS overlay on the primary stage
    public void enableFpsOverlay(boolean enable) {
        try {
            if (!enable) {
                if (fpsTimer != null) { fpsTimer.stop(); fpsTimer = null; }
                if (fpsLabel != null && primaryStage.getScene() != null) {
                    ((javafx.scene.layout.Pane) primaryStage.getScene().getRoot()).getChildren().remove(fpsLabel);
                    fpsLabel = null;
                }
                return;
            }
            if (primaryStage.getScene() == null) return;
            javafx.scene.Parent root = primaryStage.getScene().getRoot();
            if (!(root instanceof javafx.scene.layout.Pane)) return; // requires pane root
            if (fpsLabel == null) {
                fpsLabel = new javafx.scene.control.Label("FPS: 0");
                fpsLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: #fff; -fx-padding: 2 6; -fx-font-size: 11px; -fx-background-radius: 3;");
                fpsLabel.setMouseTransparent(true);
                ((javafx.scene.layout.Pane) root).getChildren().add(fpsLabel);
                fpsLabel.layoutXProperty().bind(primaryStage.getScene().widthProperty().subtract(65));
                fpsLabel.layoutYProperty().set(6);
            }
            if (fpsTimer == null) {
                fpsTimer = new javafx.animation.AnimationTimer() {
                    @Override public void handle(long now) {
                        if (fpsLastNs == 0) fpsLastNs = now;
                        fpsFrames++;
                        long diff = now - fpsLastNs;
                        if (diff >= 1_000_000_000L) {
                            int fps = fpsFrames;
                            fpsLabel.setText("FPS: " + fps);
                            fpsFrames = 0;
                            fpsLastNs = now;
                        }
                    }
                };
                fpsTimer.start();
            }
        } catch (Exception ex) {
            System.out.println("Failed to enable FPS overlay: " + ex.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        // Clean up resources when application closes
        super.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

