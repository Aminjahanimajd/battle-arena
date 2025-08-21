package com.amin.battlearena.player;

import com.amin.battlearena.model.Character;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract player owning a team of Characters.
 * Concrete players implement takeTurn(...) to perform actions for their team.
 */
public abstract class Player {
    private final String name;
    private final List<Character> team = new ArrayList<>();

    protected Player(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() { return name; }

    public List<Character> getTeam() { return List.copyOf(team); }

    public void addToTeam(Character c) {
        if (c != null) team.add(c);
    }

    public List<Character> aliveTeam() {
        return team.stream().filter(Character::isAlive).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Perform this player's turn in the context of the engine.
     * Implementations should attempt to act with one or more characters and return.
     */
    public abstract void takeTurn(com.amin.battlearena.engine.GameEngine engine) throws Exception;
}
