package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.account.AccountRepository;
import com.amin.battlearena.domain.account.Player;
import com.amin.battlearena.infra.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label goldAmount;

    @FXML
    public void initialize() {
        Player player = AccountRepository.getInstance().getCurrentUser();
        if (player != null) {
            welcomeLabel.setText("Welcome, " + player.getNickname() + "!");
            goldAmount.setText(String.valueOf(player.getGold()));
        }
    }

    @FXML
    public void onCampaign() {
        SceneManager.getInstance().switchScene("/uifx/campaign.fxml");
    }

    @FXML
    public void onShop() {
        SceneManager.getInstance().switchScene("/uifx/shop.fxml");
    }

    @FXML
    public void onHelp() {
        SceneManager.getInstance().switchScene("/uifx/help.fxml");
    }

    @FXML
    public void onChangeAccount() {
        AccountRepository.getInstance().logout();
        SceneManager.getInstance().switchScene("/uifx/signin.fxml");
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }
}
