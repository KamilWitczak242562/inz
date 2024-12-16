package com.example.client.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import static com.example.client.utils.Utils.*;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("All fields are required!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("New passwords do not match!");
            return;
        }

        try {
            Map<String, String> requestData = Map.of(
                    "email", getEmail(),
                    "oldPassword", oldPassword,
                    "newPassword", newPassword
            );

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(requestData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/auth/user/change-password"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("Password changed successfully!");
                handleCancel();
            } else if (response.statusCode() == 400) {
                showAlert("Invalid input. Please check your data.");
            } else if (response.statusCode() == 401) {
                showAlert("Wrong old password.");
            } else if (response.statusCode() == 404) {
                showAlert("Wrong old password.");
            } else {
                showAlert("Failed to change password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        switchToView("main-view.fxml");
    }
}
