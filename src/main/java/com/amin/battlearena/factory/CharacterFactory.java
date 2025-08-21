package com.amin.battlearena.factory; // match the folder name "Factory"

import com.amin.battlearena.model.Archer;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Knight;
import com.amin.battlearena.model.Mage;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.model.Warrior;

public final class CharacterFactory {

    private CharacterFactory() {}

    public static Character create(String type, String name, Position pos) {
        String t = type == null ? "" : type.trim().toLowerCase();
        return switch (t) {
            case "warrior" -> new Warrior(name, pos);
            case "mage"    -> new Mage(name, pos);
            case "archer"  -> new Archer(name, pos);
            case "knight"  -> new Knight(name, pos);
            default -> throw new IllegalArgumentException("Unknown character type: " + type);
        };
    }
}
