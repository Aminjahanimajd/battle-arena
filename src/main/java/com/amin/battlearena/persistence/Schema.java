package com.amin.battlearena.persistence;

import java.sql.Connection;
import java.sql.Statement;

public final class Schema {
    private Schema() {}

    public static void ensure() {
        try (Connection conn = Database.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS players(
                  player_id TEXT PRIMARY KEY,
                  gold INTEGER NOT NULL,
                  unlocked_level INTEGER NOT NULL,
                  last_played TEXT
                );
            """);
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_characters(
                  player_id TEXT NOT NULL,
                  character_name TEXT NOT NULL,
                  clazz TEXT NOT NULL,
                  hp INTEGER NOT NULL,
                  attack INTEGER NOT NULL,
                  defense INTEGER NOT NULL,
                  speed INTEGER NOT NULL,
                  abilities TEXT,
                  PRIMARY KEY(player_id, character_name)
                );
            """);
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS match_history(
                  match_id INTEGER PRIMARY KEY AUTOINCREMENT,
                  player_id TEXT NOT NULL,
                  level INTEGER NOT NULL,
                  result TEXT NOT NULL,
                  gold_earned INTEGER NOT NULL,
                  ts TEXT NOT NULL
                );
            """);
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure schema", e);
        }
    }
}
