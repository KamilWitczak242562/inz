package com.example.client.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Utils {
    private static String authToken;

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        authToken = authToken;
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
