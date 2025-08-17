package com.amin.battlearena.player;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import java.util.ArrayList;
import java.util.List;

// Subtyping: AIPlayer and HumanPlayer used as Player.
public abstract class Player {
    private final String name;
    private final List<Character> team = new ArrayList<>();

    protected Player(String name) { this.name = name; }
    public String getName() { return name; }
    public List<Character> getTeam() { return team; }
    public boolean hasAliveUnits() { return team.stream().anyMatch(Character::isAlive); }

    public abstract void takeTurn(GameEngine game) throws Exception;
}