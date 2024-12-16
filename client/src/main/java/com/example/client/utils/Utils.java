package com.example.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Utils {
    private static String authToken;
    private static String role;
    private static String email;
    private static final String CLIENT_SECRET = "2be4986e4bf057b65a0bb9fad7b0df44";

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Utils.email = email;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        Utils.role = role;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        Utils.authToken = authToken;
    }

    public static void showAlert(String message) {
        Platform.runLater(() -> {
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(window -> window instanceof Stage && ((Stage) window).isShowing())
                    .findFirst()
                    .orElse(null);

            if (stage == null) return;

            Popup popup = new Popup();

            Label notification = new Label(message);
            notification.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

            StackPane container = new StackPane(notification);
            container.setStyle("-fx-padding: 10px;");
            container.setAlignment(Pos.TOP_CENTER);

            popup.getContent().add(container);
            popup.setAutoHide(true);
            popup.show(stage);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), container);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), container);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(2));

            fadeIn.setOnFinished(e -> fadeOut.play());
            fadeOut.setOnFinished(e -> popup.hide());

            fadeIn.play();
        });
    }

    public static void switchToView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource(fxmlFile));
            Stage stage = (Stage) Stage.getWindows().stream().filter(window -> window.isShowing()).findFirst().orElse(null);
            if (stage != null) {
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("DyeFlow");

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);


                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
