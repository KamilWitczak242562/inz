package com.example.client.controller.resources;

import com.example.client.model.resource.Resource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class ResourceController {

    @FXML
    private GridPane resourceGrid;

    @FXML
    private Button addResourceButton;

    @FXML
    private Button goToSuppliersButton;

    public void initialize() {
        if ("ADMIN".equals(getRole())) {
            addResourceButton.setVisible(true);
            addResourceButton.setOnAction(e -> addResource());
        } else {
            addResourceButton.setVisible(false);
        }
        goToSuppliersButton.setOnAction(e -> switchToView("supplier-view.fxml"));
        loadResources();
    }

    @FXML
    private void refreshResources() {
        loadResources();
        showAlert("Resources refreshed successfully.");
    }

    private void loadResources() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/resources/resource/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            List<Resource> resources = mapper.convertValue(responseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Resource.class));

            resourceGrid.getChildren().clear();
            int col = 0;
            int row = 0;

            for (Resource resource : resources) {
                StackPane resourceTile = createResourceTile(resource);
                resourceGrid.add(resourceTile, col++, row);
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load resources: " + e.getMessage());
        }
    }

    private StackPane createResourceTile(Resource resource) {
        StackPane tile = new StackPane();
        tile.setPrefSize(250, 120);

        if (resource.getCurrentStock() < resource.getReorderLevel()) {
            tile.setStyle("-fx-background-color: #FF5252; -fx-border-color: #e8f5e9; -fx-border-radius: 10; -fx-background-radius: 10;");
        } else {
            tile.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 10; -fx-background-radius: 10;");
        }

        Label nameLabel = new Label("Name: " + resource.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label stockLabel = new Label("Stock: " + resource.getCurrentStock());
        stockLabel.setStyle("-fx-font-size: 12px;");

        Label unitLabel = new Label("Unit: " + resource.getUnit());
        unitLabel.setStyle("-fx-font-size: 12px;");

        VBox content = new VBox(10, nameLabel, stockLabel, unitLabel);
        content.setStyle("-fx-padding: 10;");
        tile.getChildren().add(content);

        tile.setOnMouseClicked(e -> showResourceDetails(resource));
        return tile;
    }

    private void addResource() {
        openResourceForm(new Resource(), false);
    }

    private void showResourceDetails(Resource resource) {
        openResourceForm(resource, true);
    }

    private void openResourceForm(Resource resource, boolean isEdit) {
        Stage stage = new Stage();
        stage.setTitle(isEdit ? "Resource Details" : "Add Resource");

        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        TextField nameField = new TextField(resource.getName());
        TextField descriptionField = new TextField(resource.getDescription());
        TextField stockField = new TextField(resource.getCurrentStock() != null ? resource.getCurrentStock().toString() : "");
        TextField unitField = new TextField(resource.getUnit());
        TextField reorderLevelField = new TextField(resource.getReorderLevel() != null ? resource.getReorderLevel().toString() : "");

        Button saveButton = new Button(isEdit ? "Update" : "Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                resource.setName(nameField.getText());
                resource.setDescription(descriptionField.getText());
                resource.setCurrentStock(Double.parseDouble(stockField.getText()));
                resource.setUnit(unitField.getText());
                resource.setReorderLevel(Double.parseDouble(reorderLevelField.getText()));

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = mapper.writeValueAsString(resource);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/resources/resource/" + (isEdit ? "update" : "new")))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                stage.close();
                loadResources();
            } catch (Exception ex) {
                showAlert("Failed to " + (isEdit ? "update" : "add") + " resource: " + ex.getMessage());
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this recipe?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteRecipe(resource, stage);
                }
            });
        });

        if (!"ADMIN".equals(getRole())) {
            saveButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        Button cancelButton = new Button("Close");
        cancelButton.setOnAction(e -> stage.close());

        formBox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionField,
                new Label("Stock:"), stockField,
                new Label("Unit:"), unitField,
                new Label("Reorder Level:"), reorderLevelField,
                saveButton, deleteButton, cancelButton
        );

        Scene scene = new Scene(formBox, 500, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void deleteRecipe(Resource resource, Stage stage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/resources/resource/" + resource.getResourceId()))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
            stage.close();
            loadResources();
        } catch (Exception ex) {
            showAlert("Failed to delete resource: " + ex.getMessage());
        }
    }

    @FXML
    private void goToMainView() {
        switchToView("main-view.fxml");
    }
}
