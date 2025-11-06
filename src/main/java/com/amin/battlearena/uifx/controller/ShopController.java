package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.economy.UpgradeCatalog;
import com.amin.battlearena.persistence.PlayerData;
import com.amin.battlearena.persistence.PlayerDataManager;
import com.amin.battlearena.uifx.MainApp;
import com.amin.battlearena.uifx.handler.ConsumablePurchaseHandler;
import com.amin.battlearena.uifx.handler.ShopUIHandler;
import com.amin.battlearena.uifx.handler.UpgradePurchaseHandler;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

// Refactored Shop Controller using delegation patterns
// NOW USES UpgradeCatalog as single source of truth for pricing
public class ShopController {
    
    private MainApp app;
    private PlayerData playerData;
    private String currentPlayerName = "Player";
    
    // Delegation pattern: Specialized handlers for different operations
    private UpgradePurchaseHandler upgradeHandler;
    private ConsumablePurchaseHandler consumableHandler;
    private ShopUIHandler uiHandler;
    
    @FXML private Label playerGold;
    @FXML private Label statusMessage;
    @FXML private Button backBtn;
    
    // Upgrade buttons
    @FXML private Button healthUpgradeBtn;
    @FXML private Button attackUpgradeBtn;
    @FXML private Button armorUpgradeBtn;
    @FXML private Button eagleEyeBtn;
    @FXML private Button swiftStepsBtn;
    @FXML private Button precisionShotBtn;
    @FXML private Button manaUpgradeBtn;
    @FXML private Button spellPowerBtn;
    @FXML private Button cooldownUpgradeBtn;

    // Dynamic price labels
    @FXML private Label healthPrice;
    @FXML private Label attackPrice;
    @FXML private Label armorPrice;
    @FXML private Label eaglePrice;
    @FXML private Label swiftPrice;
    @FXML private Label precisionPrice;
    @FXML private Label manaPrice;
    @FXML private Label spellPowerPrice;
    @FXML private Label cooldownPrice;
    
    public void setApp(MainApp app) {
        this.app = app;
        loadPlayerData();
        updateGoldDisplay();
        updateLabels();
        updateConsumableButtons();
    }
    
    public void setPlayerName(String playerName) {
        this.currentPlayerName = playerName;
        loadPlayerData();
        updateGoldDisplay();
        updateUpgradeButtons();
        updateConsumableButtons();
    }
    
    private void loadPlayerData() {
        this.playerData = PlayerDataManager.getInstance().loadPlayerData(currentPlayerName);
    }
    
    @FXML
    public void initialize() {
        // Initialize delegation handlers
        upgradeHandler = new UpgradePurchaseHandler();
        consumableHandler = new ConsumablePurchaseHandler();
        uiHandler = new ShopUIHandler(playerGold, statusMessage);
        
        // Initialize the shop
        loadPlayerData();
        updateGoldDisplay();
        
        // Populate all prices from UpgradeCatalog (single source of truth)
        populateUpgradePricesFromCatalog();
        
        updateUpgradeButtons();
        updateConsumableButtons();
        updateLabels();
        
        // Add entrance animation
        if (statusMessage != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(1000), statusMessage);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }
    
    // Populate ALL upgrade prices from UpgradeCatalog on initialization
    private void populateUpgradePricesFromCatalog() {
        // All prices now loaded from UpgradeCatalog - single source of truth
        updatePriceLabel("Health Boost");
        updatePriceLabel("Attack Power");
        updatePriceLabel("Armor Boost");
        updatePriceLabel("Eagle Eye");
        updatePriceLabel("Swift Steps");
        updatePriceLabel("Precision Shot");
        updatePriceLabel("Mana Pool");
        updatePriceLabel("Spell Power");
        updatePriceLabel("Quick Cast");
    }
    
    @FXML
    public void onBack() {
        // Save player data before leaving
        if (playerData != null) {
            PlayerDataManager.getInstance().savePlayerData(playerData);
        }
        if (app != null) {
            app.switchToMainMenu(currentPlayerName);
        }
    }
    
    // Character Upgrade Purchases
    
    // Upgrade purchase methods using delegation pattern
    @FXML
    public void onBuyHealthUpgrade() {
        purchaseUpgrade("Health Boost", "❤ +20 Max Health purchased!");
    }
    
    @FXML
    public void onBuyAttackUpgrade() {
        purchaseUpgrade("Attack Power", "⚔ +5 Attack Damage purchased!");
    }
    
    @FXML
    public void onBuyDefenseUpgrade() {
        purchaseUpgrade("Armor Boost", "🛡 +3 Defense Rating purchased!");
    }
    
    @FXML
    public void onBuyRangeUpgrade() {
        purchaseUpgrade("Eagle Eye", "🎯 +2 Attack Range purchased!");
    }
    
    @FXML
    public void onBuySpeedUpgrade() {
        purchaseUpgrade("Swift Steps", "🏃 +1 Movement Speed purchased!");
    }
    
    @FXML
    public void onBuyPrecisionUpgrade() {
        purchaseUpgrade("Precision Shot", "💥 +15% Critical Chance purchased!");
    }
    
    @FXML
    public void onBuyManaUpgrade() {
        purchaseUpgrade("Mana Pool", "✨ +30 Max Mana purchased!");
    }
    
    @FXML
    public void onBuySpellPowerUpgrade() {
        purchaseUpgrade("Spell Power", "🌟 +8 Spell Damage purchased!");
    }
    
    @FXML
    public void onBuyCooldownUpgrade() {
        purchaseUpgrade("Quick Cast", "⏰ -1 Ability Cooldown purchased!");
    }
    
    // Consumable Purchases
    
    @FXML
    public void onBuyHealthPotion() {
        purchaseConsumable("health_potion", 25, "❤️‍🩹 Health Potion added to inventory!");
    }
    
    @FXML
    public void onBuyManaPotion() {
        purchaseConsumable("mana_potion", 20, "🧪 Mana Potion added to inventory!");
    }
    
    @FXML
    public void onBuyStrengthElixir() {
        purchaseConsumable("strength_elixir", 40, "💪 Strength Elixir added to inventory!");
    }
    
    @FXML
    public void onBuyShieldScroll() {
        purchaseConsumable("shield_scroll", 35, "🛡️ Shield Scroll added to inventory!");
    }
    
    @FXML
    public void onBuyHastePotion() {
        purchaseConsumable("haste_potion", 30, "🏃‍♂️ Haste Potion added to inventory!");
    }
    
    @FXML
    public void onBuyRevivalToken() {
        purchaseConsumable("revival_token", 100, "💫 Revival Token added to inventory!");
    }
    
    private void purchaseUpgrade(String upgradeName, String successMessage) {
        if (upgradeHandler.purchaseUpgrade(playerData, upgradeName, successMessage, uiHandler)) {
            updateGoldDisplay();
            updateUpgradeButtons();
        }
    }
    
    private void purchaseConsumable(String itemId, int cost, String successMessage) {
        if (consumableHandler.purchaseConsumable(playerData, itemId, cost, successMessage, uiHandler)) {
            updateGoldDisplay();
            updateConsumableButtons();
        }
    }
    
    private void updateUpgradeButtons() {
        if (playerData == null) return;
        
        // Update button texts to show current level and cost
        updateUpgradeButton("Health Boost", healthUpgradeBtn);
        updateUpgradeButton("Attack Power", attackUpgradeBtn);
        updateUpgradeButton("Armor Boost", armorUpgradeBtn);
        updateUpgradeButton("Eagle Eye", eagleEyeBtn);
        updateUpgradeButton("Swift Steps", swiftStepsBtn);
        updateUpgradeButton("Precision Shot", precisionShotBtn);
        updateUpgradeButton("Mana Pool", manaUpgradeBtn);
        updateUpgradeButton("Spell Power", spellPowerBtn);
        updateUpgradeButton("Quick Cast", cooldownUpgradeBtn);
    }
    
    private void updateUpgradeButton(String upgradeName, Button button) {
        uiHandler.updateUpgradeButton(upgradeName, button, playerData, upgradeHandler);
        updatePriceLabel(upgradeName);
    }
    
    private void updatePriceLabel(String upgradeName) {
        if (playerData == null) return;
        int currentLevel = playerData.getUpgradeLevel(upgradeName);
        int maxLevel = UpgradeCatalog.getMaxUpgradeLevel(upgradeName);
        
        String text = (currentLevel >= maxLevel) ? "MAX" : 
                     "💰 " + UpgradeCatalog.calculateUpgradeCost(upgradeName, currentLevel);
        setPriceLabel(upgradeName, text);
    }

    private void setPriceLabel(String upgradeName, String text) {
        // Update the fx:id price labels next to each upgrade, if present
        switch (upgradeName) {
            case "Health Boost" -> {
                if (healthPrice != null) healthPrice.setText(text);
            }
            case "Attack Power" -> {
                if (attackPrice != null) attackPrice.setText(text);
            }
            case "Armor Boost" -> {
                if (armorPrice != null) armorPrice.setText(text);
            }
            case "Eagle Eye" -> {
                if (eaglePrice != null) eaglePrice.setText(text);
            }
            case "Swift Steps" -> {
                if (swiftPrice != null) swiftPrice.setText(text);
            }
            case "Precision Shot" -> {
                if (precisionPrice != null) precisionPrice.setText(text);
            }
            case "Mana Pool" -> {
                if (manaPrice != null) manaPrice.setText(text);
            }
            case "Spell Power" -> {
                if (spellPowerPrice != null) spellPowerPrice.setText(text);
            }
            case "Quick Cast" -> {
                if (cooldownPrice != null) cooldownPrice.setText(text);
            }
            default -> {
                // no label to set
            }
        }
    }
    
    private void updateGoldDisplay() {
        uiHandler.updateGoldDisplay(playerData);
        // Update affordability states
        updateUpgradeButtons();
        updateConsumableButtons();
    }

    // Language manager removed for simplified architecture

    @FXML private Button healthBuyBtn, manaBuyBtn, strengthBuyBtn, shieldBuyBtn, hasteBuyBtn, revivalBuyBtn;

    private void updateConsumableButtons() {
        if (playerData == null) return;
        uiHandler.updateConsumableButton(healthBuyBtn, consumableHandler.getConsumableCost("health_potion"), playerData);
        uiHandler.updateConsumableButton(manaBuyBtn, consumableHandler.getConsumableCost("mana_potion"), playerData);
        uiHandler.updateConsumableButton(strengthBuyBtn, consumableHandler.getConsumableCost("strength_potion"), playerData);
        uiHandler.updateConsumableButton(shieldBuyBtn, consumableHandler.getConsumableCost("shield_potion"), playerData);
        uiHandler.updateConsumableButton(hasteBuyBtn, consumableHandler.getConsumableCost("haste_potion"), playerData);
        uiHandler.updateConsumableButton(revivalBuyBtn, consumableHandler.getConsumableCost("revival_scroll"), playerData);
    }

    private void updateLabels() {
        // Language manager removed for simplified architecture
        if (backBtn != null) backBtn.setText("Back");
        if (statusMessage != null && (statusMessage.getText() == null || statusMessage.getText().isBlank())) {
            statusMessage.setText("Shop");
        }
    }
    
    // showSuccessMessage and showErrorMessage methods removed - now delegated to ShopUIHandler
    
    public void loadPlayerGold(int gold) {
        if (playerData != null) {
            playerData.setGold(gold);
            updateGoldDisplay();
        }
    }
    
    public int getCurrentGold() {
        return playerData != null ? playerData.getGold() : 0;
    }
}
