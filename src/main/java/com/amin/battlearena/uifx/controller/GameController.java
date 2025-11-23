package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.*;
import com.amin.battlearena.domain.character.GameCharacter;
import com.amin.battlearena.domain.character.Warrior;
import com.amin.battlearena.domain.character.Archer;
import com.amin.battlearena.domain.character.Mage;
import com.amin.battlearena.domain.character.Enemy;
import com.amin.battlearena.domain.ability.Ability;
import com.amin.battlearena.domain.consumable.Consumable;
import com.amin.battlearena.domain.consumable.HealthPotion;
import com.amin.battlearena.domain.consumable.ItemFactory;
import com.amin.battlearena.engine.AiEngine;
import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.infra.SceneManager;
import com.amin.battlearena.persistence.AccountRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class GameController {

    @FXML private Label turnLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label timerLabel;
    @FXML private ProgressBar timerProgress;
    
    @FXML private GridPane boardGrid;
    
    @FXML private Label selName;
    @FXML private Label selClass;
    @FXML private Label selHp;
    @FXML private ProgressBar healthBar;
    @FXML private Label selMana;
    @FXML private ProgressBar manaBar;
    @FXML private Label selAttack;
    @FXML private Label selDefense;
    @FXML private Label selRange;
    @FXML private Label selMovesLeft;
    @FXML private Label selAttacksLeft;
    
    @FXML private VBox abilitiesContainer;
    @FXML private Button useAbilityBtn;
    
    @FXML private VBox consumablesContainer;
    @FXML private Button useConsumableBtn;
    
    @FXML private Label playerUnitsAlive;
    @FXML private Label cpuUnitsAlive;
    
    @FXML private TextArea logArea;
    
    @FXML private ToggleButton moveBtn;
    @FXML private ToggleButton attackBtn;
    @FXML private Button endTurnBtn;

    private static int targetLevel = 1;
    public static void setTargetLevel(int level) { targetLevel = level; }

    private GameEngine engine;
    private AiEngine aiEngine;
    private GameCharacter selectedCharacter;
    private Ability selectedAbility;
    private String selectedItem;
    
    private final int TILE_SIZE = 50;
    
    private Timeline gameTimer;
    private int timeLeft = 60;

    @FXML
    public void initialize() {
        engine = new GameEngine();
        aiEngine = new AiEngine();
        log("Initializing Level " + targetLevel + "...");
        engine.initLevel(targetLevel);
        
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
        
        renderBoard();
        updateUI();
        updateConsumablesList();
    }

    private void updateTimer() {
        if (engine.isPlayerTurn()) {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));
            timerProgress.setProgress(timeLeft / 60.0);
            if (timeLeft <= 0) {
                log("Time's up!");
                onEndTurn();
            }
        } else {
            timerLabel.setText("-");
            timerProgress.setProgress(0);
        }
    }

    @FXML
    public void onStartBattle() {
        log("Battle Started!");
        updateUI();
    }

    private void renderBoard() {
        boardGrid.getChildren().clear();
        Board board = engine.getBoard();
        
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                StackPane tileView = createTileView(x, y);
                boardGrid.add(tileView, x, y);
            }
        }
    }

    private StackPane createTileView(int x, int y) {
        StackPane stack = new StackPane();
        stack.setPrefSize(TILE_SIZE, TILE_SIZE);
        
        Rectangle bg = new Rectangle(TILE_SIZE, TILE_SIZE);
        bg.setFill(Color.LIGHTGRAY);
        bg.setStroke(Color.BLACK);
        stack.getChildren().add(bg);
        
        Tile tile = engine.getBoard().getTile(x, y);
        if (tile.isOccupied()) {
            GameCharacter c = tile.getOccupant();
            Label charIcon = new Label(c.getIcon());
            charIcon.setFont(new Font(24));
            if (c.isPlayerTeam()) {
                charIcon.setTextFill(Color.BLUE);
            } else {
                charIcon.setTextFill(Color.RED);
            }
            stack.getChildren().add(charIcon);
            
            // HP Bar removed as requested
            stack.setAlignment(javafx.geometry.Pos.CENTER);
        }
        
        stack.setOnMouseClicked(e -> onTileClicked(x, y));
        return stack;
    }

    private void onTileClicked(int x, int y) {
        Tile tile = engine.getBoard().getTile(x, y);
        
        if (moveBtn.isSelected()) {
            handleMoveClick(tile, x, y);
            return;
        } 
        
        if (attackBtn.isSelected()) {
            handleAttackClick(tile);
            return;
        }

        handleSelectionClick(tile);
    }

    private void handleMoveClick(Tile tile, int x, int y) {
        if (selectedCharacter == null || !selectedCharacter.isPlayerTeam() || !engine.isPlayerTurn()) return;

        if (engine.moveCharacter(selectedCharacter, tile)) {
            log(selectedCharacter.getName() + " moved to (" + x + "," + y + ")");
            moveBtn.setSelected(false);
            renderBoard();
            updateUI();
        } else {
            log("Invalid move!");
        }
    }

    private void handleAttackClick(Tile tile) {
        if (selectedCharacter == null || !selectedCharacter.isPlayerTeam() || !engine.isPlayerTurn()) return;
        if (!tile.isOccupied() || tile.getOccupant().isPlayerTeam()) return;

        if (engine.attackCharacter(selectedCharacter, tile.getOccupant(), selectedAbility)) {
            log(selectedCharacter.getName() + " attacked " + tile.getOccupant().getName());
            attackBtn.setSelected(false);
            selectedAbility = null;
            useAbilityBtn.setDisable(true);
            renderBoard();
            updateUI();
        } else {
            log("Attack failed (Out of range or no actions left)!");
        }
    }

    private void handleSelectionClick(Tile tile) {
        if (tile.isOccupied()) {
            selectedCharacter = tile.getOccupant();
        } else {
            selectedCharacter = null;
        }
        updateSelectionUI();
    }

    private void updateSelectionUI() {
        if (selectedCharacter != null) {
            selName.setText(selectedCharacter.getName());
            selClass.setText(selectedCharacter.getType());
            selHp.setText(selectedCharacter.getCurrentHp() + "/" + selectedCharacter.getMaxHp());
            healthBar.setProgress((double)selectedCharacter.getCurrentHp() / selectedCharacter.getMaxHp());
            healthBar.setStyle("-fx-accent: green;");
            
            selMana.setText(selectedCharacter.getCurrentMana() + "/" + selectedCharacter.getMaxMana());
            manaBar.setProgress((double)selectedCharacter.getCurrentMana() / selectedCharacter.getMaxMana());
            manaBar.setStyle("-fx-accent: blue;");
            
            selAttack.setText(String.valueOf(selectedCharacter.getAttack()));
            selDefense.setText(String.valueOf(selectedCharacter.getDefense()));
            selRange.setText(String.valueOf(selectedCharacter.getRange()));
            selMovesLeft.setText(String.valueOf(selectedCharacter.getMovesLeft()));
            selAttacksLeft.setText(String.valueOf(selectedCharacter.getAttacksLeft()));
            
            healthBar.setVisible(true);
            manaBar.setVisible(true);
            
            updateAbilitiesList();
        } else {
            selName.setText("No Selection");
            selClass.setText("-");
            selHp.setText("-/-");
            healthBar.setVisible(false);
            selMana.setText("-/-");
            manaBar.setVisible(false);
            selAttack.setText("-");
            selDefense.setText("-");
            selRange.setText("-");
            selMovesLeft.setText("-");
            selAttacksLeft.setText("-");
            abilitiesContainer.getChildren().clear();
        }
    }

    private void updateAbilitiesList() {
        abilitiesContainer.getChildren().clear();
        if (selectedCharacter == null) return;
        
        for (Ability a : selectedCharacter.getAbilities()) {
            Button btn = new Button(a.getName() + " (" + a.getManaCost() + " MP)");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> {
                selectedAbility = a;
                useAbilityBtn.setDisable(false);
                useAbilityBtn.setText("Use " + a.getName());
                log("Selected ability: " + a.getName());
            });
            if (!a.isReady() || selectedCharacter.getCurrentMana() < a.getManaCost()) {
                btn.setDisable(true);
            }
            abilitiesContainer.getChildren().add(btn);
        }
    }

    @FXML
    public void onUseSelectedAbility() {
        if (selectedAbility != null) {
            attackBtn.setSelected(true);
            moveBtn.setSelected(false);
            log("Select a target for " + selectedAbility.getName());
        }
    }

    @FXML
    public void onModeMove() {
        if (moveBtn.isSelected()) {
            attackBtn.setSelected(false);
            log("Move Mode: Select a tile to move to.");
        }
    }

    @FXML
    public void onModeAttack() {
        if (attackBtn.isSelected()) {
            moveBtn.setSelected(false);
            selectedAbility = null; // Basic attack
            useAbilityBtn.setDisable(true);
            useAbilityBtn.setText("Use Selected Ability");
            log("Attack Mode: Select an enemy to attack.");
        }
    }

    @FXML
    public void onEndTurn() {
        if (engine.isPlayerTurn()) {
            log("Player Turn Ended.");
            engine.endTurn();
            timeLeft = 60; // Reset timer
            updateUI();
            
            // CPU Turn
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay for effect
                    Platform.runLater(() -> {
                        log("CPU Turn...");
                        aiEngine.performTurn(engine);
                        engine.endTurn();
                        log("Player Turn Started.");
                        renderBoard();
                        updateUI();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void updateUI() {
        turnLabel.setText(String.valueOf(engine.getTurnCount()));
        currentPlayerLabel.setText(engine.isPlayerTurn() ? "Player" : "CPU");
        
        int pCount = 0;
        int eCount = 0;
        for (Character c : engine.getAllCharacters()) {
            if (c.isPlayerTeam()) pCount++;
            else eCount++;
        }
        playerUnitsAlive.setText(pCount + " Alive");
        cpuUnitsAlive.setText(eCount + " Alive");
        
        if (engine.isGameOver()) {
            if (engine.didPlayerWin()) {
                log("VICTORY! You won the battle!");
                Player p = AccountRepository.getInstance().getCurrentUser();
                if (p != null) {
                    p.addGold(50 + (targetLevel * 25));
                    p.addVictory();
                    if (targetLevel == p.getCampaignProgress()) {
                        p.unlockNextLevel();
                    }
                    AccountRepository.getInstance().savePlayer(p);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Victory! You earned gold.");
                alert.showAndWait();
                onBack();
            } else {
                log("DEFEAT! You lost the battle.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Defeat! Try again.");
                alert.showAndWait();
                onBack();
            }
        }
        
        updateSelectionUI();
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    @FXML
    public void onPause() {
        log("Game Paused.");
    }

    @FXML
    public void onBack() {
        if (gameTimer != null) gameTimer.stop();
        SceneManager.getInstance().switchScene("/uifx/campaign.fxml");
    }
    
    private void updateConsumablesList() {
        if (consumablesContainer == null) return;
        consumablesContainer.getChildren().clear();
        
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p == null) return;
        
        Map<String, Integer> inventory = p.getInventory();
        if (inventory.isEmpty()) {
            Label empty = new Label("No items");
            empty.setStyle("-fx-text-fill: gray;");
            consumablesContainer.getChildren().add(empty);
            return;
        }
        
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String itemName = entry.getKey();
            int count = entry.getValue();
            
            Button btn = new Button(itemName + " x" + count);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.getStyleClass().add("consumable-item");
            btn.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"); // Ensure visibility
            btn.setOnAction(e -> {
                selectedItem = itemName;
                useConsumableBtn.setDisable(false);
                useConsumableBtn.setText("Use " + itemName);
                log("Selected item: " + itemName);
            });
            consumablesContainer.getChildren().add(btn);
        }
    }

    @FXML 
    public void onUseConsumable() {
        if (selectedItem == null) return;
        
        if (selectedCharacter != null && selectedCharacter.isPlayerTeam() && engine.isPlayerTurn()) {
             Player p = AccountRepository.getInstance().getCurrentUser();
             if (p != null && p.hasItem(selectedItem)) {
                 Consumable item = ItemFactory.createItem(selectedItem);
                 if (item != null) {
                     item.use(selectedCharacter);
                     p.useItem(selectedItem);
                     AccountRepository.getInstance().savePlayer(p);
                     
                     log("Used " + selectedItem + " on " + selectedCharacter.getName());
                     updateUI();
                     updateConsumablesList();
                     
                     if (!p.hasItem(selectedItem)) {
                         selectedItem = null;
                         useConsumableBtn.setDisable(true);
                         useConsumableBtn.setText("Use Selected Consumable");
                     }
                 } else {
                     log("Item effect not implemented: " + selectedItem);
                 }
             }
        } else {
            log("Select a player character to use item.");
        }
    }
    @FXML public void onClearLog() { logArea.clear(); }
    @FXML public void onExportLog() {}
}
