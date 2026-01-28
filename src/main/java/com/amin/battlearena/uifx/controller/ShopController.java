package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.account.AccountRepository;
import com.amin.battlearena.domain.account.Player;
import com.amin.battlearena.domain.shop.Shop;
import com.amin.battlearena.infra.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShopController {

    @FXML private Label playerGold;
    @FXML private Label statusMessage;
    
    @FXML private Label healthPrice;
    @FXML private Label attackPrice;
    @FXML private Label armorPrice;
    @FXML private Label eaglePrice;
    @FXML private Label swiftPrice;
    @FXML private Label precisionPrice;
    @FXML private Label manaPrice;
    @FXML private Label spellPowerPrice;
    @FXML private Label cooldownPrice;
    
    @FXML private Label healthPotionPrice;
    @FXML private Label manaPotionPrice;
    @FXML private Label hastePotionPrice;

    private final Shop shop = new Shop();
    
    // Upgrade type indices
    private static final int WARRIOR_HEALTH = 0;
    private static final int WARRIOR_ATTACK = 1;
    private static final int WARRIOR_DEFENSE = 2;
    private static final int ARCHER_RANGE = 3;
    private static final int ARCHER_SPEED = 4;
    private static final int ARCHER_ATTACKS = 5;
    private static final int MAGE_MANA = 6;
    private static final int MAGE_SPELL_POWER = 7;
    private static final int MAGE_COOLDOWN = 8;
    
    // Consumable names
    private static final String HEALTH_POTION = "HealthPotion";
    private static final String MANA_POTION = "ManaPotion";
    private static final String HASTE_POTION = "HastePotion";

    @FXML
    public void initialize() {
        updateUI();
        setPrices();
    }

    private void setPrices() {
        healthPrice.setText(shop.getUpgradeCost(WARRIOR_HEALTH) + " G");
        attackPrice.setText(shop.getUpgradeCost(WARRIOR_ATTACK) + " G");
        armorPrice.setText(shop.getUpgradeCost(WARRIOR_DEFENSE) + " G");
        eaglePrice.setText(shop.getUpgradeCost(ARCHER_RANGE) + " G");
        swiftPrice.setText(shop.getUpgradeCost(ARCHER_SPEED) + " G");
        precisionPrice.setText(shop.getUpgradeCost(ARCHER_ATTACKS) + " G");
        manaPrice.setText(shop.getUpgradeCost(MAGE_MANA) + " G");
        spellPowerPrice.setText(shop.getUpgradeCost(MAGE_SPELL_POWER) + " G");
        cooldownPrice.setText(shop.getUpgradeCost(MAGE_COOLDOWN) + " G");
        
        healthPotionPrice.setText(shop.getConsumablePrice(HEALTH_POTION) + " G");
        manaPotionPrice.setText(shop.getConsumablePrice(MANA_POTION) + " G");
        hastePotionPrice.setText(shop.getConsumablePrice(HASTE_POTION) + " G");
    }

    private void updateUI() {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            playerGold.setText(String.valueOf(p.getGold()));
        }
    }

    private void buyUpgrade(int upgradeType) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            if (shop.purchaseUpgrade(p, upgradeType)) {
                AccountRepository.getInstance().savePlayer(p);
                updateUI();
                statusMessage.setText("Purchased " + shop.getUpgradeName(upgradeType) + " Upgrade!");
            } else {
                statusMessage.setText("Not enough gold for " + shop.getUpgradeName(upgradeType) + "!");
            }
        }
    }

    private void buyItem(String item, String name) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            if (shop.purchaseConsumable(p, item)) {
                AccountRepository.getInstance().savePlayer(p);
                updateUI();
                statusMessage.setText("Purchased " + name + "!");
            } else {
                statusMessage.setText("Not enough gold for " + name + "!");
            }
        }
    }

    @FXML public void onBuyHealthUpgrade() { buyUpgrade(WARRIOR_HEALTH); }
    @FXML public void onBuyAttackUpgrade() { buyUpgrade(WARRIOR_ATTACK); }
    @FXML public void onBuyDefenseUpgrade() { buyUpgrade(WARRIOR_DEFENSE); }
    @FXML public void onBuyRangeUpgrade() { buyUpgrade(ARCHER_RANGE); }
    @FXML public void onBuySpeedUpgrade() { buyUpgrade(ARCHER_SPEED); }
    @FXML public void onBuyPrecisionUpgrade() { buyUpgrade(ARCHER_ATTACKS); }
    @FXML public void onBuyManaUpgrade() { buyUpgrade(MAGE_MANA); }
    @FXML public void onBuySpellPowerUpgrade() { buyUpgrade(MAGE_SPELL_POWER); }
    @FXML public void onBuyCooldownUpgrade() { buyUpgrade(MAGE_COOLDOWN); }

    @FXML public void onBuyHealthPotion() { buyItem(HEALTH_POTION, "Health Potion"); }
    @FXML public void onBuyManaPotion() { buyItem(MANA_POTION, "Mana Potion"); }
    @FXML public void onBuyHastePotion() { buyItem(HASTE_POTION, "Haste Potion"); }

    @FXML
    public void onBack() {
        SceneManager.getInstance().switchScene("/uifx/main_menu.fxml");
    }
}
