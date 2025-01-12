package com.example.client.controller.resources;

import com.example.client.model.resource.Resource;
import com.example.client.model.resource.Supplier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class SupplierController {

    @FXML
    private GridPane supplierGrid;

    @FXML
    private Button addSupplierButton;

    @FXML
    private Button goToResourcesButton;

    @FXML
    private Button goToMainViewButton;

    public void initialize() {
        if ("ADMIN".equals(getRole())) {
            addSupplierButton.setVisible(true);
            addSupplierButton.setOnAction(e -> addSupplier());
        } else {
            addSupplierButton.setVisible(false);
        }
        goToResourcesButton.setOnAction(e -> switchToView("resource-view.fxml"));
        goToMainViewButton.setOnAction(e -> switchToView("main-view.fxml"));
        loadSuppliers();
    }

    @FXML
    private void refreshSuppliers() {
        loadSuppliers();
        showAlert("Suppliers refreshed successfully.");
    }

    private void loadAvailableResources(ListView<Resource> listView) {
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

            listView.getItems().setAll(resources);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load resources: " + e.getMessage());
        }
    }


    private void loadSuppliers() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/resources/supplier/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            List<Supplier> suppliers = mapper.convertValue(responseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Supplier.class));

            supplierGrid.getChildren().clear();
            int col = 0;
            int row = 0;

            for (Supplier supplier : suppliers) {
                StackPane supplierTile = createSupplierTile(supplier);
                supplierGrid.add(supplierTile, col++, row);
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load suppliers: " + e.getMessage());
        }
    }

    private StackPane createSupplierTile(Supplier supplier) {
        StackPane tile = new StackPane();
        tile.setPrefSize(250, 150);
        tile.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label nameLabel = new Label("Name: " + supplier.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label contactLabel = new Label("Contact: " + supplier.getContactInfo());
        contactLabel.setStyle("-fx-font-size: 12px;");

        Label addressLabel = new Label("Address: " + supplier.getAddress());
        addressLabel.setStyle("-fx-font-size: 12px;");

        VBox content = new VBox(10, nameLabel, contactLabel, addressLabel);
        content.setStyle("-fx-padding: 10;");
        tile.getChildren().add(content);

        tile.setOnMouseClicked(e -> showSupplierDetails(supplier));
        return tile;
    }

    private void showSupplierDetails(Supplier supplier) {
        Stage stage = new Stage();
        stage.setTitle("Supplier Details");

        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label nameLabel = new Label("Name: " + supplier.getName());
        Label contactLabel = new Label("Contact Info: " + supplier.getContactInfo());
        Label addressLabel = new Label("Address: " + supplier.getAddress());

        VBox resourcesBox = new VBox(10);
        resourcesBox.setStyle("-fx-padding: 10;");
        Label resourcesLabel = new Label("Resources:");
        resourcesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        if (supplier.getResources() != null && !supplier.getResources().isEmpty()) {
            for (Resource resource : supplier.getResources()) {
                Label resourceLabel = new Label(resource.getName() + " - Stock: " + resource.getCurrentStock());
                resourcesBox.getChildren().add(resourceLabel);
            }
        } else {
            resourcesBox.getChildren().add(new Label("No resources available."));
        }

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateButton.setOnAction(e -> openSupplierForm(supplier, true));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> showDeleteConfirmation(supplier, stage));

        if (!getRole().equals("ADMIN")) {
            deleteButton.setVisible(false);
            updateButton.setVisible(false);
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        formBox.getChildren().addAll(
                nameLabel, contactLabel, addressLabel,
                resourcesLabel, resourcesBox, updateButton, deleteButton, closeButton
        );

        Scene scene = new Scene(formBox, 500, 450);
        stage.setScene(scene);
        stage.show();
    }


    private void addSupplier() {
        openSupplierForm(new Supplier(), false);
    }

    private void openSupplierForm(Supplier supplier, boolean isEdit) {
        Stage stage = new Stage();
        stage.setTitle(isEdit ? "Edit Supplier" : "Add Supplier");

        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        TextField nameField = new TextField(supplier.getName());
        TextField contactField = new TextField(supplier.getContactInfo());
        TextField addressField = new TextField(supplier.getAddress());

        ListView<Resource> resourceListView = new ListView<>();
        loadAvailableResources(resourceListView);
        resourceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button saveButton = new Button(isEdit ? "Update" : "Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                supplier.setName(nameField.getText());
                supplier.setContactInfo(contactField.getText());
                supplier.setAddress(addressField.getText());
                supplier.setResources(new ArrayList<>(resourceListView.getSelectionModel().getSelectedItems()));

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = mapper.writeValueAsString(supplier);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/resources/supplier/" + (isEdit ? supplier.getSupplierId() : "new")))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                stage.close();
                loadSuppliers();
            } catch (Exception ex) {
                showAlert("Failed to " + (isEdit ? "update" : "add") + " supplier: " + ex.getMessage());
            }
        });
        saveButton.setVisible("ADMIN".equals(getRole()));

        Button cancelButton = new Button("Close");
        cancelButton.setOnAction(e -> stage.close());

        formBox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Contact Info:"), contactField,
                new Label("Address:"), addressField,
                new Label("Select Resources:"), resourceListView,
                saveButton, cancelButton
        );

        Scene scene = new Scene(formBox, 600, 600);
        stage.setScene(scene);
        stage.show();
    }


    private void showDeleteConfirmation(Supplier supplier, Stage stage) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this supplier?");
        confirmationAlert.setContentText("Supplier: " + supplier.getName());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest deleteRequest = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/v1/resources/supplier/" + supplier.getSupplierId()))
                            .header("Authorization", "Bearer " + getAuthToken())
                            .header("Client", getClientSecret())
                            .DELETE()
                            .build();

                    client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                    stage.close();
                    loadSuppliers();
                } catch (Exception ex) {
                    showAlert("Failed to delete supplier: " + ex.getMessage());
                }
            }
        });
    }
}
