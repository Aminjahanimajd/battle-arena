package com.amin.battlearena.uifx.rendering;

import com.amin.battlearena.domain.model.Character;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

// CharacterDesigner handles visual design for different character types
public class CharacterDesigner {
    
    public static void applyCharacterDesign(StackPane parent, Circle base, Character character) {
        String characterType = character.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        if ("warrior".equals(characterType)) {
            createWarriorDesign(parent, base, character);
        } else if ("archer".equals(characterType)) {
            createArcherDesign(parent, base, character);
        } else if ("mage".equals(characterType)) {
            createMageDesign(parent, base, character);
        } else if ("knight".equals(characterType)) {
            createKnightDesign(parent, base, character);
        } else if ("ranger".equals(characterType)) {
            createRangerDesign(parent, base, character);
        } else {
            createGenericDesign(parent, base, character);
        }
    }
    
    private static void createWarriorDesign(StackPane parent, Circle base, Character character) {
        // Strong, bold design with shield motif
        base.setFill(Color.DARKRED);
        base.setStroke(Color.GOLD);
        base.setStrokeWidth(3);
        
        // Shield symbol
        Polygon shield = new Polygon();
        shield.getPoints().addAll(new Double[]{
            0.0, -12.0,  // Top
            -8.0, -4.0,  // Left top
            -8.0, 8.0,   // Left bottom
            0.0, 12.0,   // Bottom point
            8.0, 8.0,    // Right bottom
            8.0, -4.0    // Right top
        });
        shield.setFill(Color.SILVER);
        shield.setStroke(Color.GOLD);
        shield.setStrokeWidth(1);
        
        Label icon = new Label("⚔️");
        icon.setFont(Font.font(14));
        
        parent.getChildren().addAll(shield, icon);
        parent.getStyleClass().add("warrior-character");
    }
    
    private static void createArcherDesign(StackPane parent, Circle base, Character character) {
        // Sleek, precise design with bow motif
        base.setFill(Color.FORESTGREEN);
        base.setStroke(Color.LIGHTGREEN);
        base.setStrokeWidth(2);
        
        // Bow shape
        Rectangle bow = new Rectangle(2, 16);
        bow.setFill(Color.SADDLEBROWN);
        bow.setArcWidth(4);
        bow.setArcHeight(4);
        
        // Arrow
        Rectangle arrow = new Rectangle(12, 1);
        arrow.setFill(Color.GOLDENROD);
        
        Label icon = new Label("🏹");
        icon.setFont(Font.font(12));
        
        parent.getChildren().addAll(bow, arrow, icon);
        parent.getStyleClass().add("archer-character");
    }
    
    private static void createMageDesign(StackPane parent, Circle base, Character character) {
        // Mystical design with magical elements
        base.setFill(Color.DARKBLUE);
        base.setStroke(Color.MEDIUMPURPLE);
        base.setStrokeWidth(2);
        
        // Magic circle
        Circle innerCircle = new Circle(10);
        innerCircle.setFill(null);
        innerCircle.setStroke(Color.CYAN);
        innerCircle.setStrokeWidth(1);
        
        // Magic stars
        Label star1 = new Label("✦");
        star1.setTextFill(Color.LIGHTBLUE);
        star1.setFont(Font.font(8));
        star1.setTranslateX(-8);
        star1.setTranslateY(-8);
        
        Label star2 = new Label("✧");
        star2.setTextFill(Color.LIGHTCYAN);
        star2.setFont(Font.font(6));
        star2.setTranslateX(8);
        star2.setTranslateY(8);
        
        Label icon = new Label("🔮");
        icon.setFont(Font.font(14));
        
        parent.getChildren().addAll(innerCircle, star1, star2, icon);
        parent.getStyleClass().add("mage-character");
    }
    
    private static void createKnightDesign(StackPane parent, Circle base, Character character) {
        // Noble, armored design with heraldic elements
        base.setFill(Color.DARKSLATEBLUE);
        base.setStroke(Color.GOLD);
        base.setStrokeWidth(3);
        
        // Crown shape
        Polygon crown = new Polygon();
        crown.getPoints().addAll(new Double[]{
            -8.0, -8.0,
            -4.0, -12.0,
            0.0, -8.0,
            4.0, -12.0,
            8.0, -8.0,
            8.0, -4.0,
            -8.0, -4.0
        });
        crown.setFill(Color.GOLD);
        crown.setStroke(Color.ORANGE);
        
        Label icon = new Label("🛡️");
        icon.setFont(Font.font(12));
        
        parent.getChildren().addAll(crown, icon);
        parent.getStyleClass().add("knight-character");
    }
    
    private static void createRangerDesign(StackPane parent, Circle base, Character character) {
        // Natural, earthy design with nature motifs
        base.setFill(Color.DARKOLIVEGREEN);
        base.setStroke(Color.YELLOWGREEN);
        base.setStrokeWidth(2);
        
        // Leaves
        Label leaf1 = new Label("🍃");
        leaf1.setFont(Font.font(8));
        leaf1.setTranslateX(-6);
        leaf1.setTranslateY(-6);
        
        Label leaf2 = new Label("🍃");
        leaf2.setFont(Font.font(6));
        leaf2.setTranslateX(6);
        leaf2.setTranslateY(6);
        leaf2.setRotate(180);
        
        Label icon = new Label("🌿");
        icon.setFont(Font.font(14));
        
        parent.getChildren().addAll(leaf1, leaf2, icon);
        parent.getStyleClass().add("ranger-character");
    }
    
    private static void createGenericDesign(StackPane parent, Circle base, Character character) {
        // Default design for unknown character types
        base.setFill(Color.GRAY);
        base.setStroke(Color.LIGHTGRAY);
        base.setStrokeWidth(2);
        
        Label icon = new Label("⚔️");
        icon.setFont(Font.font(12));
        
        parent.getChildren().add(icon);
        parent.getStyleClass().add("generic-character");
    }
}