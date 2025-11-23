package com.amin.battlearena.uifx.controller;

import com.amin.battlearena.infra.SceneManager;
import javafx.fxml.FXML;

public class HelpController {

    @FXML
    public void onBack() {
        SceneManager.getInstance().switchScene("/uifx/main_menu.fxml");
    }
}
