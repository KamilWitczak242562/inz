package com.example.client.controller.machine;

import com.example.client.model.machine.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

public class MachineController {

    @FXML
    private GridPane machineGrid;

    @FXML
    private Button addDryerButton;

    @FXML
    private Button addDyeingButton;

    public void initialize() {
        if ("ADMIN".equals(getRole())) {
            addDryerButton.setVisible(true);
            addDyeingButton.setVisible(true);
            addDryerButton.setOnAction(e -> addDryer());
            addDyeingButton.setOnAction(e -> addDyeing());
        } else {
            addDryerButton.setVisible(false);
            addDyeingButton.setVisible(false);
        }
        loadMachines();
    }

    @FXML
    private void refreshMachines() {
        loadMachines();
        showAlert("Machines refreshed successfully.");
    }

    private void loadMachines() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

            machineGrid.getChildren().clear();

            int dryerCol = 0;
            int dyeingCol = 0;

            for (Machine machine : dryers) {
                StackPane machineTile = createMachineTile(machine);
                if ("ERROR".equalsIgnoreCase(machine.getState())) {
                    machineTile.setStyle("-fx-background-color: #ffe6e6; -fx-border-color: red; -fx-border-radius: 10px;");
                } else {
                    machineTile.setStyle("-fx-background-color: #e6ffe6; -fx-border-color: green; -fx-border-radius: 10px;");
                }
                machineGrid.add(machineTile, dryerCol++, 0);
            }

            for (Machine machine : dyeings) {
                StackPane machineTile = createMachineTile(machine);
                if ("ERROR".equalsIgnoreCase(machine.getState())) {
                    machineTile.setStyle("-fx-background-color: #ffe6e6; -fx-border-color: red; -fx-border-radius: 10px;");
                } else {
                    machineTile.setStyle("-fx-background-color: #e6ffe6; -fx-border-color: green; -fx-border-radius: 10px;");
                }
                machineGrid.add(machineTile, dyeingCol++, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load machines: " + e.getMessage());
        }
    }



    private StackPane createMachineTile(Machine machine) {
        StackPane tile = new StackPane();
        tile.setPrefSize(200, 200);
        tile.setStyle("-fx-border-color: #4CAF50; -fx-background-color: #E8F5E9; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label nameLabel = new Label("Name: " + (machine.getName() != null ? machine.getName() : "N/A"));
        Label stateLabel = new Label("State: " + machine.getState());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        stateLabel.setStyle("-fx-font-size: 14px;");

        tile.setOnMouseClicked(e -> showMachineDetails(machine));

        VBox content = new VBox(10, nameLabel, stateLabel);
        content.setStyle("-fx-alignment: center;");
        tile.getChildren().add(content);

        return tile;
    }

    private void addDyeing() {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to add dyeing machines.");
            return;
        }

        Stage addStage = new Stage();
        addStage.setTitle("Add Dyeing Machine");

        VBox addBox = new VBox(10);
        addBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F5F5F5;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label stateLabel = new Label("State:");
        ComboBox<State> stateComboBox = new ComboBox<>();
        stateComboBox.getItems().setAll(State.values());

        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();

        Label chargeDiameterLabel = new Label("Charge Diameter:");
        TextField chargeDiameterField = new TextField();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                State state = stateComboBox.getValue();
                int capacity = Integer.parseInt(capacityField.getText());
                int chargeDiameter = Integer.parseInt(chargeDiameterField.getText());

                if (name.isEmpty() || state == null) {
                    showAlert("All fields are required.");
                    return;
                }

                Dyeing newDyeing = new Dyeing(null, state.name(), name, null, null, capacity, chargeDiameter);

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                String requestBody = mapper.writeValueAsString(newDyeing);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/machine/dyeing/new"))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                addStage.close();
                loadMachines();
            } catch (Exception ex) {
                showAlert("Failed to add dyeing machine: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> addStage.close());

        addBox.getChildren().addAll(
                nameLabel, nameField,
                stateLabel, stateComboBox,
                capacityLabel, capacityField,
                chargeDiameterLabel, chargeDiameterField,
                saveButton, cancelButton
        );

        Scene scene = new Scene(addBox, 350, 400);
        addStage.setScene(scene);
        addStage.show();
    }


    private void showMachineDetails(Machine machine) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Machine Details");

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FAFAFA;");

        Label nameLabel = new Label("Name: " + (machine.getName() != null ? machine.getName() : "N/A"));
        Label stateLabel = new Label("State: " + machine.getState());
        Label capacityLabel = new Label("Capacity: " + machine.getCapacity());
        Label specificInfoLabel = new Label();

        if (machine instanceof Dryer) {
            specificInfoLabel.setText("Dryer Type: " + ((Dryer) machine).getDryerType());
        } else if (machine instanceof Dyeing) {
            specificInfoLabel.setText("Charge Diameter: " + ((Dyeing) machine).getChargeDiameter());
        }

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateMachine(machine));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteMachine(machine, detailsStage));

        if (!"ADMIN".equals(getRole())) {
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsBox.getChildren().addAll(nameLabel, stateLabel, capacityLabel, specificInfoLabel, updateButton, deleteButton, closeButton);

        Scene scene = new Scene(detailsBox, 350, 450);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void updateMachine(Machine machine) {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to update machines.");
            return;
        }

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Machine");

        VBox updateBox = new VBox(10);
        updateBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F5F5F5;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(machine.getName());

        Label stateLabel = new Label("State:");
        ComboBox<State> stateComboBox = new ComboBox<>();
        stateComboBox.getItems().setAll(State.values());
        stateComboBox.setValue(State.valueOf(machine.getState()));

        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField(String.valueOf(machine.getCapacity()));

        Label specificLabel = new Label();
        TextField chargeDiameterField = null;
        ComboBox<DryerType> dryerTypeComboBox = null;

        if (machine instanceof Dryer) {
            specificLabel.setText("Dryer Type:");
            dryerTypeComboBox = new ComboBox<>();
            dryerTypeComboBox.getItems().setAll(DryerType.values());
            dryerTypeComboBox.setValue(DryerType.valueOf(((Dryer) machine).getDryerType()));
        } else if (machine instanceof Dyeing) {
            specificLabel.setText("Charge Diameter:");
            chargeDiameterField = new TextField(String.valueOf(((Dyeing) machine).getChargeDiameter()));
        }

        Button saveButton = new Button("Save");
        ComboBox<DryerType> finalDryerTypeComboBox = dryerTypeComboBox;
        TextField finalChargeDiameterField = chargeDiameterField;

        saveButton.setOnAction(e -> {
            try {
                String newName = nameField.getText().isEmpty() ? machine.getName() : nameField.getText();
                String newState = stateComboBox.getValue() == null ? machine.getState() : stateComboBox.getValue().name();
                int newCapacity = capacityField.getText().isEmpty() ? machine.getCapacity() : Integer.parseInt(capacityField.getText());

                machine.setName(newName);
                machine.setState(newState);
                machine.setCapacity(newCapacity);

                if (machine instanceof Dryer) {
                    DryerType newDryerType = finalDryerTypeComboBox.getValue();
                    String dryerType = newDryerType == null ? ((Dryer) machine).getDryerType() : newDryerType.name();
                    ((Dryer) machine).setDryerType(dryerType);
                } else if (machine instanceof Dyeing) {
                    int newChargeDiameter = finalChargeDiameterField.getText().isEmpty()
                            ? ((Dyeing) machine).getChargeDiameter()
                            : Integer.parseInt(finalChargeDiameterField.getText());
                    ((Dyeing) machine).setChargeDiameter(newChargeDiameter);
                }

                String apiEndpoint = machine instanceof Dryer ? "dryer/" : "dyeing/";
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                String requestBody = mapper.writeValueAsString(machine);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/machine/" + apiEndpoint + machine.getMachineId()))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                updateStage.close();
                loadMachines();
            } catch (Exception ex) {
                showAlert("Failed to update machine: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> updateStage.close());

        if (machine instanceof Dryer) {
            updateBox.getChildren().addAll(
                    nameLabel, nameField,
                    stateLabel, stateComboBox,
                    capacityLabel, capacityField,
                    specificLabel, dryerTypeComboBox,
                    saveButton, cancelButton
            );
        } else if (machine instanceof Dyeing) {
            updateBox.getChildren().addAll(
                    nameLabel, nameField,
                    stateLabel, stateComboBox,
                    capacityLabel, capacityField,
                    specificLabel, chargeDiameterField,
                    saveButton, cancelButton
            );
        }

        Scene scene = new Scene(updateBox, 350, 450);
        updateStage.setScene(scene);
        updateStage.show();
    }


    private void addDryer() {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to add dryers.");
            return;
        }

        Stage addStage = new Stage();
        addStage.setTitle("Add Dryer");

        VBox addBox = new VBox(10);
        addBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F5F5F5;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label stateLabel = new Label("State:");
        ComboBox<State> stateComboBox = new ComboBox<>();
        stateComboBox.getItems().setAll(State.values());

        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();

        Label dryerTypeLabel = new Label("Dryer Type:");
        ComboBox<DryerType> dryerTypeComboBox = new ComboBox<>();
        dryerTypeComboBox.getItems().setAll(DryerType.values());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                State state = stateComboBox.getValue();
                int capacity = Integer.parseInt(capacityField.getText());
                DryerType dryerType = dryerTypeComboBox.getValue();

                if (name.isEmpty() || state == null || dryerType == null) {
                    showAlert("All fields are required.");
                    return;
                }

                Dryer newDryer = new Dryer(null, state.name(), name, null, null, capacity, dryerType.name());

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                String requestBody = mapper.writeValueAsString(newDryer);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/machine/dryer/new"))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                addStage.close();
                loadMachines();
            } catch (Exception ex) {
                showAlert("Failed to add dryer: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> addStage.close());

        addBox.getChildren().addAll(
                nameLabel, nameField,
                stateLabel, stateComboBox,
                capacityLabel, capacityField,
                dryerTypeLabel, dryerTypeComboBox,
                saveButton, cancelButton
        );

        Scene scene = new Scene(addBox, 350, 400);
        addStage.setScene(scene);
        addStage.show();
    }

    private void deleteMachine(Machine machine, Stage detailsStage) {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to delete machines.");
            return;
        }

        try {
            String apiEndpoint = machine instanceof Dryer ? "dryer/" : "dyeing/";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/" + apiEndpoint + machine.getMachineId()))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            detailsStage.close();
            loadMachines();
        } catch (Exception e) {
            showAlert("Failed to delete machine: " + e.getMessage());
        }
    }

    @FXML
    private void goToMainView() {
        switchToView("main-view.fxml");
    }
}
