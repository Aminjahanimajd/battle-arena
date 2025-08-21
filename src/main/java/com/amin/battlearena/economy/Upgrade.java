package com.amin.battlearena.economy;

import com.amin.battlearena.model.Character;

public interface Upgrade {
    String id();
    String name();
    int cost();
    void apply(Character c); // out-of-battle application
}
