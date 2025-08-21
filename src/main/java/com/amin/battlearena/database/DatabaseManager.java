package com.amin.battlearena.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles SQLite DB connection and schema setup.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:battlearena.db";
    private static Connection connection;

    static {
        // Initialize schema when class loads
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Player table (tracks gold, progress, etc.)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE,
                    gold INTEGER DEFAULT 0,
                    unlocked_level INTEGER DEFAULT 1
                )
            """);

            // Stats table (history of battles, kills, etc.)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS stats (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_id INTEGER,
                    kills INTEGER DEFAULT 0,
                    wins INTEGER DEFAULT 0,
                    losses INTEGER DEFAULT 0,
                    FOREIGN KEY(player_id) REFERENCES player(id)
                )
            """);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Gets (or creates) a shared database connection.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /**
     * Closes the database connection if open.
     */
    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("[DatabaseManager] Connection closed successfully.");
        }
    }
}
