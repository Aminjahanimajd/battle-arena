package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.Player;
import com.amin.battlearena.domain.ability.Shot;
import com.amin.battlearena.persistence.AccountRepository;

public class Archer extends Character {
    public Archer(boolean isPlayer) {
        super("Archer", "Archer", 
              80 + getUpgrade(0),
              60 + getUpgrade(6),
              12 + getUpgrade(1),
              2 + getUpgrade(2),
              3 + getUpgrade(3),
              4 + getUpgrade(4),
              isPlayer);
        
        addAbility(new Shot());
    }

    private static int getUpgrade(int type) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            int level = p.getUpgradeLevel(type);
            int[] multipliers = {15, 4, 2, 1, 1, 5, 10, 5, 1};
            if (type >= 0 && type < multipliers.length) {
                return level * multipliers[type];
            }
        }
        return 0;
    }

    @Override
    public String getIcon() {
        return "🏹";
    }
}
