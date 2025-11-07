package com.amin.battlearena.uifx.handler;

import com.amin.battlearena.domain.model.Character;
import com.amin.battlearena.infra.CharacterBalanceConfig;
import com.amin.battlearena.infra.CharacterBalanceConfig.AbilityConfig;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Handles ability-related UI operations and display
public class AbilityUIHandler {
    
    @FunctionalInterface
    public interface AbilitySelectionCallback {
        void onAbilitySelected(int index, String abilityName);
    }
    
    private final VBox abilitiesContainer;
    private String selectedAbility;
    private int selectedAbilityIndex = -1;
    private AbilitySelectionCallback selectionCallback;
    
    public AbilityUIHandler(VBox abilitiesContainer) {
        this.abilitiesContainer = abilitiesContainer;
    }
    
    public void setSelectionCallback(AbilitySelectionCallback callback) {
        this.selectionCallback = callback;
    }
    
    public void updateAbilitiesDisplay(Character character) {
        if (abilitiesContainer != null) {
            abilitiesContainer.getChildren().clear();
            
            if (character != null && character.getAbilities() != null) {
                for (int i = 0; i < character.getAbilities().size(); i++) {
                    var ability = character.getAbilities().get(i);
                    VBox abilityDisplay = createAbilityDisplay(ability, i);
                    abilitiesContainer.getChildren().add(abilityDisplay);
                }
            }
        }
    }
    
    private VBox createAbilityDisplay(com.amin.battlearena.domain.abilities.Ability ability, int index) {
        VBox abilityBox = new VBox(3);
        abilityBox.setAlignment(Pos.CENTER_LEFT);
        abilityBox.getStyleClass().add("ability-display");
        
        // Make clickable for selection
        abilityBox.setOnMouseClicked(e -> selectAbility(index, ability.getName()));
        
        // Ability header with icon and name
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(getAbilityIcon(ability));
        icon.getStyleClass().add("ability-icon");
        
        Label name = new Label(ability.getName());
        name.getStyleClass().add("ability-name");
        name.setFont(Font.font("System", FontWeight.BOLD, 11));
        
        header.getChildren().addAll(icon, name);
        
        // Mana cost
        HBox manaBox = new HBox(3);
        manaBox.setAlignment(Pos.CENTER_LEFT);
        Label manaIcon = new Label("✨");
        manaIcon.getStyleClass().add("mana-icon");
        manaIcon.setStyle("-fx-text-fill: #87CEEB;"); // Light blue color for mana icon
        Label manaCost = new Label("Mana Cost: " + ability.getManaCost());
        manaCost.getStyleClass().add("stat-value"); // Use stat-value for white text
        manaBox.getChildren().addAll(manaIcon, manaCost);
        
        // Damage information
        HBox damageBox = new HBox(3);
        damageBox.setAlignment(Pos.CENTER_LEFT);
        Label damageIcon = new Label("⭐");
        damageIcon.getStyleClass().add("damage-icon");
        damageIcon.setStyle("-fx-text-fill: #FFD700;"); // Gold color for star icon
        
        // Display base damage estimate based on ability type with descriptive label
        String damageType = estimateAbilityDamage(ability);
        Label damageLabel = new Label(damageType + ": " + getEstimatedDamageValue(ability));
        damageLabel.getStyleClass().add("stat-value"); // White text
        damageBox.getChildren().addAll(damageIcon, damageLabel);
        
        // Cooldown status
        HBox cooldownBox = new HBox(3);
        cooldownBox.setAlignment(Pos.CENTER_LEFT);
        Label cooldownIcon = new Label("⏱");
        cooldownIcon.getStyleClass().add("cooldown-icon");
        cooldownIcon.setStyle("-fx-text-fill: #FFA500;"); // Orange color for clock icon
        
        String cooldownText;
        if (ability.getRemainingCooldown() > 0) {
            cooldownText = "Cooldown: " + ability.getRemainingCooldown();
        } else {
            cooldownText = "Cooldown: Ready";
        }
        
        Label cooldownLabel = new Label(cooldownText);
        cooldownLabel.getStyleClass().add("stat-value"); // Use stat-value for consistent white text
        cooldownBox.getChildren().addAll(cooldownIcon, cooldownLabel);
        
        abilityBox.getChildren().addAll(header, manaBox, damageBox, cooldownBox);
        
        // Highlight if selected
        if (index == selectedAbilityIndex) {
            abilityBox.getStyleClass().add("selected");
        }
        
        return abilityBox;
    }
    
    private String getAbilityIcon(com.amin.battlearena.domain.abilities.Ability ability) {
        String name = ability.getName().toLowerCase();
        if (name.contains("heal")) return "💚";
        if (name.contains("fire") || name.contains("flame")) return "🔥";
        if (name.contains("ice") || name.contains("frost")) return "❄️";
        if (name.contains("lightning") || name.contains("thunder")) return "⚡";
        if (name.contains("arrow") || name.contains("shot")) return "🏹";
        if (name.contains("slash") || name.contains("strike")) return "⚔️";
        if (name.contains("shield") || name.contains("defend")) return "🛡️";
        if (name.contains("teleport") || name.contains("blink")) return "✨";
        return "🎯"; // Default ability icon
    }
    
    /**
     * Gets damage type label from balance.json config (physical/magical/utility).
     * REFACTORED: Removed hardcoded string-based heuristics (name.contains).
     */
    private String estimateAbilityDamage(com.amin.battlearena.domain.abilities.Ability ability) {
        try {
            // Query balance.json for actual damage type
            AbilityConfig config = getAbilityConfigSafe(ability.getName());
            if (config != null) {
                String damageType = config.getDamageType();
                // Convert type to user-friendly label
                return switch (damageType.toLowerCase()) {
                    case "physical" -> "Physical Damage";
                    case "magical" -> "Magic Damage";
                    case "utility" -> "Utility";
                    default -> "Damage";
                };
            }
        } catch (Exception e) {
            // Fallback to default if config not found
        }
        
        return "Damage"; // Default fallback
    }
    
    /**
     * Gets damage formula from balance.json config.
     * REFACTORED: Removed 7 hardcoded damage formulas (manaCost + 10, etc.).
     */
    private String getEstimatedDamageValue(com.amin.battlearena.domain.abilities.Ability ability) {
        try {
            // Query balance.json for actual damage formula
            AbilityConfig config = getAbilityConfigSafe(ability.getName());
            if (config != null) {
                return config.getDamageFormula();
            }
        } catch (Exception e) {
            // Fallback to mana cost if config not found
        }
        
        // Fallback: estimate based on mana cost
        return "~" + (ability.getManaCost() + 3);
    }
    
    /**
     * Safely retrieves ability config from balance.json.
     * Returns null if ability not found (instead of throwing exception).
     */
    private AbilityConfig getAbilityConfigSafe(String abilityName) {
        try {
            // Map ability names to config IDs (e.g., "Power Strike" → "PowerStrike")
            String abilityId = abilityName.replace(" ", "");
            return CharacterBalanceConfig.getInstance().getAbilityConfig(abilityId);
        } catch (Exception e) {
            // Config not found - return null for fallback
            return null;
        }
    }
    
    public void selectAbility(int index, String abilityName) {
        selectedAbilityIndex = index;
        selectedAbility = abilityName;
        
        // Notify callback if set
        if (selectionCallback != null) {
            selectionCallback.onAbilitySelected(index, abilityName);
        }
        
        // Refresh UI to show selection
        refreshSelectionDisplay();
    }
    
    private void refreshSelectionDisplay() {
        // Update visual selection highlighting for all ability boxes
        for (int i = 0; i < abilitiesContainer.getChildren().size(); i++) {
            javafx.scene.Node child = abilitiesContainer.getChildren().get(i);
            if (child instanceof VBox) {
                VBox abilityBox = (VBox) child;
                if (i == selectedAbilityIndex) {
                    if (!abilityBox.getStyleClass().contains("selected")) {
                        abilityBox.getStyleClass().add("selected");
                    }
                } else {
                    abilityBox.getStyleClass().remove("selected");
                }
            }
        }
    }
    
    public String getSelectedAbility() {
        return selectedAbility;
    }
    
    public int getSelectedAbilityIndex() {
        return selectedAbilityIndex;
    }
    
    public void clearSelection() {
        selectedAbilityIndex = -1;
        selectedAbility = null;
    }
}