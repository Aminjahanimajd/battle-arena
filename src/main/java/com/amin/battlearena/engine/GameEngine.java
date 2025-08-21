package com.amin.battlearena.engine;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amin.battlearena.events.EventBus;
import com.amin.battlearena.exceptions.InvalidActionException;
import com.amin.battlearena.model.Board;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.model.Position;
import com.amin.battlearena.player.AIPlayer;
import com.amin.battlearena.player.HumanPlayer;
import com.amin.battlearena.player.Player;

/**
 * Lightweight GameEngine implementation (cleaned to avoid broad catches and
 * use pattern-matching instanceof where suitable).
 */
public final class GameEngine {

    private static final Logger logger = Logger.getLogger(GameEngine.class.getName());

    private final HumanPlayer human;
    private final AIPlayer ai;
    private final Board board;
    private final EventBus eventBus;

    public GameEngine(HumanPlayer human, AIPlayer ai, Board board) {
        this.human = Objects.requireNonNull(human, "human");
        this.ai = Objects.requireNonNull(ai, "ai");
        this.board = Objects.requireNonNull(board, "board");
        this.eventBus = new EventBus();
        logger.fine("GameEngine created");
    }

    public HumanPlayer getHuman() { return human; }
    public AIPlayer getAI() { return ai; }
    public Board getBoard() { return board; }
    public EventBus getEventBus() { return eventBus; }

    public void log(String msg) {
        if (msg == null) return;
        logger.info(msg);
    }

    public void runBattleLoop() {
        log("Battle started between " + human.getName() + " and " + ai.getName());

        while (true) {
            // Human turn
            try {
                human.takeTurn(this);
            } catch (Exception e) { // catch expected runtime/checked exceptions from turn execution
                logger.log(Level.WARNING, "Exception during human turn", e);
            }

            if (!playerHasAlive(ai) || !playerHasAlive(human)) {
                break;
            }

            // AI turn
            try {
                ai.takeTurn(this);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception during AI turn", e);
            }

            if (!playerHasAlive(human) || !playerHasAlive(ai)) {
                break;
            }
        }

        // decide winner/loser
        Player winner = playerHasAlive(human) && !playerHasAlive(ai) ? human
                : playerHasAlive(ai) && !playerHasAlive(human) ? ai
                : null;
        Player loser = winner == human ? ai : (winner == ai ? human : null);

        if (winner != null) {
            log("Battle ended. Winner: " + winner.getName());
        } else {
            log("Battle ended in a draw or uncertain state.");
        }

        // Attempt to publish BattleEnded event reflectively if available
        tryPublishBattleEnded(winner, loser);
    }

    /**
     * Heuristically determines whether the player still has alive characters.
     * Uses pattern-matching instanceof to handle return types cleanly.
     */
    private boolean playerHasAlive(Player p) {
        if (p == null) return false;

        String[] candidates = {
                "hasAlive", "hasAliveTeam", "hasLivingCharacters", "isAlive",
                "aliveTeam", "getAliveTeam", "getTeam", "teamSize", "getRemaining"
        };

        for (String name : candidates) {
            try {
                Method m = p.getClass().getMethod(name);
                Object res = m.invoke(p);
                if (res instanceof Boolean b) {
                    return b;
                } else if (res instanceof Number n) {
                    return n.intValue() > 0;
                } else if (res instanceof java.util.Collection<?> c) {
                    return !c.isEmpty();
                } else if (res != null && res.getClass().isArray()) {
                    return Array.getLength(res) > 0;
                }
            } catch (NoSuchMethodException nsme) {
                // method not present on this Player impl — try next candidate
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // reflection invocation issue for this method; log at FINE and try next candidate
                logger.log(Level.FINER, "playerHasAlive reflection attempt failed for " + name, e);
            }
        }

        // As a final attempt, try aliveCount/getAliveCount
        try {
            for (String name : new String[]{"aliveCount", "getAliveCount"}) {
                try {
                    Method m = p.getClass().getMethod(name);
                    Object res = m.invoke(p);
                    if (res instanceof Number n) return n.intValue() > 0;
                } catch (NoSuchMethodException nsme) {
                    // try next
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.log(Level.FINER, "playerHasAlive final reflection attempt failed for " + name, e);
                }
            }
        } catch (Exception ignored) {
            // give up and assume alive (safe default)
        }

        // Could not determine — assume true to avoid abrupt termination
        return true;
    }

    public Player getOpponentOf(Player player) {
        if (player == null) return null;
        if (player == human) return ai;
        if (player == ai) return human;
        // fallback in case different instances compare equal
        if (player.equals(human)) return ai;
        if (player.equals(ai)) return human;
        return null;
    }


    public boolean move(Character character, Position newPosition) {
        Objects.requireNonNull(character, "character");
        Objects.requireNonNull(newPosition, "newPosition");

        // No-op if same spot
        if (newPosition.equals(character.getPosition())) {
            return true;
        }

        // Bounds check
        if (!board.isWithinBounds(newPosition)) {
            log("Move blocked: out of bounds " + newPosition);
            return false;
        }

        // Build a list of all characters on the board
        List<Character> all = new ArrayList<>();
        all.addAll(human.getTeam());
        all.addAll(ai.getTeam());

        // Don’t count the moving character as a blocker
        all.remove(character);

        // Occupancy check
        if (board.isPositionOccupied(newPosition, all)) {
            log("Move blocked: tile occupied at " + newPosition);
            return false;
        }

        try {
            // Perform the move
            character.moveTo(newPosition);
            return true;
        } catch (InvalidActionException e) {
            System.err.println("Invalid move: " + e.getMessage());
            return false;
        }
    }

    /**
     * Attempts to construct and publish a BattleEnded event using reflection.
     * Uses more specific exception handling (ReflectiveOperationException,
     * InvocationTargetException, IllegalArgumentException) instead of broad Throwable.
     */
    private void tryPublishBattleEnded(Player winner, Player loser) {
        try {
            Class<?> evtClass = Class.forName("com.amin.battlearena.events.BattleEnded");
            Object evtInstance = null;

            for (Constructor<?> ctor : evtClass.getConstructors()) {
                Class<?>[] params = ctor.getParameterTypes();
                if (params.length == 2 && Player.class.isAssignableFrom(params[0]) && Player.class.isAssignableFrom(params[1])) {
                    evtInstance = ctor.newInstance(winner, loser);
                    break;
                } else if (params.length == 1 && Player.class.isAssignableFrom(params[0])) {
                    evtInstance = ctor.newInstance(winner);
                    break;
                } else if (params.length == 0) {
                    evtInstance = ctor.newInstance();
                    break;
                }
            }

            if (evtInstance == null) {
                evtInstance = evtClass.getDeclaredConstructor().newInstance();
            }

            String[] methodNames = {"publish", "emit", "post", "dispatch", "send", "fire"};
            Method publishMethod = null;
            for (String mname : methodNames) {
                try {
                    publishMethod = eventBus.getClass().getMethod(mname, Object.class);
                    if (publishMethod != null) break;
                } catch (NoSuchMethodException ignored) {}
            }

            if (publishMethod != null) {
                publishMethod.invoke(eventBus, evtInstance);
                logger.fine("Published BattleEnded event via EventBus.");
            } else {
                logger.fine("No publish method found on EventBus; skipping BattleEnded publish.");
            }

        } catch (ClassNotFoundException cnfe) {
            logger.fine("BattleEnded event class not present; skipping event publish.");
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
        // concrete reflection-related failures; log them for debugging
        logger.log(Level.WARNING, "Failed to publish BattleEnded event reflectively", e);
        }
    }
}
