package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.ability.Slash;

public class Enemy extends GameCharacter {
    public Enemy(String name, int level) {
        super(name, "Enemy", 
              50 + (level * 10), // HP
              20 + (level * 5),  // Mana
              10 + (level * 2),  // Attack
              2 + (level * 1),   // Defense
              1 + (level / 5),   // Range
              2 + (level / 5),   // Speed
              false);            // Not player team
        
        addAbility(new Slash()); // Basic attack
    }

    @Override
    public String getIcon() {
        return "👹";
    }
}
