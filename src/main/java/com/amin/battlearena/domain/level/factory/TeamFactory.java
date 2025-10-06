package com.amin.battlearena.domain.level.factory;

import java.util.ArrayList;
import java.util.List;

import com.amin.battlearena.domain.level.LevelSpec;
import com.amin.battlearena.domain.model.Archer;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Knight;
import com.amin.battlearena.domain.model.Mage;
import com.amin.battlearena.domain.model.Master;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.domain.model.Ranger;
import com.amin.battlearena.domain.model.Warrior;

/** Builds teams from LevelSpec enemy lists and positions. */
public final class TeamFactory {

    public static List<Character> buildEnemies(LevelSpec spec) {
        List<Character> enemies = new ArrayList<>();
        List<String> types = spec.enemies();
        List<List<Integer>> positions = spec.enemyPositions();
        for (int i = 0; i < types.size(); i++) {
            String t = types.get(i);
            List<Integer> pos = positions.get(i);
            Position p = new Position(pos.get(0), pos.get(1));
            enemies.add(create(t, "Enemy-" + (i + 1), p));
        }
        return enemies;
    }

    private static Character create(String type, String name, Position position) {
        if ("Warrior".equals(type)) {
            return new Warrior(name, position);
        } else if ("Archer".equals(type)) {
            return new Archer(name, position);
        } else if ("Mage".equals(type)) {
            return new Mage(name, position);
        } else if ("Knight".equals(type)) {
            return new Knight(name, position);
        } else if ("Ranger".equals(type)) {
            return new Ranger(name, position);
        } else if ("Master".equals(type)) {
            return new Master(name, position);
        } else {
            throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }
}