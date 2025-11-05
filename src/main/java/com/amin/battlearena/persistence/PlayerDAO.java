package com.amin.battlearena.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// PlayerDAO: small, practical JDBC DAO for players
public final class PlayerDAO {

    public PlayerDAO() {
        ensureSchema();
    }

    private Connection conn() throws SQLException {
        return Database.getConnection();
    }

    /** Ensure players table exists with columns: name PK, gold, unlocked_level, unlocked_levels */
    private void ensureSchema() {
        final String sql = """
                CREATE TABLE IF NOT EXISTS player (
                  name TEXT PRIMARY KEY,
                  gold INTEGER DEFAULT 0,
                  unlocked_level INTEGER DEFAULT 1,
                  unlocked_levels TEXT DEFAULT ''
                )""";
        try (Connection c = conn(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to ensure player table schema", ex);
        }
    }

    public Optional<Integer> findGold(String playerName) {
        final String sql = "SELECT gold FROM player WHERE name = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt(1));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to query gold for " + playerName, ex);
        }
    }

    public Optional<Integer> findUnlockedLevel(String playerName) {
        final String sql = "SELECT unlocked_level FROM player WHERE name = ?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt(1));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            // If column doesn't exist or query fails, return empty to allow fallback
            return Optional.empty();
        }
    }

    // Upsert a player row
    public void createOrUpdatePlayer(String playerName, int gold, int unlockedLevel, String unlockedLevelsCsv) {
        final String sql = """
            INSERT INTO player(name, gold, unlocked_level, unlocked_levels) VALUES (?, ?, ?, ?)
              ON CONFLICT(name) DO UPDATE SET gold = excluded.gold, unlocked_level = excluded.unlocked_level, unlocked_levels = excluded.unlocked_levels
            """;
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setInt(2, gold);
            ps.setInt(3, Math.max(1, unlockedLevel));
            ps.setString(4, unlockedLevelsCsv == null ? "" : unlockedLevelsCsv);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // SQLite older versions might not support ON CONFLICT ... excluded - fallback to MERGE-style
            // Try fallback merge for compatibility
            fallbackUpsert(playerName, gold, unlockedLevel, unlockedLevelsCsv);
        }
    }

    /** Backwards-compatible fallback upsert: try update, then insert. */
    private void fallbackUpsert(String playerName, int gold, int unlockedLevel, String unlockedLevelsCsv) {
        final String update = "UPDATE player SET gold = ?, unlocked_level = ?, unlocked_levels = ? WHERE name = ?";
        final String insert = "INSERT OR IGNORE INTO player(name, gold, unlocked_level, unlocked_levels) VALUES (?, ?, ?, ?)";
        try (Connection c = conn()) {
            try (PreparedStatement u = c.prepareStatement(update)) {
                u.setInt(1, gold);
                u.setInt(2, Math.max(1, unlockedLevel));
                u.setString(3, unlockedLevelsCsv == null ? "" : unlockedLevelsCsv);
                u.setString(4, playerName);
                int rows = u.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement ins = c.prepareStatement(insert)) {
                        ins.setString(1, playerName);
                        ins.setInt(2, gold);
                        ins.setInt(3, Math.max(1, unlockedLevel));
                        ins.setString(4, unlockedLevelsCsv == null ? "" : unlockedLevelsCsv);
                        ins.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to upsert player: " + playerName, ex);
        }
    }

    /** Convenience: upsert using CSV for unlockedLevels; keeps old createOrUpdate signature semantics. */
    public void createOrUpdatePlayer(String playerName, int gold, int unlockedLevel) {
        createOrUpdatePlayer(playerName, gold, unlockedLevel, "");
    }

    /** Store unlocked levels as a CSV string (overwrites previous). */
    public void setUnlockedLevels(String playerName, String unlockedLevelsCsv) {
        final String sql = "UPDATE player SET unlocked_levels = ? WHERE name = ?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, unlockedLevelsCsv == null ? "" : unlockedLevelsCsv);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to set unlocked_levels for " + playerName, ex);
        }
    }

    /** Get unlocked levels as a list by splitting the CSV (returns empty list if none). */
    public List<String> getUnlockedLevels(String playerName) {
        final String sql = "SELECT unlocked_levels FROM player WHERE name = ?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String csv = rs.getString(1);
                    if (csv == null || csv.isBlank()) return new ArrayList<>();
                    String[] parts = csv.split(",");
                    List<String> out = new ArrayList<>();
                    for (String s : parts) {
                        String t = s.trim();
                        if (!t.isEmpty()) out.add(t);
                    }
                    return out;
                }
                return new ArrayList<>();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to read unlocked_levels for " + playerName, ex);
        }
    }

    /** Utility: return all players (names) in DB (for migration scripts) */
    public List<String> allPlayerNames() {
        final String sql = "SELECT name FROM player";
        try (Connection c = conn(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString(1));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list players", ex);
        }
    }

    /** Close / cleanup — not required for DriverManager usage but included for completeness. */
    public void close() { /* nothing to close for DriverManager */ }
}
