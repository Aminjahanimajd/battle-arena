package com.amin.battlearena.engine.memento;

import java.util.Stack;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.engine.core.GameEngine;

// Manages undo/redo functionality using Memento pattern
public final class GameCaretaker {
    
    private final Stack<GameMemento> undoStack = new Stack<>();
    private final Stack<GameMemento> redoStack = new Stack<>();
    private final int maxHistorySize;
    
    public GameCaretaker() {
        this(10);
    }
    
    public GameCaretaker(int maxHistorySize) {
        this.maxHistorySize = Math.max(1, maxHistorySize);
    }
    
    public void saveState(GameEngine engine) {
        GameMemento memento = createMemento(engine);
        undoStack.push(memento);
        redoStack.clear();
        
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
    }
    
    public boolean undo(GameEngine engine) {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        GameMemento currentState = createMemento(engine);
        redoStack.push(currentState);
        
        GameMemento previousState = undoStack.pop();
        restoreFromMemento(engine, previousState);
        
        return true;
    }
    
    public boolean redo(GameEngine engine) {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        GameMemento currentState = createMemento(engine);
        undoStack.push(currentState);
        
        GameMemento nextState = redoStack.pop();
        restoreFromMemento(engine, nextState);
        
        return true;
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public int getUndoCount() {
        return undoStack.size();
    }
    
    public int getRedoCount() {
        return redoStack.size();
    }
    
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
    
    private GameMemento createMemento(GameEngine engine) {
        return new GameMemento(
            engine.getHuman().getTeam(),
            engine.getAI().getTeam(),
            engine.getTurnManager().getCurrentTurn(),
            engine.getTurnManager().getCurrentPlayerForTurn()
        );
    }
    
    private void restoreFromMemento(GameEngine engine, GameMemento memento) {
        System.out.println("Restoring game state: " + memento);
        System.out.println("Turn: " + memento.getCurrentTurn());
        System.out.println("Current Player: " + 
                          (memento.getCurrentPlayer() != null ? memento.getCurrentPlayer().getName() : "null"));

        for (Character c : engine.getHuman().getTeam()) {
            var snap = memento.getHumanSnapshots().get(c.getName());
            if (snap != null) applySnapshot(c, snap);
        }
        for (Character c : engine.getAI().getTeam()) {
            var snap = memento.getAiSnapshots().get(c.getName());
            if (snap != null) applySnapshot(c, snap);
        }

        engine.getGameState().repopulateCharacters(() -> new java.util.Iterator<>() {
            private final java.util.Iterator<Character> it =
                java.util.stream.Stream.concat(engine.getHuman().getTeam().stream(), engine.getAI().getTeam().stream())
                    .iterator();
            @Override public boolean hasNext() { return it.hasNext(); }
            @Override public Character next() { return it.next(); }
        });
    }

    private void applySnapshot(Character c, GameMemento.CharacterSnapshot snap) {
        c.setPosition(new Position(snap.x, snap.y));
        c.getStats().setMaxHp(snap.maxHp);
        c.getStats().setHp(snap.hp);
        c.getStats().setAttack(snap.attack);
        c.getStats().setDefense(snap.defense);
        c.getStats().setRange(snap.range);
        c.invalidateCache();
    }
}
