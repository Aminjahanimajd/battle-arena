package com.amin.battlearena.domain.ability;

import com.amin.battlearena.domain.character.Character;

public interface AbilityInterface {
    String getName();
    int getManaCost();
    int getCooldown();
    int getRange();
    int getCurrentCooldown();
    boolean isReady();
    void reduceCooldown();
    void putOnCooldown();
    void execute(Character source, Character target);
}
