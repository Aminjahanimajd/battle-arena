package com.amin.battlearena.engine;

import com.amin.battlearena.domain.ability.Ability;
import com.amin.battlearena.domain.character.Archer;
import com.amin.battlearena.domain.Board;
import com.amin.battlearena.domain.character.Character;
import com.amin.battlearena.domain.character.Enemy;
import com.amin.battlearena.domain.character.Mage;
import com.amin.battlearena.domain.Tile;
import com.amin.battlearena.domain.character.Warrior;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private Board board;
    private List<Character> allCharacters;
    private int turnCount;
    private boolean isPlayerTurn;
    private boolean isGameOver;
    private boolean playerWon;

    public GameEngine() {
        this.allCharacters = new ArrayList<>();
        this.turnCount = 1;
        this.isPlayerTurn = true;
        this.isGameOver = false;
    }

    public void initLevel(int levelId) {
        // Create board
        int width = 15;
        int height = 10;
        this.board = new Board(width, height);
        this.allCharacters.clear();
        this.turnCount = 1;
        this.isPlayerTurn = true;
        this.isGameOver = false;

        // Spawn Player Team
        spawnCharacter(new Warrior(true), 1, 2);
        spawnCharacter(new Archer(true), 1, 5);
        spawnCharacter(new Mage(true), 1, 8);

        // Spawn Enemies based on level
        int enemyCount = 2 + (levelId / 2);
        Random rand = new Random();
        for (int i = 0; i < enemyCount; i++) {
            int x = width - 2 - rand.nextInt(3);
            int y = rand.nextInt(height);
            if (!board.getTile(x, y).isOccupied()) {
                spawnCharacter(new Enemy("Enemy " + (i+1), levelId), x, y);
            }
        }
    }

    private void spawnCharacter(Character c, int x, int y) {
        Tile t = board.getTile(x, y);
        if (t != null && !t.isOccupied()) {
            t.setOccupant(c);
            c.setPosition(t);
            allCharacters.add(c);
        }
    }

    public Board getBoard() { return board; }
    public List<Character> getAllCharacters() { return allCharacters; }
    public int getTurnCount() { return turnCount; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didPlayerWin() { return playerWon; }

    public boolean moveCharacter(Character c, Tile target) {
        if (c.getMovesLeft() <= 0) return false;
        if (target.isOccupied()) return false;
        
        // Simple distance check (Manhattan distance)
        int dist = Math.abs(c.getPosition().getX() - target.getX()) + 
                   Math.abs(c.getPosition().getY() - target.getY());
        
        if (dist > c.getSpeed()) return false;

        c.getPosition().setOccupant(null);
        target.setOccupant(c);
        c.setPosition(target);
        c.useMove();
        return true;
    }

    public boolean attackCharacter(Character attacker, Character target, Ability ability) {
        if (attacker.getAttacksLeft() <= 0) return false;
        if (ability != null && !ability.isReady()) return false;
        if (ability != null && attacker.getCurrentMana() < ability.getManaCost()) return false;

        int range = (ability != null) ? ability.getRange() : attacker.getRange();
        int dist = Math.abs(attacker.getPosition().getX() - target.getPosition().getX()) + 
                   Math.abs(attacker.getPosition().getY() - target.getPosition().getY());

        if (dist > range) return false;

        performAttack(attacker, target, ability);
        attacker.useAttack();
        
        if (!target.isAlive()) {
            target.getPosition().setOccupant(null);
        }
        return true;
    }

    private void performAttack(Character attacker, Character target, Ability ability) {
        if (ability != null) {
            attacker.spendMana(ability.getManaCost());
            ability.execute(attacker, target);
            return;
        }
        // Basic attack
        int damage = Math.max(1, attacker.getAttack() - target.getDefense());
        target.takeDamage(damage);
    }

    public void endTurn() {
        isPlayerTurn = !isPlayerTurn;
        if (isPlayerTurn) {
            turnCount++;
            // Reset turn for all characters
            for (Character c : allCharacters) {
                c.resetTurn();
                c.restoreMana(5); // Simple mana regen
            }
        }
        
        // Clean up dead characters
        allCharacters.removeIf(c -> !c.isAlive());
        
        checkGameOver();
    }

    private void checkGameOver() {
        boolean playerAlive = false;
        boolean enemyAlive = false;
        
        for (Character c : allCharacters) {
            if (c.isPlayerTeam()) playerAlive = true;
            else enemyAlive = true;
        }
        
        if (!playerAlive) {
            isGameOver = true;
            playerWon = false;
        } else if (!enemyAlive) {
            isGameOver = true;
            playerWon = true;
        }
    }
}
