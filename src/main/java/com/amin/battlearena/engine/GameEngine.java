package com.amin.battlearena.engine;

import com.amin.battlearena.actions.Action;
import com.amin.battlearena.exceptions.OutOfTurnException;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.player.Player;

public class GameEngine {

    private Player player1;
    private Player player2;
    private final Character player;
    private final Character ai;
    private final AIStrategy aiStrategy;
    private boolean playerTurn;

    public GameEngine(Character player, Character ai, AIStrategy aiStrategy) {
        this.player = player;
        this.ai = ai;
        this.aiStrategy = aiStrategy;
        this.playerTurn = true; // player starts
    }

     /** Returns the opponent of the given player */
    public Player getOpponentOf(Player player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        return null; // or throw an exception if player is invalid
    }

    /** ✅ Add this helper so AttackAction compiles */
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }

    // Player acts
    public void playerAction(Action action, Character target) throws Exception {
        if (!playerTurn) throw new OutOfTurnException("It's not your turn!");
        action.execute(this, player, ai);
        playerTurn = false; // switch turn
        aiTurn();
    }

public void move(Character character, Position newPos) {
    if (character == null || !character.isAlive()) {
        log("Invalid move: Character is null or dead.");
        return;
    }

    if (newPos == null) {
        log("Invalid move: Position is null.");
        return;
    }

    // Move the character directly
    character.moveTo(newPos); // Use moveTo() instead of setPosition() for protected access
    log(character.getName() + " moves to (" + newPos.x() + "," + newPos.y() + ")");
}

    // AI acts
    private void aiTurn() {
        if (ai.isDead() || player.isDead()) return;
        aiStrategy.takeTurn(ai, player);
        playerTurn = true; // switch turn back to player
    }

    // Game over check
    public boolean isGameOver() {
        return player.isDead() || ai.isDead();
    }

    public Character getWinner() {
        if (!isGameOver()) return null;
        return player.isDead() ? ai : player;
    }

    public void showStatus() {
        System.out.println(player);
        System.out.println(ai);
        System.out.println("Player's turn: " + playerTurn);
    }
}
