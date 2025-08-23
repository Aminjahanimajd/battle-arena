package com.amin.battlearena.engine;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amin.battlearena.domain.events.BattleEnded;
import com.amin.battlearena.domain.events.CharacterKilled;
import com.amin.battlearena.domain.events.EventBus;
import com.amin.battlearena.domain.model.Board;
import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.domain.model.Position;
import com.amin.battlearena.infra.DeadCharacterException;
import com.amin.battlearena.players.AIPlayer;
import com.amin.battlearena.players.HumanPlayer;
import com.amin.battlearena.players.Player;

/**
 * Central game engine responsible for turn orchestration and
 * providing a small set of runtime services to domain logic (actions/abilities).
 *
 * - Uses a single injected EventBus for typed GameEvents.
 * - Centralize damage application so CharacterKilled events are posted consistently.
 * - Supports a temporary evasion mechanic (abilities can grant a per-turn dodge chance).
 */
public final class GameEngine {

    private static final Logger LOG = Logger.getLogger(GameEngine.class.getName());

    private final HumanPlayer human;
    private final AIPlayer ai;
    private final Board board;
    private final EventBus eventBus;

    private volatile Player currentPlayer;

    public GameEngine(HumanPlayer human, AIPlayer ai, Board board, EventBus eventBus) {
        this.human = Objects.requireNonNull(human, "human");
        this.ai = Objects.requireNonNull(ai, "ai");
        this.board = Objects.requireNonNull(board, "board");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    /**
     * Convenience ctor for backwards compatibility which creates a local EventBus.
     */
    public GameEngine(HumanPlayer human, AIPlayer ai, Board board) {
        this(human, ai, board, new EventBus());
    }

    public HumanPlayer getHuman() { return human; }
    public AIPlayer getAI() { return ai; }
    public Board getBoard() { return board; }
    public EventBus getEventBus() { return eventBus; }
    public Player getCurrentPlayer() { return currentPlayer; }

    /**
     * Main turn loop: alternate human <-> AI until one side has no alive characters.
     */
    public void runBattleLoop() {
        log("Battle started between " + human.getName() + " and " + ai.getName());

        List<Player> order = List.of(human, ai);
        int turn = 0;

        while (!human.aliveTeam().isEmpty() && !ai.aliveTeam().isEmpty()) {
            currentPlayer = order.get(turn % order.size());
            try {
                log("Turn " + (turn + 1) + " - " + currentPlayer.getName());
                currentPlayer.takeTurn(this);
            } catch (Exception | Error t) {
                LOG.log(Level.WARNING, "Exception during player turn: " + currentPlayer.getName(), t);
            } finally {
                currentPlayer = null;
            }

            // if someone died during the turn, break early
            if (human.aliveTeam().isEmpty() || ai.aliveTeam().isEmpty()) break;
            turn++;
        }

        Player winner = human.aliveTeam().isEmpty() ? ai : human;
        Player loser  = human.aliveTeam().isEmpty() ? human : ai;

        log("Battle ended. Winner: " + winner.getName() + ", Loser: " + loser.getName());
        try {
            eventBus.post(new BattleEnded(winner, loser));
        } catch (Exception | Error t) {
            LOG.log(Level.WARNING, "Failed to post BattleEnded event", t);
        }
    }

    /**
     * The canonical way to deal damage. Domain code (abilities/actions) should call this.
     *
     * @param target target character
     * @param amount damage amount (>0)
     * @param source optional source character (may be null)
     * @return true if the target died as a result of this call, false otherwise
     */
    public boolean applyDamage(Character target, int amount, Character source) {
        Objects.requireNonNull(target, "target");
        if (amount <= 0) {
            log("applyDamage called with non-positive amount: " + amount + " for " + target.getName());
            return false;
        }

        synchronized (target) {
            if (!target.isAlive()) {
                log("applyDamage: target already dead: " + target.getName());
                return false;
            }

            // Evasion check: abilities can grant a per-turn evasion chance (0.0 - 1.0)
            double evasionChance = target.getTemporaryEvasion();
            if (evasionChance > 0.0) {
                double roll = ThreadLocalRandom.current().nextDouble();
                if (roll < evasionChance) {
                    log(target.getName() + " evaded an attack! (chance=" + evasionChance + ", roll=" + roll + ")");
                    return false;
                }
            }

            try {
                // Character.takeDamage is the domain-level implementation (may throw DeadCharacterException)
                target.takeDamage(amount);
                log(String.format("%s took %d damage (hp=%d/%d) from %s",
                        target.getName(), amount, target.getStats().getHp(), target.getStats().getMaxHp(),
                        source == null ? "<unknown>" : source.getName()));
                return false;
            } catch (DeadCharacterException dcx) {
                log(String.format("%s was killed by %s", target.getName(), source == null ? "<unknown>" : source.getName()));
                // find player-owner of the killer character, if any
                com.amin.battlearena.players.Player killerOwner = findOwnerOf(source);
                publishCharacterKilled(target, killerOwner);
                return true;
            }
        }
    }

    /**
     * Publish a CharacterKilled event using the engine's EventBus.
     */
    private void publishCharacterKilled(Character victim, com.amin.battlearena.players.Player killer) {
        try {
            eventBus.post(new CharacterKilled(victim, killer));
            log("Published CharacterKilled: victim=" + victim.getName() + " killer=" + (killer == null ? "<unknown>" : killer.getName()));
        } catch (Exception | Error t) {
            LOG.log(Level.WARNING, "Failed to post CharacterKilled", t);
        }
    }

    /**
     * Find which player owns the supplied character (human, ai or null).
     */
    private com.amin.battlearena.players.Player findOwnerOf(Character c) {
        if (c == null) return null;
        if (human.getTeam().contains(c)) return human;
        if (ai.getTeam().contains(c)) return ai;
        return null;
    }

    public void log(String msg) {
        if (msg == null) return;
        LOG.info("[Engine] " + msg);
        System.out.println("[Engine] " + msg);
    }

    /**
     * Get the opponent of the given player.
     */
    public Player getOpponentOf(Player player) {
        if (player == human) return ai;
        if (player == ai) return human;
        throw new IllegalArgumentException("Unknown player: " + player);
    }

    /**
     * Move a character to a new position.
     */
    public void move(Character character, Position newPosition) {
        // Simple movement - just update the character's position
        character.setPosition(newPosition);
        log(character.getName() + " moved to " + newPosition);
    }
}
