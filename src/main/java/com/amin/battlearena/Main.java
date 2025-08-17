package com.amin.battlearena;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.*;
import com.amin.battlearena.player.*;
import com.amin.battlearena.exceptions.InvalidActionException;

public final class Main {
    public static void main(String[] args) throws InvalidActionException {
        var board = new Board(5,5);

        var human = new HumanPlayer("Human");
        var ai = new AIPlayer("Computer");

        var h1 = new Warrior("H-Warrior", new Position(0,0));
        var h2 = new Archer("H-Archer", new Position(0,1));
        human.getTeam().add(h1);
        human.getTeam().add(h2);

        var a1 = new Warrior("C-Warrior", new Position(4,4));
        var a2 = new Archer("C-Archer", new Position(4,3));
        ai.getTeam().add(a1);
        ai.getTeam().add(a2);

        var engine = new GameEngine(board, human, ai);
        engine.place(h1, h1.getPosition());
        engine.place(h2, h2.getPosition());
        engine.place(a1, a1.getPosition());
        engine.place(a2, a2.getPosition());

        engine.start();
    }
}