package com.example.client.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class CreateUserController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    public void initialize() {
        roleComboBox.getItems().addAll("ADMIN", "USER");
    }

    @FXML
    private void handleCreateUser() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("All fields are required!");
            return;
        }

        try {
            Map<String, String> user = Map.of(
                    "firstName", firstName,
                    "lastName", lastName,
                    "email", email,
                    "password", password,
                    "role", role
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String requestBody = mapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/auth/user/register"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("User created successfully!");
                handleCancel();
            } else {
                showAlert("Failed to create user!");
            }
        } catch (Exception e) {
            showAlert("Error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        switchToView("main-view.fxml");
    }
}
