package com.amin.battlearena.engine;

import java.util.Stack;

/**
 * Manages undo/redo functionality using the Memento pattern.
 * Stores game states and allows players to undo/redo their actions.
 */
public final class GameCaretaker {
    
    private final Stack<GameMemento> undoStack = new Stack<>();
    private final Stack<GameMemento> redoStack = new Stack<>();
    private final int maxHistorySize;
    
    public GameCaretaker() {
        this(10); // Default max history size
    }
    
    public GameCaretaker(int maxHistorySize) {
        this.maxHistorySize = Math.max(1, maxHistorySize);
    }
    
    /**
     * Save the current game state.
     * @param engine the current game engine
     */
    public void saveState(GameEngine engine) {
        GameMemento memento = createMemento(engine);
        undoStack.push(memento);
        redoStack.clear(); // Clear redo when new action is performed
        
        // Limit history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
    }
    
    /**
     * Undo the last action.
     * @param engine the game engine to restore
     * @return true if undo was successful, false if no actions to undo
     */
    public boolean undo(GameEngine engine) {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        // Save current state to redo stack
        GameMemento currentState = createMemento(engine);
        redoStack.push(currentState);
        
        // Restore previous state
        GameMemento previousState = undoStack.pop();
        restoreFromMemento(engine, previousState);
        
        return true;
    }
    
    /**
     * Redo the last undone action.
     * @param engine the game engine to restore
     * @return true if redo was successful, false if no actions to redo
     */
    public boolean redo(GameEngine engine) {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        // Save current state to undo stack
        GameMemento currentState = createMemento(engine);
        undoStack.push(currentState);
        
        // Restore next state
        GameMemento nextState = redoStack.pop();
        restoreFromMemento(engine, nextState);
        
        return true;
    }
    
    /**
     * Check if undo is available.
     * @return true if undo is possible
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Check if redo is available.
     * @return true if redo is possible
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Get the number of available undo operations.
     * @return the number of undo operations available
     */
    public int getUndoCount() {
        return undoStack.size();
    }
    
    /**
     * Get the number of available redo operations.
     * @return the number of redo operations available
     */
    public int getRedoCount() {
        return redoStack.size();
    }
    
    /**
     * Clear all history.
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Create a memento from the current game state.
     */
    private GameMemento createMemento(GameEngine engine) {
        return new GameMemento(
            engine.getHuman().getTeam(),
            engine.getAI().getTeam(),
            engine.getTurnManager().getCurrentTurn(),
            engine.getTurnManager().getCurrentPlayerForTurn()
        );
    }
    
    /**
     * Restore the game state from a memento.
     */
    private void restoreFromMemento(GameEngine engine, GameMemento memento) {
        // This is a simplified restoration - in a real implementation,
        // you would need to properly restore character states, positions, etc.
        // For now, we'll just log the restoration
        
        System.out.println("Restoring game state: " + memento);
        System.out.println("Turn: " + memento.getCurrentTurn());
        System.out.println("Current Player: " + 
                          (memento.getCurrentPlayer() != null ? memento.getCurrentPlayer().getName() : "null"));
        
        // todo: Implement proper state restoration when needed
        // engine parameter is kept for future implementation
    }
}
