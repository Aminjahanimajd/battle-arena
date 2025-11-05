package com.amin.battlearena.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.amin.battlearena.domain.items.ConsumableFactory;
import com.amin.battlearena.domain.items.StrengthElixir;
import com.amin.battlearena.domain.model.Character;

// Abstract player owning a team of Characters
public abstract class Player {
    private final String name;
    private final List<Character> team = new ArrayList<>();
    // Basic inventory for consumables during battle
    private final Inventory inventory = new Inventory();

    protected Player(String name) {
        this.name = Objects.requireNonNull(name);
        // Starter items (using ConsumableFactory for extensibility)
        inventory.add(ConsumableFactory.createHealthPotion(20));
        inventory.add(ConsumableFactory.createManaPotion(10));
        inventory.add(new StrengthElixir());
    }

    public String getName() { return name; }

    public List<Character> getTeam() { return List.copyOf(team); }

    public Inventory getInventory() { return inventory; }

    public void addToTeam(Character c) {
        if (c != null) team.add(c);
    }

    public List<Character> aliveTeam() {
        return team.stream().filter(Character::isAlive).collect(Collectors.toUnmodifiableList());
    }

    public boolean hasAlive() {
        return team.stream().anyMatch(Character::isAlive);
    }

    public int aliveCount() {
        return (int) team.stream().filter(Character::isAlive).count();
    }

    public boolean hasAliveCharacters() {
        return !aliveTeam().isEmpty();
    }

    public int getAliveCharacterCount() {
        return aliveTeam().size();
    }

    public abstract void takeTurn(com.amin.battlearena.engine.core.GameEngine engine) throws Exception;
}
