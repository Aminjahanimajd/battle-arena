package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.Player;
import com.amin.battlearena.domain.ability.Slash;
import com.amin.battlearena.persistence.AccountRepository;

public class Warrior extends Character {
    public Warrior(boolean isPlayer) {
        super("Warrior", "Warrior", 
              100 + getUpgrade(0),
              50 + getUpgrade(6),
              15 + getUpgrade(1),
              5 + getUpgrade(2),
              1,
              3 + getUpgrade(4),
              isPlayer);
        
        addAbility(new Slash());
    }

    private static int getUpgrade(int type) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            int level = p.getUpgradeLevel(type);
            int[] multipliers = {20, 5, 3, 1, 1, 5, 10, 5, 1};
            if (type >= 0 && type < multipliers.length) {
                return level * multipliers[type];
            }
        }
        return 0;
    }

    @Override
    public String getIcon() {
        return "🛡";
    }
}
