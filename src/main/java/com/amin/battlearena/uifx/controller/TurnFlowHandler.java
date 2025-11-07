package com.amin.battlearena.uifx.controller;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.players.AIPlayer;
import com.amin.battlearena.players.HumanPlayer;
import com.amin.battlearena.uifx.MainApp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

// Handles turn flow, game end conditions, victory/defeat logic, and timer management
public class TurnFlowHandler {
    
    // Dependencies injected from GameController
    private GameEngine engine;
    private HumanPlayer human;
    private AIPlayer cpu;
    private String currentPlayerName;
    private int currentLevelNumber;
    private MainApp app;
    
    // State tracking
    private boolean gameEnded = false;
    private Timeline turnTimer;
    private int secondsLeft = 60;
    
    // UI components for timer
    private Label timerLabel;
    private ProgressBar timerProgress;
    
    // State maps
    private Map<Character, Integer> movesLeft;
    private Map<Character, Integer> attacksLeft;
    private Map<Position, Integer> deathFx;
    
    // Callback functions to interact with GameController
    private Consumer<String> appendLog;
    private Runnable renderBoard;
    private Runnable updateControls;
    private Runnable updateSelectedHud;
    private Runnable disableControlsAfterGameOver;
    private Runnable onEndTurn;
    private Function<Character, Boolean> hasAttacksLeft;
    private Function<Character, Boolean> hasMovesLeft;
    private Consumer<Character> decrementAttack;
    private Consumer<Character> decrementMove;
    private Function<Character, Integer> movesPerTurn;
    private Function<Character, Integer> attacksPerTurn;
    private BiFunction<Character, List<Character>, Boolean> tryUseAnyAbility;
    private BiFunction<Character, List<Character>, Boolean> tryAttackAny;
    private BiFunction<Character, List<Character>, Character> nearestEnemy;
    private BiFunction<Position, Position, List<Position>> bfsPath;
    private BiFunction<Integer, Integer, Character> findCharacterAt;
    
    public TurnFlowHandler() {
        // Constructor - dependencies will be injected via setters
    }
    
    // Dependency injection methods
    public void setEngine(GameEngine engine) { this.engine = engine; }
    public void setHuman(HumanPlayer human) { this.human = human; }
    public void setCpu(AIPlayer cpu) { this.cpu = cpu; }
    public void setCurrentPlayerName(String currentPlayerName) { this.currentPlayerName = currentPlayerName; }
    public void setCurrentLevelNumber(int currentLevelNumber) { this.currentLevelNumber = currentLevelNumber; }
    public void setApp(MainApp app) { this.app = app; }
    public void setTimerLabel(Label timerLabel) { this.timerLabel = timerLabel; }
    public void setTimerProgress(ProgressBar timerProgress) { this.timerProgress = timerProgress; }
    public void setMovesLeft(Map<Character, Integer> movesLeft) { this.movesLeft = movesLeft; }
    public void setAttacksLeft(Map<Character, Integer> attacksLeft) { this.attacksLeft = attacksLeft; }
    public void setDeathFx(Map<Position, Integer> deathFx) { this.deathFx = deathFx; }
    
    // Callback injection methods
    public void setAppendLog(Consumer<String> appendLog) { this.appendLog = appendLog; }
    public void setRenderBoard(Runnable renderBoard) { this.renderBoard = renderBoard; }
    public void setUpdateControls(Runnable updateControls) { this.updateControls = updateControls; }
    public void setUpdateSelectedHud(Runnable updateSelectedHud) { this.updateSelectedHud = updateSelectedHud; }
    public void setDisableControlsAfterGameOver(Runnable disableControlsAfterGameOver) { this.disableControlsAfterGameOver = disableControlsAfterGameOver; }
    public void setOnEndTurn(Runnable onEndTurn) { this.onEndTurn = onEndTurn; }
    public void setHasAttacksLeft(Function<Character, Boolean> hasAttacksLeft) { this.hasAttacksLeft = hasAttacksLeft; }
    public void setHasMovesLeft(Function<Character, Boolean> hasMovesLeft) { this.hasMovesLeft = hasMovesLeft; }
    public void setDecrementAttack(Consumer<Character> decrementAttack) { this.decrementAttack = decrementAttack; }
    public void setDecrementMove(Consumer<Character> decrementMove) { this.decrementMove = decrementMove; }
    public void setMovesPerTurn(Function<Character, Integer> movesPerTurn) { this.movesPerTurn = movesPerTurn; }
    public void setAttacksPerTurn(Function<Character, Integer> attacksPerTurn) { this.attacksPerTurn = attacksPerTurn; }
    public void setTryUseAnyAbility(BiFunction<Character, List<Character>, Boolean> tryUseAnyAbility) { this.tryUseAnyAbility = tryUseAnyAbility; }
    public void setTryAttackAny(BiFunction<Character, List<Character>, Boolean> tryAttackAny) { this.tryAttackAny = tryAttackAny; }
    public void setNearestEnemy(BiFunction<Character, List<Character>, Character> nearestEnemy) { this.nearestEnemy = nearestEnemy; }
    public void setBfsPath(BiFunction<Position, Position, List<Position>> bfsPath) { this.bfsPath = bfsPath; }
    public void setFindCharacterAt(BiFunction<Integer, Integer, Character> findCharacterAt) { this.findCharacterAt = findCharacterAt; }
    
    public void runCpuTurn() {
        if (engine == null || cpu == null) return;

        // Reset CPU budgets for this turn
        movesLeft.clear();
        attacksLeft.clear();
        for (var u : cpu.getTeam()) {
            movesLeft.put(u, movesPerTurn.apply(u));
            attacksLeft.put(u, attacksPerTurn.apply(u));
        }

        var enemies = (human != null)
                ? new java.util.ArrayList<>(human.getTeam())
                : java.util.Collections.<Character>emptyList();

        // For each CPU unit, keep acting until its budgets are exhausted or battle ends
        for (var u : new java.util.ArrayList<>(cpu.getTeam())) {
            while (u.isAlive() && (hasMovesLeft.apply(u) || hasAttacksLeft.apply(u))) {
                if (checkAndHandleGameEnd()) return;

                // 1) Use all available abilities before doing anything else
                boolean usedAbility = false;
                while (tryUseAnyAbility.apply(u, enemies)) {
                    usedAbility = true;
                    renderBoard.run();
                    updateControls.run();
                    updateSelectedHud.run();
                    if (checkAndHandleGameEnd()) return;
                }

                // 2) Attack as long as enemies are in range and attacks remain
                boolean attacked = false;
                while (hasAttacksLeft.apply(u) && tryAttackAny.apply(u, enemies)) {
                    decrementAttack.accept(u);
                    attacked = true;
                    renderBoard.run();
                    updateControls.run();
                    updateSelectedHud.run();
                    if (checkAndHandleGameEnd()) return;
                }

                if (attacked || usedAbility) {
                    // If we acted, continue loop to re-evaluate (could attack again)
                    continue;
                }

                // 3) Otherwise move one step toward nearest enemy
                if (hasMovesLeft.apply(u)) {
                    var tgt = nearestEnemy.apply(u, enemies);
                    if (tgt != null) {
                        List<Position> path = bfsPath.apply(u.getPosition(), tgt.getPosition());
                        if (!path.isEmpty()) {
                            Position next = path.get(0);
                            if (findCharacterAt.apply(next.x(), next.y()) == null) {
                                boolean ok = engine.move(u, next);
                                if (ok) {
                                    decrementMove.accept(u);
                                    appendLog.accept(u.getName() + " (CPU) moved to " + next + "\n");
                                    renderBoard.run();
                                    updateControls.run();
                                    updateSelectedHud.run();
                                    continue; // after moving, re-loop to attempt abilities/attacks
                                }
                            }
                        }
                    }
                    // Could not move — still consume one move to prevent infinite loop
                    decrementMove.accept(u);
                } else {
                    break; // No moves left, end this unit's actions
                }
            }
        }

        renderBoard.run();
        updateControls.run();
        updateSelectedHud.run();
        appendLog.accept("CPU turn completed.\n");
    }
    
    // Checks if the game has ended and handles victory/defeat
    public boolean checkAndHandleGameEnd() {
        if (gameEnded) return true; // Already handled
        
        boolean humanAlive = (human != null) && human.hasAliveCharacters();
        boolean cpuAlive   = (cpu != null)   && cpu.hasAliveCharacters();

        if (humanAlive && cpuAlive) return false;

        gameEnded = true; // Set flag to prevent spam
        boolean playerWon = humanAlive;
        String winner = playerWon ? "Victory!" : (cpuAlive ? "Defeat!" : "Draw!");
        appendLog.accept("Battle ended. Result: " + winner + "\n");
        disableControlsAfterGameOver.run();
        
        // Stop timer
        if (turnTimer != null) {
            turnTimer.stop();
        }

        // Handle progression and rewards
        if (playerWon) {
            handleVictory();
        } else {
            handleDefeat();
        }

        return true;
    }
    
    private void handleVictory() {
        // Calculate rewards
        int goldReward = calculateGoldReward();
        
        // Load player data
        PlayerData playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
        
        if (playerData != null) {
            // Use the existing completeLevel method which handles everything
            playerData.completeLevel(currentLevelNumber, goldReward);
            
            // Save progress
            PlayerDataManager.getInstance().savePlayerData(playerData);
            
            appendLog.accept("🎉 Victory! Earned " + goldReward + " gold!\n");
            appendLog.accept("Level " + currentLevelNumber + " completed!\n");
            
            if (currentLevelNumber < 12) {
                appendLog.accept("Level " + (currentLevelNumber + 1) + " unlocked!\n");
            } else {
                appendLog.accept("🏆 Congratulations! You've completed all levels!\n");
            }
        } else {
            appendLog.accept("[ERROR] Player data is null!\n");
        }
        
        // Show victory dialog
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Victory!");
            alert.setHeaderText("🎉 Level " + currentLevelNumber + " Complete!");
            alert.setContentText("""
                Rewards earned:
                • %d Gold
                %s""".formatted(goldReward, 
                currentLevelNumber < 12 ? "• Level " + (currentLevelNumber + 1) + " unlocked!" : ""));
            alert.showAndWait();
            
            // Return to campaign screen to show progress
            if (app != null) {
                try {
                    app.switchToCampaign(currentPlayerName);
                } catch (Exception e) {
                    app.switchToMainMenu(currentPlayerName);
                }
            }
        });
    }
    
    private void handleDefeat() {
        appendLog.accept("💀 Defeat! Better luck next time.\n");
        
        // Show defeat dialog
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Defeat");
            alert.setHeaderText("💀 Mission Failed");
            alert.setContentText("Your team has been defeated.\nTry upgrading your characters in the shop and try again!");
            alert.showAndWait();
            
            // Return to campaign screen
            if (app != null) {
                try {
                    app.switchToCampaign(currentPlayerName);
                } catch (Exception e) {
                    app.switchToMainMenu(currentPlayerName);
                }
            }
        });
    }
    
    private int calculateGoldReward() {
        // Base reward scales with level difficulty
        int baseReward = Math.max(50, currentLevelNumber * 25);
        
        // Bonus for keeping team alive
        int aliveBonus = 0;
        if (human != null) {
            aliveBonus = human.aliveTeam().size() * 10;
        }
        
        return baseReward + aliveBonus;
    }
    
    public void startTurnTimer() {
        secondsLeft = 60;
        updateTimerHud();
        turnTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateTimerHud();
            if (secondsLeft <= 0) {
                turnTimer.stop();
                if (onEndTurn != null) {
                    onEndTurn.run();
                }
            }
        }));
        turnTimer.setCycleCount(Timeline.INDEFINITE);
        turnTimer.playFromStart();
    }

    public void restartTurnTimer() {
        if (turnTimer != null) turnTimer.stop();
        startTurnTimer();
    }

    private void updateTimerHud() {
        if (timerLabel != null) timerLabel.setText(Integer.toString(Math.max(0, secondsLeft)));
        if (timerProgress != null) {
            double progress = Math.max(0.0, Math.min(1.0, secondsLeft / 60.0));
            timerProgress.setProgress(progress);
            
            // Change color based on remaining time
            if (secondsLeft <= 10) {
                timerProgress.getStyleClass().removeAll("timer-bar");
                timerProgress.getStyleClass().add("timer-bar-critical");
            } else if (secondsLeft <= 20) {
                timerProgress.getStyleClass().removeAll("timer-bar", "timer-bar-critical");
                timerProgress.getStyleClass().add("timer-bar-warning");
            } else {
                timerProgress.getStyleClass().removeAll("timer-bar-warning", "timer-bar-critical");
                timerProgress.getStyleClass().add("timer-bar");
            }
        }
    }
    
    public void stopTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }
    
    public boolean isGameEnded() {
        return gameEnded;
    }
    
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }
}