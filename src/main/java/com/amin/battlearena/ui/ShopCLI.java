package com.amin.battlearena.ui;

import java.util.Scanner;

import com.amin.battlearena.economy.ShopService;
import com.amin.battlearena.persistence.PlayerDAO;
import com.amin.battlearena.players.Player;

/**
 * Minimal CLI for the shop that is compatible with the existing ShopService and PlayerDAO.
 *
 * Usage:
 *   ShopCLI.open(scanner, shopService, playerName, player);
 */
public final class ShopCLI {

    public static void open(Scanner sc, ShopService shop, String playerName, Player player) {
        PlayerDAO dao = new PlayerDAO();
        System.out.println("==== Shop ====");
        boolean running = true;
        while (running) {
            int gold = dao.findGold(playerName).orElse(0);
            System.out.println("Player: " + playerName + " | Gold: " + gold);
            System.out.println("1) Purchase stat upgrade");
            System.out.println("2) Purchase ability");
            System.out.println("0) Exit");
            System.out.print("Choice: ");
            int choice = -1;
            try { choice = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignored) {}
            switch (choice) {
                case 1 -> handleStatUpgrade(sc, shop, playerName, player);
                case 2 -> handlePurchaseAbility(sc, shop, playerName, player);
                case 0 -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleStatUpgrade(Scanner sc, ShopService shop, String playerName, Player player) {
        System.out.print("Character name: ");
        String cname = sc.nextLine().trim();
        System.out.print("Stat key (attack/defense/hp): ");
        String stat = sc.nextLine().trim();
        System.out.print("Delta (positive integer, e.g., 5): ");
        int delta = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Cost (gold): ");
        int cost = Integer.parseInt(sc.nextLine().trim());

        boolean ok = shop.purchaseStatUpgrade(playerName, player, cname, stat, delta, cost);
        System.out.println(ok ? "Upgrade purchased." : "Purchase failed.");
    }

    private static void handlePurchaseAbility(Scanner sc, ShopService shop, String playerName, Player player) {
        System.out.print("Character name: ");
        String cname = sc.nextLine().trim();
        System.out.print("Ability key (e.g., PowerStrike): ");
        String ability = sc.nextLine().trim();
        System.out.print("Cost (gold): ");
        int cost = Integer.parseInt(sc.nextLine().trim());

        boolean ok = shop.purchaseAbility(playerName, player, cname, ability, cost);
        System.out.println(ok ? "Ability purchased." : "Purchase failed.");
    }
}
