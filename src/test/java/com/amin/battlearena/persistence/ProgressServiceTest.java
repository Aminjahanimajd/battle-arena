package com.amin.battlearena.persistence;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.amin.battlearena.progression.PlayerProgress;

public class ProgressServiceTest {

    @Test
    void saveAndLoadJsonAndSyncDb() throws Exception {
        Path tmp = Files.createTempDirectory("saves");
        String oldUrl = System.getProperty("battlearena.jdbcUrl");
        Path tempDb = Files.createTempFile("arena-test-", ".db");
        System.setProperty("battlearena.jdbcUrl", "jdbc:sqlite:" + tempDb.toAbsolutePath());
        try {
            ProgressService svc = new ProgressService(new PlayerDAO(), tmp);
            PlayerProgress p = new PlayerProgress("Bob");
            p.wallet().add(50);
            p.unlockLevel("L02");
            svc.save("Bob", p);

            PlayerProgress re = svc.load("Bob");
            assertEquals(50, re.wallet().getGold());
            assertTrue(re.unlockedLevels().contains("L02"));
        } finally {
            if (oldUrl == null) System.clearProperty("battlearena.jdbcUrl");
            else System.setProperty("battlearena.jdbcUrl", oldUrl);
            try { Files.deleteIfExists(tempDb); } catch (java.io.IOException ignored) {
                // Test cleanup failure, ignore
            }
        }
    }
}


