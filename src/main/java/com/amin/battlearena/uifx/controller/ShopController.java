package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.Player;
import com.amin.battlearena.infra.SceneManager;
import com.amin.battlearena.persistence.AccountRepository;
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
    @FXML private Label revivalTokenPrice;

    private final int COST_HEALTH = 100;
    private final int COST_ATTACK = 150;
    private final int COST_DEFENSE = 120;
    private final int COST_RANGE = 200;
    private final int COST_SPEED = 180;
    private final int COST_PRECISION = 150;
    private final int COST_MANA = 100;
    private final int COST_SPELL = 150;
    private final int COST_COOLDOWN = 250;
    
    private final int COST_POTION_HP = 50;
    private final int COST_POTION_MP = 30;
    private final int COST_POTION_HASTE = 75;
    private final int COST_TOKEN_REVIVE = 500;

    @FXML
    public void initialize() {
        updateUI();
        setPrices();
    }

    private void setPrices() {
        healthPrice.setText(COST_HEALTH + " G");
        attackPrice.setText(COST_ATTACK + " G");
        armorPrice.setText(COST_DEFENSE + " G");
        eaglePrice.setText(COST_RANGE + " G");
        swiftPrice.setText(COST_SPEED + " G");
        precisionPrice.setText(COST_PRECISION + " G");
        manaPrice.setText(COST_MANA + " G");
        spellPowerPrice.setText(COST_SPELL + " G");
        cooldownPrice.setText(COST_COOLDOWN + " G");
        
        healthPotionPrice.setText(COST_POTION_HP + " G");
        manaPotionPrice.setText(COST_POTION_MP + " G");
        hastePotionPrice.setText(COST_POTION_HASTE + " G");
        revivalTokenPrice.setText(COST_TOKEN_REVIVE + " G");
    }

    private void updateUI() {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            playerGold.setText(String.valueOf(p.getGold()));
        }
    }

    private void buyUpgrade(int type, int cost, String name) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            if (p.getGold() >= cost) {
                p.spendGold(cost);
                p.upgrade(type);
                AccountRepository.getInstance().savePlayer(p);
                updateUI();
                statusMessage.setText("Purchased " + name + " Upgrade!");
            } else {
                statusMessage.setText("Not enough gold for " + name + "!");
            }
        }
    }

    private void buyItem(String item, int cost, String name) {
        Player p = AccountRepository.getInstance().getCurrentUser();
        if (p != null) {
            if (p.getGold() >= cost) {
                p.spendGold(cost);
                p.addItem(item);
                AccountRepository.getInstance().savePlayer(p);
                updateUI();
                statusMessage.setText("Purchased " + name + "!");
            } else {
                statusMessage.setText("Not enough gold for " + name + "!");
            }
        }
    }

    @FXML public void onBuyHealthUpgrade() { buyUpgrade(0, COST_HEALTH, "Health"); }
    @FXML public void onBuyAttackUpgrade() { buyUpgrade(1, COST_ATTACK, "Attack"); }
    @FXML public void onBuyDefenseUpgrade() { buyUpgrade(2, COST_DEFENSE, "Defense"); }
    @FXML public void onBuyRangeUpgrade() { buyUpgrade(3, COST_RANGE, "Range"); }
    @FXML public void onBuySpeedUpgrade() { buyUpgrade(4, COST_SPEED, "Speed"); }
    @FXML public void onBuyPrecisionUpgrade() { buyUpgrade(5, COST_PRECISION, "Precision"); }
    @FXML public void onBuyManaUpgrade() { buyUpgrade(6, COST_MANA, "Mana"); }
    @FXML public void onBuySpellPowerUpgrade() { buyUpgrade(7, COST_SPELL, "Spell Power"); }
    @FXML public void onBuyCooldownUpgrade() { buyUpgrade(8, COST_COOLDOWN, "Cooldown"); }

    @FXML public void onBuyHealthPotion() { buyItem("Health Potion", COST_POTION_HP, "Health Potion"); }
    @FXML public void onBuyManaPotion() { buyItem("Mana Potion", COST_POTION_MP, "Mana Potion"); }
    @FXML public void onBuyHastePotion() { buyItem("Haste Potion", COST_POTION_HASTE, "Haste Potion"); }
    @FXML public void onBuyRevivalToken() { buyItem("Revival Token", COST_TOKEN_REVIVE, "Revival Token"); }

    @FXML
    public void onBack() {
        SceneManager.getInstance().switchScene("/uifx/main_menu.fxml");
    }
}
