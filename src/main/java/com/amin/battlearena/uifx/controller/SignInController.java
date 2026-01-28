package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.domain.account.AccountRepository;
import com.amin.battlearena.infra.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SignInController {

    @FXML
    private TextField nicknameField;

    @FXML
    public void onLogin() {
        String nickname = nicknameField.getText();
        if (nickname != null && !nickname.trim().isEmpty()) {
            AccountRepository.getInstance().login(nickname.trim());
            SceneManager.getInstance().switchScene("/uifx/main_menu.fxml");
        }
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }
}
