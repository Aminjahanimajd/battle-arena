package com.amin.battlearena.persistence;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerDAOTest {

    private String oldUrl;
    private Path tempDb;

    @BeforeEach
    void setup() throws Exception {
        oldUrl = System.getProperty("battlearena.jdbcUrl");
        tempDb = Files.createTempFile("arena-test-", ".db");
        System.setProperty("battlearena.jdbcUrl", "jdbc:sqlite:" + tempDb.toAbsolutePath());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (oldUrl == null) System.clearProperty("battlearena.jdbcUrl");
        else System.setProperty("battlearena.jdbcUrl", oldUrl);
        try { Files.deleteIfExists(tempDb); } catch (java.io.IOException ignored) {
            // Test cleanup failure, ignore
        }
    }

    @Test
    void upsertAndQueryPlayer() {
        PlayerDAO dao = new PlayerDAO();
        dao.createOrUpdatePlayer("Alice", 100, 3, "L01,L02,L03");
        assertEquals(100, dao.findGold("Alice").orElse(-1));
        assertEquals(3, dao.findUnlockedLevel("Alice").orElse(-1));
        assertEquals(3, dao.getUnlockedLevels("Alice").size());
    }
}


