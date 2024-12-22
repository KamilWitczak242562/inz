package com.example.client.controller;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import static com.example.client.utils.Utils.switchToView;

public class LoadingController {

    public void initialize() {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> switchToView("login-view.fxml"));
        delay.play();
    }

}
