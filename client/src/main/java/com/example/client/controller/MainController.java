package com.example.client.controller;

import com.example.client.model.machine.Dryer;
import com.example.client.model.machine.Dyeing;
import com.example.client.model.resource.Resource;
import com.example.client.utils.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class MainController {

    @FXML
    private Button machineViewButton;

    @FXML
    private HBox buttonContainer;

    @FXML
    private Button resourceViewButton;

    @FXML
    private MenuBar menuBar;

    public void initialize() {
        checkMachineStatus();
        checkResourceStatus();
        addRoleBasedControls();
        addCommonControls();
    }

    @FXML
    private void goToUserManagement() {
        switchToView("user-management-view.fxml");
    }

    @FXML
    private void changePassword() {
        switchToView("change-password-view.fxml");
    }

    @FXML
    private void goToView1() {
        switchToView("program-view.fxml");
    }

    @FXML
    private void goToView2() {
        switchToView("recipe-view.fxml");
    }

    @FXML
    private void goToView3() {
        switchToView("planning-view.fxml");
    }

    @FXML
    private void goToView4() {
        switchToView("resource-view.fxml");
    }

    @FXML
    private void goToView5() {
        switchToView("machine-view.fxml");
    }

    @FXML
    private void logout() {
        Utils.setAuthToken(null);
        Utils.setEmail(null);
        Utils.setRole(null);
        switchToView("login-view.fxml");
    }

    @FXML
    private void exitApplication() {
        System.exit(0);
    }

    private void addRoleBasedControls() {
        if ("ADMIN".equalsIgnoreCase(getRole())) {
            Menu userMenu = new Menu("User Management");

            MenuItem manageUsers = new MenuItem("Manage Users");
            manageUsers.setOnAction(event -> goToUserManagement());

            MenuItem createUser = new MenuItem("Create User");
            createUser.setOnAction(event -> switchToView("create-user-view.fxml"));

            userMenu.getItems().addAll(manageUsers, createUser);
            menuBar.getMenus().add(userMenu);
        }
    }

    private void addCommonControls() {
        Menu accountMenu = new Menu("Account");
        MenuItem changePassword = new MenuItem("Change Password");
        changePassword.setOnAction(event -> changePassword());
        accountMenu.getItems().add(changePassword);
        menuBar.getMenus().add(accountMenu);
    }

    public void checkResourceStatus() {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
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

            boolean hasLowStock = resources.stream().anyMatch(resource -> resource.getCurrentStock() < resource.getReorderLevel());

            Platform.runLater(() -> {
                if (resourceViewButton != null) {
                    VBox buttonContent = new VBox(5);
                    buttonContent.setStyle("-fx-alignment: center;");

                    Text buttonText = new Text("Resource View");
                    buttonText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #424242;");

                    Text warningText = new Text();
                    warningText.setStyle("-fx-font-size: 14px; -fx-fill: red;");

                    if (hasLowStock) {
                        warningText.setText("Low stock detected");
                        resourceViewButton.setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: black;");
                    } else {
                        warningText.setText("");
                        resourceViewButton.setStyle("-fx-background-color: #e6ffe6; -fx-text-fill: black;");
                    }

                    buttonContent.getChildren().setAll(buttonText, warningText);
                    resourceViewButton.setGraphic(buttonContent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkMachineStatus() {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            HttpRequest dryerRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/dryer/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();
            HttpResponse<String> dryerResponse = client.send(dryerRequest, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> dryerResponseMap = mapper.readValue(dryerResponse.body(), Map.class);
            List<Dryer> dryers = mapper.convertValue(dryerResponseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Dryer.class));

            HttpRequest dyeingRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/dyeing/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();
            HttpResponse<String> dyeingResponse = client.send(dyeingRequest, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> dyeingResponseMap = mapper.readValue(dyeingResponse.body(), Map.class);
            List<Dyeing> dyeings = mapper.convertValue(dyeingResponseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Dyeing.class));

            boolean isErrorState = dryers.stream().anyMatch(dryer -> "ERROR".equalsIgnoreCase(dryer.getState()))
                    || dyeings.stream().anyMatch(dyeing -> "ERROR".equalsIgnoreCase(dyeing.getState()));

            Platform.runLater(() -> {
                if (machineViewButton != null) {
                    VBox buttonContent = new VBox(5);
                    buttonContent.setStyle("-fx-alignment: center;");

                    Text buttonText = new Text("Machine View");
                    buttonText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #424242;");

                    Text errorText = new Text();
                    errorText.setStyle("-fx-font-size: 14px; -fx-fill: red;");

                    if (isErrorState) {
                        errorText.setText("Error detected");
                        machineViewButton.setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: black;");
                    } else {
                        errorText.setText("");
                        machineViewButton.setStyle("-fx-background-color: #e6ffe6; -fx-text-fill: black;");
                    }

                    buttonContent.getChildren().setAll(buttonText, errorText);
                    machineViewButton.setGraphic(buttonContent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
