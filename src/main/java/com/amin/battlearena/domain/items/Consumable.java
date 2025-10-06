package com.amin.battlearena.domain.items;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;

/**
 * A consumable item that can be used during battle to produce an immediate effect.
 */
public interface Consumable {
    String key();              // stable identifier (e.g., "HEALTH_POTION")
    String displayName();      // user-visible name
    String description();
    int getCost();            // gold cost for shop

    /**
     * Apply the item effect. The effect should be immediate.
     */
    void use(GameEngine engine, Character user, Character target);
}