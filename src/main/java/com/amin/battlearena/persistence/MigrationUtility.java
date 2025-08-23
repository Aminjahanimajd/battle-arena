package com.amin.battlearena.persistence;

import java.io.IOException;
import java.nio.file.Path;

import com.amin.battlearena.progression.PlayerProgress;

/**
 * Simple migration utility to import saves/*.json into the DB players table's unlocked_levels column.
 * Usage (from main or a one-off tool): MigrationUtility.migrate(new PlayerDAO(), Paths.get("saves"));
 */
public final class MigrationUtility {

    private MigrationUtility() {}

    public static void migrate(PlayerDAO dao, Path savesDir) {
        ProgressService ps = new ProgressService(dao, savesDir);
        // List files in savesDir using the DAO or NIO
        try {
            var files = java.nio.file.Files.list(savesDir)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .toList();
            for (var f : files) {
                String playerName = f.getFileName().toString().replaceFirst("\\.json$", "");
                PlayerProgress pp = ps.load(playerName);
                String csv = String.join(",", pp.unlockedLevels());
                dao.createOrUpdatePlayer(playerName, pp.wallet().gold(), 1, csv); // numeric level kept as 1 (DB numeric)
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Migration failed: " + e.getMessage(), e);
        }
    }
}
