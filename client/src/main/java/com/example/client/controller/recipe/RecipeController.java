package com.example.client.controller.recipe;

import com.example.client.model.recipe.Recipe;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class RecipeController {

    @FXML
    private GridPane recipeGrid;

    @FXML
    private Button addRecipeButton;

    public void initialize() {
        if ("ADMIN".equals(getRole())) {
            addRecipeButton.setVisible(true);
            addRecipeButton.setOnAction(e -> openRecipeForm(new Recipe(), false));
        } else {
            addRecipeButton.setVisible(false);
        }
        loadRecipes();
    }

    @FXML
    private void refreshRecipes() {
        loadRecipes();
        showAlert("Recipes refreshed successfully.");
    }

    private void loadRecipes() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/recipe/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            List<Recipe> recipes = mapper.convertValue(
                    responseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Recipe.class));

            recipeGrid.getChildren().clear();
            int col = 0;
            int row = 0;

            for (Recipe recipe : recipes) {
                StackPane recipeTile = createRecipeTile(recipe);
                recipeGrid.add(recipeTile, col++, row);
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load recipes: " + e.getMessage());
        }
    }

    private StackPane createRecipeTile(Recipe recipe) {
        StackPane tile = new StackPane();
        tile.setPrefSize(250, 120);

        tile.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label nameLabel = new Label("Name: " + recipe.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("Description: " + recipe.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 12px;");

        VBox content = new VBox(10, nameLabel, descriptionLabel);
        content.setStyle("-fx-padding: 10;");
        tile.getChildren().add(content);

        tile.setOnMouseClicked(e -> showRecipeDetails(recipe));
        return tile;
    }

    private void showRecipeDetails(Recipe recipe) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Recipe Details");

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FAFAFA;");

        Label nameLabel = new Label("Name: " + recipe.getName());
        Label descriptionLabel = new Label("Description: " + recipe.getDescription());

        VBox resourceList = new VBox(10);
        resourceList.setStyle("-fx-padding: 10;");

        recipe.getResourcesQuantities().forEach((resourceId, quantity) -> {
            Map<String, Object> resourceDetails = fetchResourceById(resourceId);
            Label resourceLabel = new Label(resourceDetails.get("name") + " - Quantity: " + quantity);
            resourceList.getChildren().add(resourceLabel);
        });

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> openRecipeForm(recipe, true));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this recipe?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteRecipe(recipe, detailsStage);
                }
            });
        });


        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsBox.getChildren().addAll(nameLabel, descriptionLabel, resourceList, updateButton, deleteButton, closeButton);

        Scene scene = new Scene(detailsBox, 500, 400);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private Map<String, Object> fetchResourceById(Long resourceId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/resources/resource/" + resourceId))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                showAlert("Failed to load resource: Invalid response status " + response.statusCode());
                return Map.of();
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);

            if (responseMap == null || !responseMap.containsKey("response")) {
                showAlert("Resource not found or missing response field.");
                return Map.of();
            }

            Map<String, Object> resourceData = (Map<String, Object>) responseMap.get("response");
            if (resourceData == null || resourceData.isEmpty()) {
                showAlert("Resource data is empty.");
                return Map.of();
            }

            return resourceData;
        } catch (Exception e) {
            showAlert("Failed to load resource: " + e.getMessage());
            return Map.of();
        }
    }


    private void deleteRecipe(Recipe recipe, Stage detailsStage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/recipe/" + recipe.getId()))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            detailsStage.close();
            loadRecipes();
        } catch (Exception e) {
            showAlert("Failed to delete recipe: " + e.getMessage());
        }
    }

    private void openRecipeForm(Recipe recipe, boolean isEdit) {
        Stage stage = new Stage();
        stage.setTitle(isEdit ? "Recipe Details" : "Add Recipe");

        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        TextField nameField = new TextField(recipe.getName());
        TextArea descriptionArea = new TextArea(recipe.getDescription());

        Label resourceLabel = new Label("Resources:");

        Map<Long, Double> selectedResources = new HashMap<>(recipe.getResourcesQuantities() != null ? recipe.getResourcesQuantities() : new HashMap<>());
        recipe.setResourcesQuantities(selectedResources);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        VBox resourceSelectionBox = new VBox(10);
        scrollPane.setContent(resourceSelectionBox);

        List<Map<String, Object>> allResources = fetchAllResources();
        if (allResources == null || allResources.isEmpty()) {
            showAlert("No resources available.");
        } else {
            allResources.forEach(resource -> {
                Long resourceId = ((Number) resource.getOrDefault("resourceId", -1L)).longValue();
                String resourceName = String.valueOf(resource.getOrDefault("name", "Unknown"));

                if (resourceId == -1 || "Unknown".equals(resourceName)) {
                    return;
                }

                HBox resourceRow = new HBox(10);
                Label resourceNameLabel = new Label(resourceName);
                TextField quantityField = new TextField();
                quantityField.setPromptText("Quantity");

                Double currentQuantity = selectedResources.get(resourceId);
                if (currentQuantity != null) {
                    quantityField.setText(String.valueOf(currentQuantity));
                }

                resourceRow.getChildren().addAll(resourceNameLabel, quantityField);
                resourceSelectionBox.getChildren().add(resourceRow);

                quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
                    updateSelectedResources(selectedResources, resourceId, newValue);
                });
            });
        }

        Button saveButton = new Button(isEdit ? "Update" : "Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveRecipe(recipe, selectedResources, isEdit, nameField, descriptionArea, stage));

        Button cancelButton = new Button("Close");
        cancelButton.setOnAction(e -> stage.close());

        formBox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionArea,
                resourceLabel, scrollPane,
                saveButton, cancelButton
        );

        Scene scene = new Scene(formBox, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void updateSelectedResources(Map<Long, Double> selectedResources, Long resourceId, String newValue) {
        try {
            double quantity = Double.parseDouble(newValue);
            if (quantity > 0) {
                selectedResources.put(resourceId, quantity);
            } else {
                selectedResources.remove(resourceId);
            }
        } catch (NumberFormatException ex) {
            selectedResources.remove(resourceId);
        }
    }

    private void saveRecipe(Recipe recipe, Map<Long, Double> selectedResources, boolean isEdit, TextField nameField, TextArea descriptionArea, Stage stage) {
        try {
            recipe.setName(nameField.getText());
            recipe.setDescription(descriptionArea.getText());
            recipe.setResourcesQuantities(new HashMap<>(selectedResources));

            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String requestBody = mapper.writeValueAsString(recipe);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/recipe/" + (isEdit ? recipe.getId() : "new")))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            stage.close();
            loadRecipes();
        } catch (Exception ex) {
            showAlert("Failed to " + (isEdit ? "update" : "add") + " recipe: " + ex.getMessage());
        }
    }

    private List<Map<String, Object>> fetchAllResources() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/resources/resource/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);

            if (!responseMap.containsKey("response") || responseMap.get("response") == null) {
                showAlert("Resource list is empty.");
                return List.of();
            }

            return (List<Map<String, Object>>) responseMap.get("response");
        } catch (Exception e) {
            showAlert("Failed to load resources: " + e.getMessage());
            return List.of();
        }
    }




    @FXML
    private void goToMainView() {
        switchToView("main-view.fxml");
    }
}
