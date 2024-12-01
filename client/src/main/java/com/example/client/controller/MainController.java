package com.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.example.client.utils.Utils.switchToView;

public class MainController {

    @FXML
    private void goToView1() {
        switchToView("view1.fxml");
    }

    @FXML
    private void goToView2() {
        switchToView("view2.fxml");
    }

    @FXML
    private void goToView3() {
        switchToView("view3.fxml");
    }

    @FXML
    private void goToView4() {
        switchToView("view4.fxml");
    }

    @FXML
    private void goToView5() {
        switchToView("view5.fxml");
    }

    @FXML
    private void logout() {
        switchToView("login-view.fxml");
    }

    @FXML
    private void exitApplication() {
        System.exit(0);
    }


}
