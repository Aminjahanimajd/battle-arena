package com.amin.battlearena.domain.actions;

import java.util.Objects;

import com.amin.battlearena.domain.items.Consumable;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.infra.InvalidActionException;
import com.amin.battlearena.players.Player;

// Domain-level action to use a consumable item from a player's inventory
public final class UseConsumableAction implements Action{
    private final Player owner;
    private final Consumable item;
    private final Character user;


    @Override
    public String name() { return "Consume " + item.displayName(); }

    public UseConsumableAction(Player owner, Consumable item, Character user, Character target) {
        this.owner = Objects.requireNonNull(owner);
        this.item = Objects.requireNonNull(item);
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public void execute(GameEngine engine, Character actor, Character target)
            throws InvalidActionException, DeadCharacterException {
        if (!user.isAlive()) throw new InvalidActionException(user.getName() + " is dead and can't use items.");
        if (target != null && !target.isAlive()) throw new InvalidActionException("Target is dead.");

        // use and remove from inventory
        item.use(engine, user, target);
        owner.getInventory().remove(item);
    }
}