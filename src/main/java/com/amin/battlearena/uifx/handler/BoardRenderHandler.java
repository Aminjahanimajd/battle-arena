package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Board;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Handles board rendering and tile interactions
 * Delegates board-specific operations from GameController
 */
public class BoardRenderHandler {
    
    private final GridPane boardGrid;
    private final Button[][] boardButtons;
    private final int boardSize;
    private BiConsumer<Integer, Integer> tileClickHandler;
    
    public BoardRenderHandler(GridPane boardGrid, int boardSize) {
        this.boardGrid = boardGrid;
        this.boardSize = boardSize;
        this.boardButtons = new Button[boardSize][boardSize];
        initializeBoardButtons();
    }
    
    /**
     * Sets the handler for tile clicks
     */
    public void setTileClickHandler(BiConsumer<Integer, Integer> handler) {
        this.tileClickHandler = handler;
    }
    
    /**
     * Initializes board buttons with click handlers
     */
    private void initializeBoardButtons() {
        boardGrid.getChildren().clear();
        
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button tileButton = createTileButton(row, col);
                boardButtons[row][col] = tileButton;
                boardGrid.add(tileButton, col, row);
            }
        }
    }
    
    /**
     * Creates a single tile button
     */
    private Button createTileButton(int row, int col) {
        Button button = new Button();
        button.setPrefSize(40, 40);
        button.getStyleClass().add("board-tile");
        
        // Handle tile clicks
        button.setOnAction(e -> {
            if (tileClickHandler != null) {
                tileClickHandler.accept(row, col);
            }
        });
        
        return button;
    }
    
    /**
     * Renders the complete board state
     */
    public void renderBoard(Board board, List<Character> players) {
        if (board == null) return;
        
        // Clear all tiles first
        clearAllTiles();
        
                // Render terrain
        renderTerrain();
        
        // Render characters
        renderCharacters(players);
    }
    
    /**
     * Clears all tile visuals
     */
    private void clearAllTiles() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button tile = boardButtons[row][col];
                tile.setText("");
                tile.getStyleClass().removeAll("character-tile", "selected-tile", 
                    "valid-move", "valid-attack", "terrain-water", "terrain-mountain");
                tile.getStyleClass().add("board-tile");
            }
        }
    }
    
    /**
     * Renders terrain features (simplified - no terrain tiles in current board model)
     */
    private void renderTerrain() {
        // Current Board model doesn't have terrain tiles
        // This method is kept for future terrain feature expansion
    }
    
    /**
     * Renders all characters on board
     */
    private void renderCharacters(List<Character> players) {
        if (players == null) return;
        
        for (Character character : players) {
            if (character != null && character.getPosition() != null) {
                renderCharacter(character);
            }
        }
    }
    
    /**
     * Renders a single character on the board
     */
    private void renderCharacter(Character character) {
        Position pos = character.getPosition();
        if (pos == null || !isValidPosition(pos)) return;
        
        Button tile = boardButtons[pos.x()][pos.y()];
        tile.getStyleClass().add("character-tile");
        
        // Create character display container
        VBox characterDisplay = new VBox(2);
        characterDisplay.setAlignment(Pos.CENTER);
        
        // Character info with health and mana bars
        VBox characterInfo = new VBox(2);
        characterInfo.setAlignment(Pos.CENTER);
        
        // Health indicator (circle background) - keep existing visual
        StackPane characterIcon = new StackPane();
        Circle healthCircle = new Circle(15);
        double healthRatio = (double) character.getStats().getHp() / character.getStats().getMaxHp();
        healthCircle.setFill(getHealthColor(healthRatio));
        
        // Character symbol
        Label characterLabel = new Label(getCharacterSymbol(character));
        characterLabel.setAlignment(Pos.CENTER);
        characterLabel.getStyleClass().add("character-symbol");
        
        characterIcon.getChildren().addAll(healthCircle, characterLabel);
        
        // Health bar
        ProgressBar healthBar = new ProgressBar();
        healthBar.getStyleClass().add("health-bar");
        healthBar.setPrefWidth(35);
        healthBar.setPrefHeight(5);
        healthBar.setProgress(healthRatio);
        
        // Mana bar
        ProgressBar manaBar = new ProgressBar();
        manaBar.getStyleClass().add("mana-bar");
        manaBar.setPrefWidth(35);
        manaBar.setPrefHeight(5);
        double manaProgress = character.getMaxMana() > 0 ? (double) character.getCurrentMana() / character.getMaxMana() : 0;
        manaBar.setProgress(manaProgress);
        
        characterInfo.getChildren().addAll(characterIcon, healthBar, manaBar);
        characterDisplay.getChildren().add(characterInfo);
        
        tile.setGraphic(characterDisplay);
    }
    
    /**
     * Gets health color based on health ratio
     */
    private Color getHealthColor(double healthRatio) {
        if (healthRatio > 0.7) return Color.GREEN;
        if (healthRatio > 0.3) return Color.YELLOW;
        return Color.RED;
    }
    
    /**
     * Gets character symbol for display
     */
    private String getCharacterSymbol(Character character) {
        String name = character.getName().toLowerCase();
        if (name.contains("warrior")) return "⚔";
        if (name.contains("mage")) return "🔮";
        if (name.contains("archer")) return "🏹";
        if (name.contains("paladin")) return "🛡";
        if (name.contains("cpu")) return "🤖";
        return character.getName().substring(0, 1).toUpperCase();
    }
    
    /**
     * Highlights valid moves for selected character
     */
    public void highlightValidMoves(List<Position> validMoves) {
        clearHighlights();
        
        if (validMoves != null) {
            for (Position pos : validMoves) {
                if (isValidPosition(pos)) {
                    boardButtons[pos.x()][pos.y()].getStyleClass().add("valid-move");
                }
            }
        }
    }
    
    /**
     * Highlights valid attack targets
     */
    public void highlightValidAttacks(List<Position> validTargets) {
        if (validTargets != null) {
            for (Position pos : validTargets) {
                if (isValidPosition(pos)) {
                    boardButtons[pos.x()][pos.y()].getStyleClass().add("valid-attack");
                }
            }
        }
    }
    
    /**
     * Highlights selected character position
     */
    public void highlightSelectedCharacter(Position position) {
        clearHighlights();
        
        if (position != null && isValidPosition(position)) {
            boardButtons[position.x()][position.y()].getStyleClass().add("selected-tile");
        }
    }
    
    /**
     * Clears all highlights
     */
    public void clearHighlights() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button tile = boardButtons[row][col];
                tile.getStyleClass().removeAll("selected-tile", "valid-move", "valid-attack");
            }
        }
    }
    
    /**
     * Checks if position is valid on board
     */
    private boolean isValidPosition(Position pos) {
        return pos.x() >= 0 && pos.x() < boardSize && 
               pos.y() >= 0 && pos.y() < boardSize;
    }
    
    /**
     * Gets button at specific position
     */
    public Button getTileButton(Position position) {
        if (isValidPosition(position)) {
            return boardButtons[position.x()][position.y()];
        }
        return null;
    }
}