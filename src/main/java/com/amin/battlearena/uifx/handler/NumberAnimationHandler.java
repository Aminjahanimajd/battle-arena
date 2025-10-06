package com.amin.battlearena.uifx.handler;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Handler for number animation effects with delegation pattern
 */
public class NumberAnimationHandler {
    
    /**
     * Show damage number animation with critical hit support
     */
    public void showDamageNumber(Pane parent, double x, double y, int damage, boolean isCritical) {
        Label damageLabel = new Label("-" + damage);
        damageLabel.setLayoutX(x - 15);
        damageLabel.setLayoutY(y - 20);
        
        if (isCritical) {
            damageLabel.setTextFill(Color.RED);
            damageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            damageLabel.setTextFill(Color.ORANGE);
            damageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        }
        
        parent.getChildren().add(damageLabel);
        
        // Animate number floating up
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(1000), damageLabel);
        moveUp.setByY(-50);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), damageLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Scale effect for critical hits
        if (isCritical) {
            ScaleTransition critical = new ScaleTransition(Duration.millis(200), damageLabel);
            critical.setFromX(1.0);
            critical.setFromY(1.0);
            critical.setToX(1.5);
            critical.setToY(1.5);
            critical.setAutoReverse(true);
            critical.setCycleCount(2);
            critical.play();
        }
        
        moveUp.setOnFinished(e -> parent.getChildren().remove(damageLabel));
        moveUp.play();
        fadeOut.play();
    }
    
    /**
     * Show healing number animation
     */
    public void showHealingNumber(Pane parent, double x, double y, int healing) {
        Label healLabel = new Label("+" + healing);
        healLabel.setLayoutX(x - 15);
        healLabel.setLayoutY(y - 20);
        healLabel.setTextFill(Color.LIGHTGREEN);
        healLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        parent.getChildren().add(healLabel);
        
        // Animate number floating up
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(1000), healLabel);
        moveUp.setByY(-50);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), healLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Gentle scale for healing
        ScaleTransition heal = new ScaleTransition(Duration.millis(300), healLabel);
        heal.setFromX(0.8);
        heal.setFromY(0.8);
        heal.setToX(1.2);
        heal.setToY(1.2);
        heal.setAutoReverse(true);
        heal.setCycleCount(2);
        heal.play();
        
        moveUp.setOnFinished(e -> parent.getChildren().remove(healLabel));
        moveUp.play();
        fadeOut.play();
    }
}