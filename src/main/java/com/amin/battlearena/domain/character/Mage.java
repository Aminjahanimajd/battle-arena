package com.amin.battlearena.domain.character;

import com.amin.battlearena.domain.Player;
import com.amin.battlearena.domain.ability.Fireball;
import com.amin.battlearena.persistence.AccountRepository;

public class Mage extends GameCharacter {
    public Mage(boolean isPlayer) {
        super("Mage", "Mage", 
              60 + getUpgrade(0),
              100 + getUpgrade(6),
              20 + getUpgrade(7),
              1 + getUpgrade(2),
              2 + getUpgrade(3),
              3 + getUpgrade(4),
              isPlayer);
        
        addAbility(new Fireball());
    }

    private static int getUpgrade(int type) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            int level = p.getUpgradeLevel(type);
            int[] multipliers = {10, 2, 1, 1, 1, 5, 20, 8, 1};
            if (type >= 0 && type < multipliers.length) {
                return level * multipliers[type];
            }
        }
        return 0;
    }

    @Override
    public String getIcon() {
        return "🔮";
    }
}
