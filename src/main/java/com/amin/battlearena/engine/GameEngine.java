package com.amin.battlearena.engine;

import com.amin.battlearena.actions.Action;
import com.amin.battlearena.actions.AttackAction;
import com.amin.battlearena.actions.DefendAction;
import com.amin.battlearena.exceptions.OutOfTurnException;
import com.amin.battlearena.model.Character;
import main.java.com.amin.battlearena.engine.AIStrategy;
import java.util.List;

public class GameEngine {

    private Character player;
    private Character ai;
    private AIStrategy aiStrategy;
    private boolean playerTurn;

    public GameEngine(Character player, Character ai, AIStrategy aiStrategy) {
        this.player = player;
        this.ai = ai;
        this.aiStrategy = aiStrategy;
        this.playerTurn = true; // player starts
    }

    // Player acts
    public void playerAction(Action action, Character target) throws Exception {
        if (!playerTurn) throw new OutOfTurnException("It's not your turn!");
        action.execute(this, player, ai);
        playerTurn = false; // switch turn
        aiTurn();
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
