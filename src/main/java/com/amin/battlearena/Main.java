package com.amin.battlearena;

import com.amin.battlearena.infra.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstance().setStage(primaryStage);
        SceneManager.getInstance().switchScene("/uifx/signin.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
