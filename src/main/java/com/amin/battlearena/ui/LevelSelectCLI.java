package com.amin.battlearena.ui;

import java.util.Scanner;

import com.amin.battlearena.content.LevelRegistry;
import com.amin.battlearena.progression.CampaignMap;
import com.amin.battlearena.progression.Level;
import com.amin.battlearena.progression.PlayerProgress;

public final class LevelSelectCLI {
    public static Level pick(CampaignMap map, PlayerProgress progress, Scanner sc) {
        var unlocked = map.unlockedNodes(progress);
        System.out.println("== Select a Level ==");
        for (int i=0; i<unlocked.size(); i++) {
            var n = unlocked.get(i);
            System.out.printf("%d) %s (%s)%n", i+1, n.name(), n.id());
        }
        System.out.print("Choice: ");
        int idx = Math.max(1, Math.min(unlocked.size(), sc.nextInt())) - 1;
        var chosenNode = unlocked.get(idx);
        return LevelRegistry.find(chosenNode.id()).orElseThrow();
    }
}
