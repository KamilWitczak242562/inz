package com.example.client.controller;

import com.example.client.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class LoginController {
    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    private String logIn(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be empty.");
        }

        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/auth/user/login"))
                .header("Client", getClientSecret())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                Utils.setAuthToken((String) responseBody.get("token"));
                Utils.setRole((String) responseBody.get("role"));
                Utils.setEmail((String) responseBody.get("email"));
                return "success";
            } else {
                throw new IllegalArgumentException("Failed to log in: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during login: " + e.getMessage());
        }
    }

    @FXML
    protected void logIn() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            String result = logIn(login, password);

            if ("success".equals(result)) {
                switchToView("main-view.fxml");
            }
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }
}
