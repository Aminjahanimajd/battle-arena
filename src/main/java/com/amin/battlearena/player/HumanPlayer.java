package com.amin.battlearena.player;

import com.amin.battlearena.engine.GameEngine;
import com.amin.battlearena.model.Character;
import com.amin.battlearena.actions.AttackAction;
import java.util.List;
import java.util.Scanner;

public final class HumanPlayer extends Player {
    public HumanPlayer(String name) { super(name); }

    @Override
    public void takeTurn(GameEngine game) throws Exception {
        Scanner sc = new Scanner(System.in);
        List<Character> my = getTeam().stream().filter(Character::isAlive).toList();
        List<Character> enemy = game.getOpponentOf(this).getTeam().stream()
                .filter(Character::isAlive).toList();

        game.log("Your units:");
        for (int i=0;i<my.size();i++)
            game.log(i + ") " + my.get(i).getName() + " (HP " + my.get(i).getStats().getHp() + ")");

        game.log("Choose attacker index:");
        int ai = Integer.parseInt(sc.nextLine());
        Character attacker = my.get(ai);

        game.log("Enemy units:");
        for (int j=0;j<enemy.size();j++)
            game.log(j + ") " + enemy.get(j).getName() + " (HP " + enemy.get(j).getStats().getHp() + ")");

        game.log("Choose target index:");
        int ti = Integer.parseInt(sc.nextLine());
        Character target = enemy.get(ti);

        new AttackAction().execute(game, attacker, target);
    }
}