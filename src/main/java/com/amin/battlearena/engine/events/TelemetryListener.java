package com.amin.battlearena.engine.events;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.engine.core.GameEngine;
import com.amin.battlearena.players.Player;

/**
 * Simple telemetry listener that logs key events to a file for analysis.
 * Registers as a GameEventListener on the engine.
 */
public final class TelemetryListener implements AutoCloseable, GameEventListener {
    private static final Logger LOG = Logger.getLogger(TelemetryListener.class.getName());
    private FileHandler handler;
    private final GameEngine engine;

    public TelemetryListener(GameEngine engine) {
        this.engine = engine;
        try {
            handler = new FileHandler("logs/telemetry.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOG.addHandler(handler);
            LOG.setLevel(Level.INFO);
        } catch (IOException e) {
            // fallback to console
            LOG.log(Level.WARNING, "Failed to initialize file handler for telemetry", e);
        }
        this.engine.addEventListener(this);
    }

    @Override
    public void onBattleEnded(Player winner, Player loser) {
        LOG.info(String.format("END winner=%s loser=%s", winner.getName(), loser.getName()));
    }

    @Override
    public void onCharacterKilled(Character character) {
        LOG.info(String.format("KILL target=%s", character.getName()));
    }

    @Override
    public void close() throws Exception {
        this.engine.removeEventListener(this);
        if (handler != null) {
            handler.close();
        }
    }
}


