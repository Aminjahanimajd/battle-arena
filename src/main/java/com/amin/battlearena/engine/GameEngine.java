package com.amin.battlearena.engine;

import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Board;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.player.Player;

public final class GameEngine {
    private final Board board;
    private final Player p1, p2;
    private Player current;

    public GameEngine(Board board, Player p1, Player p2) {
        this.board = board; this.p1 = p1; this.p2 = p2; this.current = p1;
    }

    public void start() {
        log("Game start: " + p1.getName() + " vs " + p2.getName());
        while (!isGameOver()) {
            try {
                log("Turn: " + current.getName());
                current.takeTurn(this);
                swapTurn();
            } catch (Exception e) {
                log("Error: " + e.getMessage());
                // keep same player's turn if error to retry
            }
        }
        Player winner = p1.hasAliveUnits() ? p1 : p2;
        log("Winner: " + winner.getName());
    }

    private void swapTurn() { current = (current == p1) ? p2 : p1; }

    public boolean isGameOver() {
        return !p1.hasAliveUnits() || !p2.hasAliveUnits();
    }

    public Player getOpponentOf(Player player) { return (player == p1) ? p2 : p1; }

    public void place(Character c, Position p) throws InvalidActionException { board.place(c, p); }
    public void move(Character c, Position p) throws InvalidActionException { board.move(c, p); }

    public void log(String s) { System.out.println(s); }
}