package com.amin.battlearena.uifx.controller;


import java.util.List;

import com.amin.battlearena.domain.level.LevelRepository;
import com.amin.battlearena.domain.level.LevelSpec;
import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.ai.SimpleAIStrategy;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.engine.memento.GameCaretaker;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.players.AIPlayer;
import com.amin.battlearena.players.HumanPlayer;
import com.amin.battlearena.uifx.MainApp;
import com.amin.battlearena.uifx.handler.AbilityUIHandler;
import com.amin.battlearena.uifx.handler.BoardRenderHandler;
import com.amin.battlearena.uifx.handler.UIStateHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameController implements Initializable {
    private MainApp app;
    private String currentPlayerName = "Player";
    private int currentLevelNumber = 1;
    private LevelRepository levelRepository = new LevelRepository();
    
    // Main UI Components
    @FXML private TextArea logArea;
    @FXML private GridPane boardGrid;
    @FXML private ProgressBar timerProgress;
    
    // Status Labels
    @FXML private Label turnLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label timerLabel;
    
    // Character Info
    @FXML private Label selName;
    @FXML private Label selClass;
    @FXML private Label selHp;
    @FXML private Label selMana;
    @FXML private Label selAttack;
    @FXML private Label selDefense;

    @FXML private Label selRange;
    @FXML private Label selMovesLeft;
    @FXML private Label selAttacksLeft;
    @FXML private ProgressBar healthBar;
    @FXML private ProgressBar manaBar;
    
    // Team Info
    @FXML private Label playerUnitsAlive;
    @FXML private Label cpuUnitsAlive;
    
    // Action Buttons
    @FXML private ToggleButton moveBtn;
    @FXML private ToggleButton attackBtn;
    @FXML private ToggleButton abilityBtn;
    @FXML private Button setupBtn;
    
    // Quick Action Buttons
    @FXML private Button undoBtn;
    @FXML private Button redoBtn;

    @FXML private Button consumableBtn;
    
    // Abilities
    @FXML private ListView<String> abilitiesList;
    @FXML private VBox abilitiesContainer;
    @FXML private Button useAbilityBtn;
    
    // Consumables
    @FXML private VBox consumablesContainer;
    @FXML private Button useConsumableBtn;
    
    private enum Mode { SELECT, MOVE, ATTACK, ABILITY, CONSUMABLE }
    private Mode mode = Mode.SELECT;

    private Board board;
    private HumanPlayer human;
    private AIPlayer cpu;
    private GameEngine engine;
    private final GameCaretaker caretaker = new GameCaretaker();

    private com.amin.battlearena.domain.model.Character selected;
    private int currentTurn = 1;
    private final java.util.Map<com.amin.battlearena.domain.model.Character, Integer> movesLeft = new java.util.HashMap<>();
    private final java.util.Map<com.amin.battlearena.domain.model.Character, Integer> attacksLeft = new java.util.HashMap<>();
    private transient javafx.animation.Timeline turnTimer;
    private int secondsLeft = 60;
    private final java.util.Map<Position, Integer> deathFx = new java.util.HashMap<>();
    private int selectedAbilityIndex = -1;
    
    // Delegated handlers for OOP separation of concerns
    private AbilityUIHandler abilityHandler;
    private BoardRenderHandler boardHandler;
    private UIStateHandler stateHandler;
    private CombatActionHandler combatHandler;
    private TurnFlowHandler turnHandler;
    
    // Auto-play mode
    private boolean gameEnded = false;
    
    public void setApp(MainApp app) {
        this.app = app;
        
        // Hide the ability button from top toolbar (issue #11)
        if (abilityBtn != null) {
            abilityBtn.setVisible(false);
            abilityBtn.setManaged(false);
        }
        
        // Initialize visual effects
        // The board will be available after onSetupDemo is called
    }
    
    public void setPlayerName(String playerName) {
        this.currentPlayerName = playerName;
    }
    
    public void setLevelNumber(int levelNumber) {
        this.currentLevelNumber = levelNumber;
        // Auto-start battle when level is set
        if (currentPlayerName != null && levelNumber > 0) {
            Platform.runLater(() -> onStartBattle());
        }
    }

    @FXML private void onStartBattle() {
        gameEnded = false; // Reset game ended flag
        setupLevelBasedBattle();
        // Auto-start the battle and timer
        startTurnTimer();
        setupBtn.setText("🔄 Restart Battle");
        
        // Show battle start dialogue
        // MasterDialogueManager.getInstance().showDialogue("battle_start", 
        //     boardGrid.getScene().getWindow(), 
        //     boardGrid.getScene().getWidth() / 2, 100); // Method signature issue, commenting out
    }
    
    private void setupLevelBasedBattle() {
        this.board = new Board(12, 8);
        this.human = new HumanPlayer(currentPlayerName);
        this.cpu = new AIPlayer("CPU", new SimpleAIStrategy());

        // Load player data and apply upgrades
        com.amin.battlearena.persistence.PlayerData playerData = 
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
        
        // Create player team with upgrades applied
        setupPlayerTeam(playerData);
        
        // Create level-appropriate enemy team
        setupEnemyTeam(currentLevelNumber);

        this.engine = new GameEngine(human, cpu, board);
        caretaker.saveState(engine);

        renderBoard();
        append("Level " + currentLevelNumber + " battle started! Select a unit, then Move or Attack.\n");
        enableKeyboardShortcuts();
        resetBudgetsForTurn();
        startTurnTimer();
        updateUndoRedoButtons();
        
        // Audio and dialogue systems removed for simplified architecture
    }
    
    private void setupPlayerTeam(com.amin.battlearena.persistence.PlayerData playerData) {
        // Create base characters using CharacterFactory for extensibility
        com.amin.battlearena.domain.model.Character warrior = 
            com.amin.battlearena.domain.model.CharacterFactory.create("warrior", "Garen", new Position(0, 2));
        com.amin.battlearena.domain.model.Character archer = 
            com.amin.battlearena.domain.model.CharacterFactory.create("archer", "Ashe", new Position(0, 3));
        com.amin.battlearena.domain.model.Character mage = 
            com.amin.battlearena.domain.model.CharacterFactory.create("mage", "Ryze", new Position(1, 3));
        com.amin.battlearena.domain.model.Character knight = 
            com.amin.battlearena.domain.model.CharacterFactory.create("knight", "Braum", new Position(0, 4));
        
        // Apply purchased upgrades if player data exists
        if (playerData != null) {
            com.amin.battlearena.economy.Shop shop = new com.amin.battlearena.economy.Shop(
                new com.amin.battlearena.economy.UpgradeCatalog());
            shop.applyPurchasedUpgrades(warrior, playerData);
            shop.applyPurchasedUpgrades(archer, playerData);
            shop.applyPurchasedUpgrades(mage, playerData);
            shop.applyPurchasedUpgrades(knight, playerData);
        }
        
        human.addToTeam(warrior);
        human.addToTeam(archer);
        human.addToTeam(mage);
        human.addToTeam(knight);
    }
    
    private void setupEnemyTeam(int level) {
        // Load level configuration from repository
        String levelId = String.format("L%02d", level);
        LevelSpec levelSpec;
        
        try {
            levelSpec = levelRepository.require(levelId);
        } catch (IllegalArgumentException e) {
            append("Error: Level " + levelId + " not found. Using fallback configuration.\n");
            createFallbackEnemies(level);
            return;
        }
        
        // Create enemies from level specification
        List<String> enemies = levelSpec.enemies();
        List<List<Integer>> positions = levelSpec.enemyPositions();
        
        if (enemies.size() != positions.size()) {
            append("Error: Enemy count mismatch in level " + levelId + ". Using fallback.\n");
            createFallbackEnemies(level);
            return;
        }
        
        // Calculate stats based on level difficulty
        int baseHp = 80 + (level * 15);
        int baseAttack = 10 + (level * 2);
        int baseDefense = 5 + level;
        
        for (int i = 0; i < enemies.size(); i++) {
            String enemyType = enemies.get(i);
            List<Integer> pos = positions.get(i);
            int row = pos.get(0);
            int col = pos.get(1);
            
            String enemyName = enemyType + "-" + (i + 1);
            com.amin.battlearena.domain.model.Character enemy = 
                com.amin.battlearena.domain.model.CharacterFactory.create(
                    enemyType.toLowerCase(), enemyName, new Position(row, col));
            
            // Scale stats based on enemy type
            if (enemyType.equalsIgnoreCase("mage")) {
                scaleEnemyStats(enemy, baseHp - 20, baseAttack + 3, baseDefense - 1);
            } else if (enemyType.equalsIgnoreCase("archer")) {
                scaleEnemyStats(enemy, baseHp - 10, baseAttack + 2, baseDefense);
            } else if (enemyType.equalsIgnoreCase("knight")) {
                scaleEnemyStats(enemy, baseHp + 20, baseAttack, baseDefense + 3);
            } else if (enemyType.equalsIgnoreCase("master") || 
                       enemyType.equalsIgnoreCase("dragon") ||
                       enemyType.equalsIgnoreCase("warlord")) {
                scaleEnemyStats(enemy, baseHp + 100, baseAttack + 8, baseDefense + 8);
            } else {
                scaleEnemyStats(enemy, baseHp, baseAttack, baseDefense);
            }
            
            cpu.addToTeam(enemy);
        }
    }
    
    private void createFallbackEnemies(int level) {
        // Fallback enemy creation if JSON fails
        int baseHp = 80 + (level * 15);
        int baseAttack = 10 + (level * 2);
        int baseDefense = 5 + level;
        
        com.amin.battlearena.domain.model.Character enemy1 = 
            com.amin.battlearena.domain.model.CharacterFactory.create("warrior", "Enemy-1", 
                new Position(board.getWidth() - 1, 2));
        scaleEnemyStats(enemy1, baseHp, baseAttack, baseDefense);
        cpu.addToTeam(enemy1);
        
        if (level > 1) {
            com.amin.battlearena.domain.model.Character enemy2 = 
                com.amin.battlearena.domain.model.CharacterFactory.create("archer", "Enemy-2", 
                    new Position(board.getWidth() - 1, 3));
            scaleEnemyStats(enemy2, baseHp - 10, baseAttack + 2, baseDefense);
            cpu.addToTeam(enemy2);
        }
    }
    
    private void scaleEnemyStats(com.amin.battlearena.domain.model.Character enemy, int hp, int attack, int defense) {
        enemy.getStats().setMaxHp(hp);
        enemy.getStats().setHp(hp);
        enemy.getStats().setAttack(attack);
        enemy.getStats().setDefense(defense);
    }

    @FXML private void onModeMove() { 
        setMode(Mode.MOVE);
        append("🏃 Switched to Movement mode\n"); 
        updateModeButtons();
        renderBoard(); 
    }
    
    @FXML private void onModeAttack() { 
        setMode(Mode.ATTACK);
        append("⚔ Switched to Attack mode\n"); 
        updateModeButtons();
        renderBoard(); 
    }
    
    
    // Removed unused method onUseSelectedAbility
    // Removed unused method onUseSelectedAbility
    
    private void setMode(Mode newMode) {
        mode = newMode;
        
        // Delegate UI state updates to handler (OOP delegation pattern)
        if (stateHandler != null) {
            stateHandler.setMode(newMode.toString());
        }
    }
    
    private void updateModeButtons() {
        if (moveBtn != null) moveBtn.setSelected(mode == Mode.MOVE);
        if (attackBtn != null) attackBtn.setSelected(mode == Mode.ATTACK);
        // Hide ability button - abilities are now activated via "Use Selected Ability" button
        if (abilityBtn != null) {
            abilityBtn.setVisible(false);
            abilityBtn.setManaged(false);
        }
    }
    @FXML private void onEndTurn() {
        mode = Mode.SELECT;
        append("Turn ended.\n");
        
        // End turn housekeeping for player characters (cooldown reduction, status effects, etc.)
        if (human != null) {
            for (com.amin.battlearena.domain.model.Character character : human.getTeam()) {
                if (character.isAlive()) {
                    character.endTurnHousekeeping();
                }
            }
        }
        
        // Regenerate mana for all characters
        regenerateManaForAllCharacters();
        
        currentTurn++;
        updateTurnHud();
        // simple CPU action stub
        runCpuTurn();
        // Reset budgets and timer for next player turn
        resetBudgetsForTurn();
        restartTurnTimer();
        
        // Update ability display to reflect new cooldown states
        updateSelectedHud();
    }
    
    private void regenerateManaForAllCharacters() {
        // Regenerate mana for player team
        for (com.amin.battlearena.domain.model.Character character : human.getTeam()) {
            if (character.isAlive()) {
                int regenAmount = character.getManaRegenPerTurn();
                if (regenAmount > 0) {
                    int oldMana = character.getCurrentMana();
                    character.restoreMana(regenAmount);
                    int newMana = character.getCurrentMana();
                    if (newMana > oldMana) {
                        append(character.getName() + " regenerated " + (newMana - oldMana) + " mana.\n");
                    }
                }
            }
        }
        
        // Regenerate mana for CPU team
        for (com.amin.battlearena.domain.model.Character character : cpu.getTeam()) {
            if (character.isAlive()) {
                int regenAmount = character.getManaRegenPerTurn();
                if (regenAmount > 0) {
                    character.restoreMana(regenAmount);
                }
            }
        }
    }

    // Removed unused method onPause

    private void updateTurnHud() {
        if (turnLabel != null) turnLabel.setText(Integer.toString(currentTurn));
        if (currentPlayerLabel != null) currentPlayerLabel.setText(selected != null && human.getTeam().contains(selected) ? human.getName() : "CPU");
    }

    private void enableKeyboardShortcuts() {
        // Attach to scene when available
        var scene = logArea.getScene();
        if (scene == null) return;
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.M) { onModeMove(); e.consume(); }
            else if (e.getCode() == KeyCode.A) { onModeAttack(); e.consume(); }
            else if (e.getCode() == KeyCode.ENTER) { /* No-op: could confirm action in future */ e.consume(); }
            else if (e.getCode() == KeyCode.I) { useConsumable(); e.consume(); }
            else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) { cycleSelection(e.getCode()); e.consume(); }
        });
    }

    private void cycleSelection(KeyCode code) {
        if (human == null || human.getTeam().isEmpty()) return;
        var team = human.getTeam();
        int idx = selected == null ? -1 : team.indexOf(selected);
        if (code == KeyCode.RIGHT || code == KeyCode.DOWN) idx = (idx + 1) % team.size();
        else idx = (idx - 1 + team.size()) % team.size();
        selected = team.get(idx);
        updateSelectedHud();
        renderBoard();
        updateControls();
    }

    private void append(String text) {
        Platform.runLater(() -> logArea.appendText(text));
    }


    private void renderBoard() {
        if (board == null) return;
        boardGrid.getChildren().clear();
        var moveTargets = computeMoveTargets();
        var attackTargets = computeAttackTargets();
        var abilityTargets = computeAbilityTargets();
        var consumableTargets = computeConsumableTargets();
        // decay death FX
        java.util.List<Position> toRemove = new java.util.ArrayList<>();
        for (var e : deathFx.entrySet()) {
            int v = e.getValue() - 1;
            if (v <= 0) toRemove.add(e.getKey()); else e.setValue(v);
        }
        toRemove.forEach(deathFx::remove);
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(50, 50); // Increased from 40x40 to 50x50 for better visibility
                Rectangle base = new Rectangle(48, 48); // Increased from 40x40 to match cell size
                base.setFill(((x + y) % 2 == 0) ? Color.BEIGE : Color.LIGHTGRAY);
                base.setStroke(Color.DARKGRAY);
                final int cx = x, cy = y;
                cellPane.setOnMouseClicked(e -> onCellClicked(cx, cy));

                com.amin.battlearena.domain.model.Character occupant = findCharacterAt(x, y);
                boolean highlightMove = moveTargets[cx][cy];
                boolean highlightAtk = attackTargets[cx][cy];
                boolean highlightAbility = abilityTargets[cx][cy];
                boolean highlightConsumable = consumableTargets[cx][cy];

                // Keep base fill for side colors; draw highlights as stroked overlay so enemies remain visible
                Rectangle hl = null;
                if (highlightMove) {
                    hl = new Rectangle(48, 48);
                    hl.setFill(Color.TRANSPARENT);
                    hl.setStroke(Color.LIMEGREEN);
                    hl.setStrokeWidth(2.0);
                } else if (highlightAtk) {
                    hl = new Rectangle(48, 48);
                    hl.setFill(Color.TRANSPARENT);
                    hl.setStroke(Color.RED);
                    hl.setStrokeWidth(2.0);
                } else if (highlightAbility) {
                    hl = new Rectangle(48, 48);
                    hl.setFill(Color.TRANSPARENT);
                    hl.setStroke(Color.MEDIUMPURPLE);
                    hl.setStrokeWidth(2.0);
                } else if (highlightConsumable) {
                    hl = new Rectangle(48, 48);
                    hl.setFill(Color.TRANSPARENT);
                    hl.setStroke(Color.GOLD);
                    hl.setStrokeWidth(2.0);
                }

                if (occupant != null) {
                    boolean isHuman = human != null && human.getTeam().contains(occupant);
                    base.setFill(isHuman ? Color.LIGHTBLUE : Color.LIGHTSALMON);

                    // HP bar
                    int hp = occupant.getStats().getHp();
                    int max = occupant.getStats().getMaxHp();
                    double ratio = Math.max(0, Math.min(1.0, (max == 0 ? 0 : (hp * 1.0 / max))));
                    Rectangle hpBg = new Rectangle(40, 4, Color.DARKGRAY);
                    Rectangle hpBar = new Rectangle(40 * ratio, 4, Color.LIMEGREEN);
                    StackPane.setAlignment(hpBg, Pos.TOP_LEFT);
                    StackPane.setAlignment(hpBar, Pos.TOP_LEFT);
                    StackPane.setMargin(hpBg, new Insets(2, 0, 0, 2));
                    StackPane.setMargin(hpBar, new Insets(2, 0, 0, 2));
                    
                    // Mana bar
                    int currentMana = occupant.getCurrentMana();
                    int maxMana = occupant.getMaxMana();
                    double manaRatio = Math.max(0, Math.min(1.0, (maxMana == 0 ? 0 : (currentMana * 1.0 / maxMana))));
                    Rectangle manaBg = new Rectangle(40, 4, Color.DARKGRAY);
                    Rectangle manaBar = new Rectangle(40 * manaRatio, 4, Color.DODGERBLUE);
                    StackPane.setAlignment(manaBg, Pos.TOP_LEFT);
                    StackPane.setAlignment(manaBar, Pos.TOP_LEFT);
                    StackPane.setMargin(manaBg, new Insets(8, 0, 0, 2)); // 8 pixels down from top (below HP bar)
                    StackPane.setMargin(manaBar, new Insets(8, 0, 0, 2));
                    
                    // Character symbol
                    Label characterSymbol = new Label(getCharacterSymbol(occupant));
                    characterSymbol.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                    characterSymbol.setTextFill(Color.WHITE);
                    characterSymbol.setStyle("-fx-effect: dropshadow(gaussian, black, 2, 0.5, 1, 1);");
                    StackPane.setAlignment(characterSymbol, Pos.CENTER);
                    Rectangle selOutline = null;
                    if (selected != null && occupant == selected) {
                        selOutline = new Rectangle(40, 40);
                        selOutline.setFill(Color.TRANSPARENT);
                        selOutline.setStroke(Color.GOLD);
                        selOutline.setStrokeWidth(2.0);
                    }
                    boolean spent = movesLeft.getOrDefault(occupant, movesPerTurn(occupant)) == 0
                            && attacksLeft.getOrDefault(occupant, attacksPerTurn(occupant)) == 0;
                    Rectangle spentOverlay = null;
                    if (spent) {
                        spentOverlay = new Rectangle(40, 40);
                        spentOverlay.setFill(new Color(0, 0, 0, 0.35));
                    }
                    if (hl != null && selOutline != null && spentOverlay != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, hl, selOutline, spentOverlay);
                    else if (hl != null && selOutline != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, hl, selOutline);
                    else if (hl != null && spentOverlay != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, hl, spentOverlay);
                    else if (selOutline != null && spentOverlay != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, selOutline, spentOverlay);
                    else if (hl != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, hl);
                    else if (selOutline != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, selOutline);
                    else if (spentOverlay != null) cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar, spentOverlay);
                    else cellPane.getChildren().addAll(base, characterSymbol, hpBg, hpBar, manaBg, manaBar);
                } else {
                    Rectangle fx = null;
                    Integer ticks = deathFx.get(new Position(x, y));
                    if (ticks != null && ticks > 0) {
                        fx = new Rectangle(40, 40);
                        fx.setFill(new Color(1, 0, 0, Math.min(0.5, ticks / 15.0)));
                    }
                    if (hl != null && fx != null) cellPane.getChildren().addAll(base, hl, fx);
                    else if (hl != null) cellPane.getChildren().addAll(base, hl);
                    else if (fx != null) cellPane.getChildren().addAll(base, fx);
                    else cellPane.getChildren().add(base);
                }

                boardGrid.add(cellPane, x, y);
            }
        }
    }

    private com.amin.battlearena.domain.model.Character findCharacterAt(int x, int y) {
        if (human != null) {
            for (var c : human.getTeam()) if (c.isAlive() && c.getPosition().x() == x && c.getPosition().y() == y) return c;
        }
        if (cpu != null) {
            for (var c : cpu.getTeam()) if (c.isAlive() && c.getPosition().x() == x && c.getPosition().y() == y) return c;
        }
        return null;
    }
    
    private String getCharacterSymbol(com.amin.battlearena.domain.model.Character character) {
        String className = character.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        if ("warrior".equals(className)) return "⚔️";
        else if ("archer".equals(className)) return "🏹";
        else if ("mage".equals(className)) return "🔮";
        else if ("knight".equals(className)) return "🛡️";
        else if ("ranger".equals(className)) return "🌿";
        else if ("master".equals(className)) return "👑";
        else return "❓";
    }
    


    private void onTileClick(Integer row, Integer col) {
        onCellClicked(row, col);
    }
    
    private void onCellClicked(int x, int y) {
        com.amin.battlearena.domain.model.Character occupant = findCharacterAt(x, y);
        
        // Allow enemy selection when game has ended (issue #5)
        if (gameEnded && occupant != null) {
            selected = occupant;
            append("Inspecting: " + selected.getName() + " (" + 
                   (human != null && human.getTeam().contains(occupant) ? "Ally" : "Enemy") + ")\n");
            updateSelectedHud();
            renderBoard();
            return;
        }
        
        // In most modes: clicking an allied unit selects it; in consumable mode, apply to the ally
        if (occupant != null && human != null && human.getTeam().contains(occupant)) {
            if (mode == Mode.CONSUMABLE) {
                handleConsumableUse(occupant);
                return;
            }
            selected = occupant;
            append("Selected: " + selected.getName() + "\n");
            updateSelectedHud();
            renderBoard();
            updateControls();
            return;
        }
        
        // Show enemy details when clicked (inspection mode)
        if (occupant != null && cpu != null && cpu.getTeam().contains(occupant)) {
            // Allow enemy inspection in early turns, when game ended, or when explicitly in inspect mode
            if (currentTurn <= 2 || gameEnded || mode == Mode.SELECT) {
                selected = occupant;
                append("🔍 Inspecting Enemy: " + occupant.getName() + " - HP: " + occupant.getStats().getHp() + "/" + 
                       occupant.getStats().getMaxHp() + ", ATK: " + occupant.getStats().getAttack() + 
                       ", DEF: " + occupant.getStats().getDefense() + ", Mana: " + occupant.getCurrentMana() + 
                       "/" + occupant.getMaxMana() + "\n");
                updateSelectedHud();
                renderBoard();
                updateControls();
                return;
            }
        }
        
        if (mode == Mode.SELECT) {
            if (occupant != null && human != null && human.getTeam().contains(occupant)) {
                selected = occupant;
                append("Selected: " + selected.getName() + "\n");
                updateSelectedHud();
            } else {
                selected = null;
                append("Cleared selection\n");
                updateSelectedHud();
            }
            renderBoard();
            updateControls();
            return;
        }

        if (selected == null) { append("Select a unit first.\n"); return; }

        if (mode == Mode.MOVE) {
            handleMove(x, y);
        } else if (mode == Mode.ATTACK) {
            handleAttack(occupant);
        } else if (mode == Mode.ABILITY) {
            handleAbility(occupant);
        } else if (mode == Mode.CONSUMABLE) {
            handleConsumableUse(occupant);
        }
    }

    private void handleMove(int x, int y) {
        if (engine == null) { append("Setup the board first.\n"); return; }
        // Prevent moving onto occupied tiles
        if (findCharacterAt(x, y) != null) { append("Tile is occupied.\n"); selected = null; mode = Mode.SELECT; updateSelectedHud(); renderBoard(); updateControls(); return; }
        if (!hasMovesLeft(selected)) { append("No moves left for this unit.\n"); return; }
        Position dest = new Position(x, y);
        boolean ok = engine.move(selected, dest);
        append(ok ? (selected.getName() + " moved to (" + x + "," + y + ")\n") : "Invalid move\n");
        if (ok) {
            // Audio removed for simplified architecture
            caretaker.saveState(engine); // Save state after successful move
        }
        renderBoard();
        updateControls();
        updateSelectedHud();
        if (ok) decrementMove(selected);
    }

    private void handleAttack(com.amin.battlearena.domain.model.Character target) {
        updateCombatHandlerState();
        combatHandler.handleAttack(target);
        
        // Save state after successful attack (if target was affected)
        if (target != null && engine != null) {
            caretaker.saveState(engine);
        }
        
        // Reset mode after combat action
        selected = null;
        mode = Mode.SELECT;
        updateSelectedHud();
        renderBoard();
        updateControls();
    }

    private void handleAbility(com.amin.battlearena.domain.model.Character target) {
        updateCombatHandlerState();
        combatHandler.handleAbility(target);
        
        // Reset mode after ability use
        mode = Mode.SELECT;
        updateControls();
    }

    private void updateControls() {
        boolean hasSelection = selected != null;
        moveBtn.setDisable(!hasSelection);
        attackBtn.setDisable(!hasSelection);
        updateTurnHud();
    }

    void updateSelectedHud() {
        if (selected == null) {
            // Clear all character info
            selName.setText("No Selection");
            if (selClass != null) selClass.setText("-");
            if (selHp != null) selHp.setText("-/-");
            if (selMana != null) selMana.setText("-/-");
            if (selAttack != null) selAttack.setText("-");
            if (selDefense != null) selDefense.setText("-");

            if (selRange != null) selRange.setText("-");
            if (selMovesLeft != null) selMovesLeft.setText("-");
            if (selAttacksLeft != null) selAttacksLeft.setText("-");
            if (healthBar != null) healthBar.setProgress(0);
            if (manaBar != null) manaBar.setProgress(0);
            
            // Update abilities list
            if (abilitiesList != null) {
                abilitiesList.getItems().clear();
                useAbilityBtn.setDisable(true);
            }
            
            // Update consumables list
            updateConsumablesDisplay();
            

            return;
        }
        
        // Update character info
        var stats = selected.getStats();
        selName.setText(selected.getName());
        if (selClass != null) selClass.setText(selected.getClass().getSimpleName());
        
        // Health
        if (selHp != null) selHp.setText(stats.getHp() + "/" + stats.getMaxHp());
        if (healthBar != null) {
            double healthRatio = stats.getMaxHp() > 0 ? (double) stats.getHp() / stats.getMaxHp() : 0;
            healthBar.setProgress(healthRatio);
        }
        
        // Mana
        if (selMana != null) selMana.setText(selected.getCurrentMana() + "/" + selected.getMaxMana());
        if (manaBar != null) {
            double manaRatio = selected.getMaxMana() > 0 ? (double) selected.getCurrentMana() / selected.getMaxMana() : 0;
            manaBar.setProgress(manaRatio);
        }
        
        // Combat stats
        if (selAttack != null) selAttack.setText(String.valueOf(stats.getAttack()));
        if (selDefense != null) selDefense.setText(String.valueOf(stats.getDefense()));
        if (selRange != null) selRange.setText(String.valueOf(stats.getRange()));
        
        // Action economy
        if (selMovesLeft != null) {
            int mv = movesLeft.getOrDefault(selected, movesPerTurn(selected));
            selMovesLeft.setText(String.valueOf(mv));
        }
        if (selAttacksLeft != null) {
            int at = attacksLeft.getOrDefault(selected, attacksPerTurn(selected));
            selAttacksLeft.setText(String.valueOf(at));
        }

        // Update abilities list
        updateAbilitiesList();
        updateConsumablesDisplay();
        
        // Update team counters
        updateTeamCounters();
    }
    
    private void updateAbilitiesList() {
        // Delegate ability display to specialized handler (OOP delegation pattern)
        if (abilityHandler != null && selected != null) {
            abilityHandler.updateAbilitiesDisplay(selected);
            
            // Update ability button based on available abilities
            if (selected.getAbilities().isEmpty()) {
                useAbilityBtn.setDisable(true);
            } else {
                useAbilityBtn.setDisable(false);
            }
        } else if (abilitiesContainer != null) {
            // Fallback for null cases
            abilitiesContainer.getChildren().clear();
            Label noAbilities = new Label("No abilities available");
            noAbilities.getStyleClass().add("no-abilities-label");
            abilitiesContainer.getChildren().add(noAbilities);
            useAbilityBtn.setDisable(true);
        }
    }
    

    

    

    
    private void updateConsumablesDisplay() {
        if (consumablesContainer == null) return;
        
        consumablesContainer.getChildren().clear();
        
        // Load player data to get consumables
        com.amin.battlearena.persistence.PlayerData playerData = 
            com.amin.battlearena.persistence.PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
        
        if (playerData == null) return;
        
        // Define all available consumables
        String[] allConsumables = {"health_potion", "mana_potion", "strength_elixir", "shield_scroll", "haste_potion", "revival_token"};
        
        boolean hasAnyConsumables = false;
        
        for (String consumableId : allConsumables) {
            int quantity = playerData.getConsumableQuantity(consumableId);
            VBox consumableBox = createConsumableDisplay(consumableId, quantity);
            consumablesContainer.getChildren().add(consumableBox);
            
            if (quantity > 0) {
                hasAnyConsumables = true;
            }
        }
        
        // Enable/disable the use consumable button based on availability
        if (useConsumableBtn != null) {
            useConsumableBtn.setDisable(!hasAnyConsumables);
        }
    }
    
    private VBox createConsumableDisplay(String consumableId, int quantity) {
        VBox consumableBox = new VBox(5);
        consumableBox.setPadding(new Insets(8));
        consumableBox.getStyleClass().add("consumable-item");
        
        // Header with icon and name - matching abilities style
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(getConsumableIcon(consumableId));
        iconLabel.getStyleClass().add("consumable-icon");
        
        Label nameLabel = new Label(getConsumableName(consumableId));
        nameLabel.getStyleClass().addAll("consumable-name", "ability-name");
        nameLabel.setStyle("-fx-text-fill: #FFA500;"); // Orange/yellow theme
        
        header.getChildren().addAll(iconLabel, nameLabel);
        
        // Status row - matching abilities format with yellow theme
        HBox statusRow = new HBox(15);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        
        // Quantity display (similar to mana cost)
        HBox quantityBox = new HBox(3);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityIcon = new Label("📦");
        quantityIcon.getStyleClass().add("stat-icon");
        quantityIcon.setStyle("-fx-text-fill: #FFD700;"); // Gold color
        
        Label quantityLabel = new Label("x" + quantity);
        quantityLabel.getStyleClass().add("stat-value");
        if (quantity > 0) {
            quantityLabel.setStyle("-fx-text-fill: #32CD32;"); // Green for available
        } else {
            quantityLabel.setStyle("-fx-text-fill: #FF6B6B;"); // Red for unavailable
        }
        quantityBox.getChildren().addAll(quantityIcon, quantityLabel);
        
        // Availability indicator (similar to cooldown)
        HBox availabilityBox = new HBox(3);
        availabilityBox.setAlignment(Pos.CENTER_LEFT);
        
        if (quantity > 0) {
            Label availableIcon = new Label("✅");
            availableIcon.getStyleClass().add("stat-icon");
            Label availableLabel = new Label("Ready");
            availableLabel.getStyleClass().add("stat-value");
            availableLabel.setStyle("-fx-text-fill: #32CD32;");
            availabilityBox.getChildren().addAll(availableIcon, availableLabel);
            consumableBox.getStyleClass().add("consumable-enabled");
        } else {
            Label unavailableIcon = new Label("🔒");
            unavailableIcon.getStyleClass().add("stat-icon");
            Label unavailableLabel = new Label("Out of Stock");
            unavailableLabel.getStyleClass().add("stat-value");
            unavailableLabel.setStyle("-fx-text-fill: #FF6B6B;");
            availabilityBox.getChildren().addAll(unavailableIcon, unavailableLabel);
            consumableBox.getStyleClass().add("consumable-disabled");
        }
        
        statusRow.getChildren().addAll(quantityBox, availabilityBox);
        
        // Description - matching abilities style
        Label descLabel = new Label(getConsumableDescription(consumableId));
        descLabel.getStyleClass().addAll("consumable-description", "ability-description");
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #CCCCCC;"); // Light gray text
        
        consumableBox.getChildren().addAll(header, statusRow, descLabel);
        
        // Make clickable for selection (if available) - yellow hover effect
        if (quantity > 0) {
            consumableBox.setOnMouseClicked(e -> {
                selectedConsumableId = consumableId;
                updateConsumableSelection();
            });
            
            // Add hover effects matching abilities but in yellow theme
            consumableBox.setOnMouseEntered(e -> {
                consumableBox.setStyle("-fx-background-color: rgba(255, 215, 0, 0.2); -fx-border-color: #FFD700; -fx-border-width: 1px;");
            });
            
            consumableBox.setOnMouseExited(e -> {
                if (!consumableBox.getStyleClass().contains("consumable-selected")) {
                    consumableBox.setStyle("");
                }
            });
        }
        
        return consumableBox;
    }
    
    private String getConsumableIcon(String consumableId) {
        if ("health_potion".equals(consumableId)) {
            return "❤️";
        } else if ("mana_potion".equals(consumableId)) {
            return "💙";
        } else if ("strength_elixir".equals(consumableId)) {
            return "💪";
        } else if ("shield_scroll".equals(consumableId)) {
            return "🛡️";
        } else if ("haste_potion".equals(consumableId)) {
            return "⚡";
        } else if ("revival_token".equals(consumableId)) {
            return "💫";
        } else {
            return "📦";
        }
    }
    
    private String getConsumableDescription(String consumableId) {
        if ("health_potion".equals(consumableId)) {
            return "Restores 50 HP instantly";
        } else if ("mana_potion".equals(consumableId)) {
            return "Restores 30 Mana instantly";
        } else if ("strength_elixir".equals(consumableId)) {
            return "Increases Attack by 10 for this battle";
        } else if ("shield_scroll".equals(consumableId)) {
            return "Increases Defense by 5 for this battle";
        } else if ("haste_potion".equals(consumableId)) {
            return "+2 movement for the current turn";
        } else if ("revival_token".equals(consumableId)) {
            return "Revives a fallen ally with 25% HP";
        } else {
            return "Unknown consumable";
        }
    }
    

    private String selectedConsumableId = null;
    
    private void updateConsumableSelection() {
        if (consumablesContainer == null) return;
        
        // Update visual selection
        for (Node node : consumablesContainer.getChildren()) {
            if (node instanceof VBox) {
                node.getStyleClass().removeAll("consumable-selected");
            }
        }
        
        // Find and highlight the selected consumable
        if (selectedConsumableId != null) {
            for (Node node : consumablesContainer.getChildren()) {
                if (node instanceof VBox vbox) {
                    // Check if this is the selected consumable by examining its content
                    if (!vbox.getChildren().isEmpty() && vbox.getChildren().get(0) instanceof HBox header) {
                        if (header.getChildren().size() > 1 && header.getChildren().get(1) instanceof Label nameLabel) {
                            String displayName = getConsumableName(selectedConsumableId);
                            if (displayName.equals(nameLabel.getText())) {
                                vbox.getStyleClass().add("consumable-selected");
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    private void updateTeamCounters() {
        if (playerUnitsAlive != null && human != null) {
            playerUnitsAlive.setText(human.aliveTeam().size() + "/" + human.getTeam().size());
        }
        
        if (cpuUnitsAlive != null && cpu != null) {
            cpuUnitsAlive.setText(cpu.aliveTeam().size() + "/" + cpu.getTeam().size());
        }
    }


    private void runCpuTurn() {
        updateTurnHandlerState();
        turnHandler.runCpuTurn();
    }





    private boolean[][] computeMoveTargets() {
        boolean[][] targets = new boolean[board != null ? board.getWidth() : 0][board != null ? board.getHeight() : 0];
        if (board == null || mode != Mode.MOVE || selected == null) return targets;
        int sx = selected.getPosition().x();
        int sy = selected.getPosition().y();
        int maxMove = movementRangeForSelected();
        int minX = Math.max(0, sx - maxMove);
        int maxX = Math.min(board.getWidth() - 1, sx + maxMove);
        int minY = Math.max(0, sy - maxMove);
        int maxY = Math.min(board.getHeight() - 1, sy + maxMove);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == sx && y == sy) continue;
                int dx = Math.abs(x - sx);
                int dy = Math.abs(y - sy);
                int steps = Math.max(dx, dy); // Chebyshev distance for grid moves
                if (steps <= maxMove && findCharacterAt(x, y) == null) {
                    // require a clear path (no passing through units)
                    if (isPathClearForUI(selected.getPosition(), new Position(x, y))) {
                        targets[x][y] = true;
                    }
                }
            }
        }
        return targets;
    }

    private boolean[][] computeAbilityTargets() {
        boolean[][] targets = new boolean[board != null ? board.getWidth() : 0][board != null ? board.getHeight() : 0];
        if (board == null || mode != Mode.ABILITY || selected == null) return targets;
        
        // For now, use same range as attack but this can be customized per ability
        int sx = selected.getPosition().x();
        int sy = selected.getPosition().y();
        int abilityRange = selected.getAbilityRange();
        int minX = Math.max(0, sx - abilityRange);
        int maxX = Math.min(board.getWidth() - 1, sx + abilityRange);
        int minY = Math.max(0, sy - abilityRange);
        int maxY = Math.min(board.getHeight() - 1, sy + abilityRange);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                int dist = Math.abs(x - sx) + Math.abs(y - sy);
                if (dist <= abilityRange && (x != sx || y != sy)) {
                    targets[x][y] = true;
                }
            }
        }
        return targets;
    }

    private boolean[][] computeAttackTargets() {
        boolean[][] targets = new boolean[board != null ? board.getWidth() : 0][board != null ? board.getHeight() : 0];
        if (board == null || mode != Mode.ATTACK || selected == null) return targets;
        int sx = selected.getPosition().x();
        int sy = selected.getPosition().y();
        int atkRange = selected.getStats().getRange();
        int minX = Math.max(0, sx - atkRange);
        int maxX = Math.min(board.getWidth() - 1, sx + atkRange);
        int minY = Math.max(0, sy - atkRange);
        int maxY = Math.min(board.getHeight() - 1, sy + atkRange);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == sx && y == sy) continue;
                int dx = Math.abs(x - sx);
                int dy = Math.abs(y - sy);
                int steps = Math.max(dx, dy); // Chebyshev distance for adjacency
                if (steps <= atkRange) {
                    var occ = findCharacterAt(x, y);
                    if (occ != null && human != null && !human.getTeam().contains(occ)) targets[x][y] = true;
                }
            }
        }
        return targets;
    }

    private int movementRangeForSelected() {
        if (selected == null) return 1;
        // Prefer per-class overrides if present
        try {
            var m = selected.getClass().getMethod("getMovementRange");
            Object r = m.invoke(selected);
            if (r instanceof Integer i) return Math.max(1, i);
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException ignored) {
            // Reflection failed, use default value
        }
        return 1;
    }

    private boolean isPathClearForUI(Position from, Position to) {
        int dx = Integer.compare(to.x(), from.x());
        int dy = Integer.compare(to.y(), from.y());
        int x = from.x();
        int y = from.y();
        while (true) {
            x += dx; y += dy;
            if (x == to.x() && y == to.y()) break;
            if (x < 0 || y < 0 || x >= board.getWidth() || y >= board.getHeight()) return false;
            if (findCharacterAt(x, y) != null) return false;
        }
        return true;
    }

    private com.amin.battlearena.domain.model.Character nearestEnemy(
            com.amin.battlearena.domain.model.Character from,
            java.util.List<com.amin.battlearena.domain.model.Character> enemies) {
        com.amin.battlearena.domain.model.Character best = null;
        int bestDist = Integer.MAX_VALUE;
        for (com.amin.battlearena.domain.model.Character e : enemies) {
            int d = from.getPosition().distanceTo(e.getPosition());
            if (d < bestDist) { bestDist = d; best = e; }
        }
        return best;
    }


    private java.util.List<Position> bfsPath(Position start, Position goal) {
        java.util.Queue<Position> q = new java.util.ArrayDeque<>();
        java.util.Map<Position, Position> prev = new java.util.HashMap<>();
        java.util.Set<Position> visited = new java.util.HashSet<>();
        q.add(start);
        visited.add(start);
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        while (!q.isEmpty()) {
            Position cur = q.poll();
            if (cur.equals(goal)) break;
            for (int i = 0; i < 8; i++) {
                int nx = cur.x() + dx[i];
                int ny = cur.y() + dy[i];
                Position np = new Position(nx, ny);
                if (nx < 0 || ny < 0 || nx >= board.getWidth() || ny >= board.getHeight()) continue;
                if (visited.contains(np)) continue;
                // avoid occupied cells except goal
                if (!np.equals(goal) && findCharacterAt(nx, ny) != null) continue;
                visited.add(np);
                prev.put(np, cur);
                q.add(np);
            }
        }
        java.util.List<Position> path = new java.util.ArrayList<>();
        if (!prev.containsKey(goal)) {
            // find nearest reachable to goal
            Position best = null; int bestD = Integer.MAX_VALUE;
            for (Position p : prev.keySet()) {
                int d = p.distanceTo(goal);
                if (d < bestD) { bestD = d; best = p; }
            }
            if (best == null) return path;
            goal = best;
        }
        Position cur = goal;
        while (cur != null && !cur.equals(start)) {
            path.add(0, cur);
            cur = prev.get(cur);
        }
        return path;
    }

    private void resetBudgetsForTurn() {
        movesLeft.clear(); attacksLeft.clear();
        if (human != null) {
            for (var u : human.getTeam()) { movesLeft.put(u, movesPerTurn(u)); attacksLeft.put(u, attacksPerTurn(u)); }
        }
    }

    private int movesPerTurn(com.amin.battlearena.domain.model.Character u) {
        String cls = u.getClass().getSimpleName();
        if ("Knight".equals(cls)) return 2; // Knight moves twice per turn
        return 1;
    }

    private int attacksPerTurn(com.amin.battlearena.domain.model.Character u) {
        String cls = u.getClass().getSimpleName();
        if ("Warrior".equals(cls)) return 2; // Warrior can attack twice per turn
        return 1;
    }

    private boolean hasMovesLeft(com.amin.battlearena.domain.model.Character u) { return movesLeft.getOrDefault(u, 0) > 0; }
    private boolean hasAttacksLeft(com.amin.battlearena.domain.model.Character u) { return attacksLeft.getOrDefault(u, 0) > 0; }
    private void decrementMove(com.amin.battlearena.domain.model.Character u) { movesLeft.put(u, Math.max(0, movesLeft.getOrDefault(u, 0) - 1)); }
    private void decrementAttack(com.amin.battlearena.domain.model.Character u) { attacksLeft.put(u, Math.max(0, attacksLeft.getOrDefault(u, 0) - 1)); }

    private void startTurnTimer() {
        updateTurnHandlerState();
        turnHandler.startTurnTimer();
    }

    private void restartTurnTimer() {
        updateTurnHandlerState();
        turnHandler.restartTurnTimer();
    }



    private boolean tryAttackAny(com.amin.battlearena.domain.model.Character u,
                                java.util.List<com.amin.battlearena.domain.model.Character> enemies) {
        if (u == null || !u.isAlive()) return false;
        if (!hasAttacksLeft(u)) return false;
        if (enemies == null || enemies.isEmpty()) return false;

        com.amin.battlearena.domain.model.Character best = null;
        int bestHp = Integer.MAX_VALUE;
        int range = u.getStats().getRange();

        for (var e : enemies) {
            if (e == null || !e.isAlive()) continue;
            int dx = Math.abs(u.getPosition().x() - e.getPosition().x());
            int dy = Math.abs(u.getPosition().y() - e.getPosition().y());
            int steps = Math.max(dx, dy); // Chebyshev distance
            if (steps <= range) {
                int hp = e.getStats().getHp();
                if (hp < bestHp) { best = e; bestHp = hp; }
            }
        }

        if (best != null) {
            try {
                new com.amin.battlearena.domain.actions.AttackAction().execute(engine, u, best);
                append(u.getName() + " (CPU) attacked " + best.getName() + "\n");
                if (!best.isAlive()) {
                    // maintain existing UX: mark death FX on board
                    deathFx.put(best.getPosition(), 15);
                }
                return true;
            } catch (InvalidActionException | DeadCharacterException ex) {
                append("CPU attack failed: " + ex.getMessage() + "\n");
            } catch (RuntimeException ex) {
                append("CPU attack runtime error: " + ex.getMessage() + "\n");
            }
        }
        return false;
    }

    private boolean tryUseAnyAbility(com.amin.battlearena.domain.model.Character u,
                                    java.util.List<com.amin.battlearena.domain.model.Character> enemies) {
        // Defensive: no abilities or dead
        if (u == null || !u.isAlive()) return false;
        var abilities = u.getAbilities();
        if (abilities == null || abilities.isEmpty()) {
            return false;
        }

        for (com.amin.battlearena.domain.abilities.Ability ab : abilities) {
            try {
                if (!ab.canUse(u)) {
                    continue;
                }

                // Prefer an enemy target; fallback to self
                com.amin.battlearena.domain.model.Character target = nearestEnemy(u, enemies);
                
                // Check if target is in range before using ability
                if (target != null && target != u) {
                    int abilityRange = u.getAbilityRange();
                    int dx = Math.abs(target.getPosition().x() - u.getPosition().x());
                    int dy = Math.abs(target.getPosition().y() - u.getPosition().y());
                    int distance = Math.max(dx, dy);
                    
                    if (distance > abilityRange) {
                        target = u; // Use on self if target is out of range
                    }
                }
                
                if (target == null) target = u;

                // Activate ability via engine — can throw domain exceptions
                ab.activate(u, target, engine);

                // Log / UI
                append(u.getName() + " used ability: " + ab.getName() +
                    (target == u ? " (self)\n" : " on " + target.getName() + "\n"));

                // Abilities consume the unit's 'attack/ability' budget in this controller's model
                decrementAttack(u);

                return true; // used an ability — caller's while-loop can continue
            } catch (InvalidActionException | DeadCharacterException ex) {
                // If activate fails for this target, try next ability (or try self-target above)
                append("CPU ability failed: " + ex.getMessage() + "\n");
                // continue to try other abilities
            } catch (RuntimeException ex) {
                append("CPU ability runtime error: " + ex.getMessage() + "\n");
                // continue to try other abilities
            }
        }

        return false; // no ability used
    }

    private boolean checkAndHandleGameEnd() {
        if (gameEnded) return true; // Already handled
        
        boolean humanAlive = (human != null) && human.hasAliveCharacters();
        boolean cpuAlive   = (cpu != null)   && cpu.hasAliveCharacters();

        if (humanAlive && cpuAlive) return false;

        gameEnded = true; // Set flag to prevent spam
        boolean playerWon = humanAlive;
        String winner = playerWon ? "Victory!" : (cpuAlive ? "Defeat!" : "Draw!");
        append("Battle ended. Result: " + winner + "\n");
        disableControlsAfterGameOver();
        
        // Stop auto-play if active


        // Show dialog but don’t rely on imports; fully qualify
        // Handle progression and rewards
        // Delegate to TurnFlowHandler for victory/defeat handling
        updateTurnHandlerState();
        return turnHandler.checkAndHandleGameEnd();
    }
    

    

    @FXML private Button endTurnBtn;
    
    private void disableControlsAfterGameOver() {
        try {
            moveBtn.setDisable(true);
            attackBtn.setDisable(true);
            // ability button may not be a field; ignore if null
            try {
                java.lang.reflect.Field f = getClass().getDeclaredField("abilityBtn");
                f.setAccessible(true);
                Object btn = f.get(this);
                if (btn instanceof javafx.scene.control.Button b) b.setDisable(true);
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException ignored) {
                // Field may not exist or not accessible
            }
            endTurnBtn.setDisable(true);
            boardGrid.setDisable(true);
        } catch (RuntimeException ignored) {
            // Control disabling may fail
        }
    }
    
    // New GUI methods
    // Handlers required by FXML (restored to match FXML bindings)
    @FXML private void onBack() {
        if (app != null) app.switchToMainMenu(currentPlayerName != null ? currentPlayerName : "Player");
    }
    
    @FXML private void onPause() {
        try {
            if (turnTimer != null) {
                if (turnTimer.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                    turnTimer.stop();
                } else {
                    turnTimer.play();
                }
            }
        } catch (IllegalStateException | UnsupportedOperationException e) {
            // Timer might be in invalid state or operation not supported
            System.err.println("Timer pause/resume failed: " + e.getMessage());
        }
    }
    
    @FXML private void onUndo() {
        if (engine != null && caretaker.undo(engine)) {
            append("↶ Undid last action.\n");
            renderBoard();
            updateUndoRedoButtons();
        }
    }
    
    @FXML private void onRedo() {
        if (engine != null && caretaker.redo(engine)) {
            append("↷ Redid last action.\n");
            renderBoard();
            updateUndoRedoButtons();
        }
    }
    

    
    @FXML private void onUseSelectedAbility() {
        if (selected == null) {
            append("No character selected.\n");
            return;
        }
        
        if (!selected.isAlive()) {
            append("Selected character is dead.\n");
            return;
        }
        

        
        // Get selected ability
        if (selectedAbilityIndex < 0) {
            append("No ability selected. Click on an ability first.\n");
            return;
        }
        
        java.util.List<com.amin.battlearena.domain.abilities.Ability> abilities = selected.getAbilities();
        if (selectedAbilityIndex >= abilities.size()) {
            append("Invalid ability selection.\n");
            return;
        }
        
        com.amin.battlearena.domain.abilities.Ability ability = abilities.get(selectedAbilityIndex);
        
        // Check if ability is ready and character can use it
        if (!ability.canUse(selected)) {
            if (!ability.isReady()) {
                append("Ability is on cooldown (" + ability.getRemainingCooldown() + " turns remaining).\n");
            } else if (!selected.canSpendMana(ability.getManaCost())) {
                append("Not enough mana. Need " + ability.getManaCost() + " mana.\n");
            } else {
                append("Cannot use ability.\n");
            }
            return;
        }
        
        // Switch to ability targeting mode
        mode = Mode.ABILITY;
        append("Select a target for " + ability.getName() + "...\n");
        updateControls();
    }
    
    @FXML private void onUseConsumable() { useConsumable(); }
    
    @FXML private void onClearLog() { if (logArea != null) logArea.clear(); }
    
    @FXML private void onExportLog() {
        // Export functionality would be implemented here
    }
    
    private void updateUndoRedoButtons() {
        if (undoBtn != null) {
            undoBtn.setDisable(!caretaker.canUndo());
        }
        if (redoBtn != null) {
            redoBtn.setDisable(!caretaker.canRedo());
        }
    }
    

    
    // Removed unused method onAutoPlay
    // Removed unused method onAutoPlay
    
    // Removed unused method startAutoPlay
    

    
    // Removed unused method onUseConsumable
    
    private void useConsumable() {
        if (selectedConsumableId == null) {
            append("Select a consumable from the list first.\n");
            return;
        }
        // Enter consumable targeting mode; next click on a character applies it
        mode = Mode.CONSUMABLE;
        append("🎒 Ready to use " + getConsumableName(selectedConsumableId) + ". Click a character to apply.\n");

        renderBoard();
    }

    private void handleConsumableUse(com.amin.battlearena.domain.model.Character target) {
        updateCombatHandlerState();
        combatHandler.setSelectedConsumableId(selectedConsumableId);
        combatHandler.handleConsumableUse(target);
        
        // Update consumables display after use
        updateConsumablesDisplay();
        
        // Exit consumable mode after use
        mode = Mode.SELECT;
    }
    

    
    private String getConsumableName(String consumableId) {
        if ("health_potion".equals(consumableId)) {
            return "Health Potion";
        } else if ("mana_potion".equals(consumableId)) {
            return "Mana Potion";
        } else if ("strength_elixir".equals(consumableId)) {
            return "Strength Elixir";
        } else if ("shield_scroll".equals(consumableId)) {
            return "Shield Scroll";
        } else if ("haste_potion".equals(consumableId)) {
            return "Haste Potion";
        } else if ("revival_token".equals(consumableId)) {
            return "Revival Token";
        } else {
            return consumableId;
        }
    }
    
    private boolean[][] computeConsumableTargets() {
        boolean[][] targets = new boolean[board != null ? board.getWidth() : 0][board != null ? board.getHeight() : 0];
        if (board == null || mode != Mode.CONSUMABLE) return targets;
        // Allow targeting only allies for current consumables
        if (human == null) return targets;
        for (var c : human.getTeam()) {
            if (c.isAlive()) {
                targets[c.getPosition().x()][c.getPosition().y()] = true;
            }
        }
        return targets;
    }
    

    // Removed unused method onClearLog
    
    // Removed unused method onExportLog

    

    
    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Initialize delegated handlers for OOP separation of concerns
        abilityHandler = new AbilityUIHandler(abilitiesContainer);
        boardHandler = new BoardRenderHandler(boardGrid, 8); // 8x8 board
        stateHandler = new UIStateHandler(consumablesContainer); // Reuse container for state display
        
        // Initialize combat action handler
        combatHandler = new CombatActionHandler();
        initializeCombatHandler();
        
        // Initialize turn flow handler
        turnHandler = new TurnFlowHandler();
        initializeTurnHandler();
        
        // Set up ability selection callback to synchronize with main controller
        abilityHandler.setSelectionCallback((index, abilityName) -> {
            selectedAbilityIndex = index;
            // Enable use ability button when ability is selected
            if (useAbilityBtn != null) {
                useAbilityBtn.setDisable(false);
            }
        });
        
        // Set up board click handling through delegation
        boardHandler.setTileClickHandler(this::onTileClick);
        
        // Initialize UI components
        if (abilitiesList != null) {
            abilitiesList.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    boolean hasSelection = newVal != null && newVal.intValue() >= 0;
                    if (useAbilityBtn != null) useAbilityBtn.setDisable(!hasSelection);
                }
            );
        }
        
        // Hide consumable button - consumables will be shown in HUD (issue #4)
        if (consumableBtn != null) {
            consumableBtn.setVisible(false);
            consumableBtn.setManaged(false);
        }
        
        // Set initial state
        updateSelectedHud();
        updateTeamCounters();

    }
    
    // Initialize CombatActionHandler with callbacks
    private void initializeCombatHandler() {
        // Set callbacks for UI interaction
        combatHandler.setAppendLog(this::append);
        combatHandler.setRenderBoard(this::renderBoard);
        combatHandler.setUpdateControls(this::updateControls);
        combatHandler.setUpdateSelectedHud(this::updateSelectedHud);
        combatHandler.setHasAttacksLeft(this::hasAttacksLeft);
        combatHandler.setHasMovesLeft(this::hasMovesLeft);
        combatHandler.setDecrementAttack(this::decrementAttack);
        combatHandler.setAddDeathEffect((pos, ticks) -> deathFx.put(pos, ticks));
        combatHandler.setCheckAndHandleGameEnd(this::checkAndHandleGameEnd);
        combatHandler.setMovesPerTurn(this::movesPerTurn);
        
        // These will be updated dynamically as game state changes
        updateCombatHandlerState();
    }
    
    private void updateCombatHandlerState() {
        if (combatHandler == null) return;
        
        combatHandler.setEngine(engine);
        combatHandler.setHuman(human);
        combatHandler.setCpu(cpu);
        combatHandler.setSelected(selected);
        combatHandler.setSelectedAbilityIndex(selectedAbilityIndex);
        combatHandler.setMovesLeft(movesLeft);
        combatHandler.setAttacksLeft(attacksLeft);
        combatHandler.setDeathFx(deathFx);
        combatHandler.setCurrentPlayerName(currentPlayerName);
    }
    
    // Initialize TurnFlowHandler with callbacks
    private void initializeTurnHandler() {
        // Set UI components
        turnHandler.setTimerLabel(timerLabel);
        turnHandler.setTimerProgress(timerProgress);
        
        // Set callbacks for UI interaction
        turnHandler.setAppendLog(this::append);
        turnHandler.setRenderBoard(this::renderBoard);
        turnHandler.setUpdateControls(this::updateControls);
        turnHandler.setUpdateSelectedHud(this::updateSelectedHud);
        turnHandler.setDisableControlsAfterGameOver(this::disableControlsAfterGameOver);
        turnHandler.setOnEndTurn(this::onEndTurn);
        turnHandler.setHasAttacksLeft(this::hasAttacksLeft);
        turnHandler.setHasMovesLeft(this::hasMovesLeft);
        turnHandler.setDecrementAttack(this::decrementAttack);
        turnHandler.setDecrementMove(this::decrementMove);
        turnHandler.setMovesPerTurn(this::movesPerTurn);
        turnHandler.setAttacksPerTurn(this::attacksPerTurn);
        turnHandler.setTryUseAnyAbility(this::tryUseAnyAbility);
        turnHandler.setTryAttackAny(this::tryAttackAny);
        turnHandler.setNearestEnemy(this::nearestEnemy);
        turnHandler.setBfsPath(this::bfsPath);
        turnHandler.setFindCharacterAt(this::findCharacterAt);
        
        // These will be updated dynamically as game state changes
        updateTurnHandlerState();
    }
    
    private void updateTurnHandlerState() {
        if (turnHandler == null) return;
        
        turnHandler.setEngine(engine);
        turnHandler.setHuman(human);
        turnHandler.setCpu(cpu);
        turnHandler.setCurrentPlayerName(currentPlayerName);
        turnHandler.setCurrentLevelNumber(currentLevelNumber);
        turnHandler.setApp(app);
        turnHandler.setMovesLeft(movesLeft);
        turnHandler.setAttacksLeft(attacksLeft);
        turnHandler.setDeathFx(deathFx);
    }


}


