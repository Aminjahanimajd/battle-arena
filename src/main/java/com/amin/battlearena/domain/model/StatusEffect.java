package com.amin.battlearena.domain.model;

// Status effect with duration that can modify damage and per-turn behavior
public interface StatusEffect {
    String name();
    int remainingTurns();
    void onTurnStart(Character character);
    void onTurnEnd(Character character);
    int modifyOutgoingDamage(int baseDamage);
    int modifyIncomingDamage(int incomingDamage);
    StatusEffect tick();
    boolean isExpired();
}


