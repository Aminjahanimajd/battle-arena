package com.amin.battlearena.ui;

import java.util.Scanner;

import com.amin.battlearena.levels.LevelService;
import com.amin.battlearena.progression.LevelNode;
import com.amin.battlearena.progression.PlayerProgress;

public final class LevelSelectCLI {

    /**
     * Presents unlocked levels and returns the chosen level id (e.g., "L03").
     */
    public static String pick(LevelService levels, PlayerProgress progress, Scanner sc) {
        var unlocked = levels.unlockedNodes(progress);
        System.out.println("== Select a Level ==");
        for (int i = 0; i < unlocked.size(); i++) {
            LevelNode n = unlocked.get(i);
            System.out.printf("%d) %s (%s)%n", i + 1, n.name(), n.id());
        }
        System.out.print("Choice: ");
        int idx = Math.max(1, Math.min(unlocked.size(), sc.nextInt())) - 1;
        return unlocked.get(idx).id();
    }
}
