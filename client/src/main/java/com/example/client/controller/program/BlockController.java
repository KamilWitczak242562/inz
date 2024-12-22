package com.example.client.controller.program;

import com.example.client.model.program.Block;
import com.example.client.model.program.MainTank;
import com.example.client.model.program.Pump;
import com.example.client.model.program.SecondaryTank;
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

public class BlockController {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    @FXML
    private GridPane blockGrid;
    @FXML
    private Button addBlockButton;
    @FXML
    private Button refreshButton;

    @FXML
    public void initialize() {
        refreshButton.setOnAction(e -> loadBlocks());
        addBlockButton.setOnAction(e -> handleAddBlockButton());
        if (!"ADMIN".equals(getRole())) {
            addBlockButton.setVisible(false);
        }
        loadBlocks();
    }

    private void loadBlocks() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/blocks/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() != null && !response.body().isEmpty()) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                List<Block> blocks = objectMapper.convertValue(
                        responseMap.get("response"),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Block.class)
                );

                blockGrid.getChildren().clear();

                int rowIndex = 0;
                int colIndex = 0;

                for (Block block : blocks) {
                    StackPane blockTile = createBlockTile(block, getBlockType(block));
                    blockGrid.add(blockTile, colIndex++, rowIndex);
                    if (colIndex == 3) {
                        colIndex = 0;
                        rowIndex++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnToMainView() {
        switchToView("main-view.fxml");
    }

    @FXML
    private void returnToProgramView() {
        switchToView("program-view.fxml");
    }

    @FXML
    private void refresh() {
        loadBlocks();
    }

    private StackPane createBlockTile(Block block, String blockType) {
        StackPane tile = new StackPane();
        tile.setPrefSize(200, 100);
        tile.setStyle("-fx-border-color: #FF9800; -fx-background-color: #FFF3E0; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox content = new VBox();
        content.setStyle("-fx-alignment: center; -fx-spacing: 10;");
        content.getChildren().addAll(
                new Label("Block ID: " + block.getBlockId()),
                new Label("Type: " + blockType)
        );

        tile.getChildren().add(content);
        tile.setOnMouseClicked(e -> showBlockDetails(block, blockType));

        return tile;
    }

    private void showBlockDetails(Block block, String blockType) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Block Details: ID " + block.getBlockId());

        VBox detailsBox = new VBox(15);
        detailsBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Block Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        List<Label> blockDetails = new ArrayList<>();
        blockDetails.add(new Label("Block ID: " + block.getBlockId()));
        blockDetails.add(new Label("Type: " + blockType));

        if (block instanceof MainTank mainTank) {
            blockDetails.add(new Label("Fill Level: " + mainTank.getFillLevel()));
            blockDetails.add(new Label("Hot Water: " + (mainTank.getIsHotWater() ? "Yes" : "No")));
            blockDetails.add(new Label("Target Temperature: " + mainTank.getTargetTemperature() + "°C"));
            blockDetails.add(new Label("Temperature Increase Rate: " + mainTank.getTemperatureIncreaseRate() + "°C/min"));
            blockDetails.add(new Label("Hold Temperature Time: " + mainTank.getHoldTemperatureTime() + " mins"));
            blockDetails.add(new Label("Drain Active: " + (mainTank.getIsDrainActive() ? "Yes" : "No")));
        } else if (block instanceof SecondaryTank secondaryTank) {
            blockDetails.add(new Label("Fill Level: " + secondaryTank.getFillLevel()));
            blockDetails.add(new Label("Hot Water: " + (secondaryTank.getIsHotWater() ? "Yes" : "No")));
            blockDetails.add(new Label("Target Temperature: " + secondaryTank.getTargetTemperature() + "°C"));
            blockDetails.add(new Label("Temperature Increase Rate: " + secondaryTank.getTemperatureIncreaseRate() + "°C/min"));
            blockDetails.add(new Label("Hold Temperature Time: " + secondaryTank.getHoldTemperatureTime() + " mins"));
            blockDetails.add(new Label("Drain Active: " + (secondaryTank.getIsDrainActive() ? "Yes" : "No")));
            blockDetails.add(new Label("Mixer Active: " + (secondaryTank.getIsMixerActive() ? "Yes" : "No")));
            blockDetails.add(new Label("Chemical Dose: " + secondaryTank.getChemicalDose() + " L"));
            blockDetails.add(new Label("Dye Dose: " + secondaryTank.getDyeDose() + " L"));
        } else if (block instanceof Pump pump) {
            blockDetails.add(new Label("RPM: " + pump.getRpm()));
            blockDetails.add(new Label("Circulation Time In-Out: " + pump.getCirculationTimeInOut() + " secs"));
            blockDetails.add(new Label("Circulation Time Out-In: " + pump.getCirculationTimeOutIn() + " secs"));
        }

        for (Label label : blockDetails) {
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        }

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");
        closeButton.setOnAction(e -> detailsStage.close());

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        updateButton.setOnAction(e -> showUpdateBlockForm(block, detailsStage));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this block?");
            confirmationAlert.setContentText("This action cannot be undone.");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteBlock(block, detailsStage);
                }
            });
        });

        if (!"ADMIN".equals(getRole())) {
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        detailsBox.getChildren().addAll(title);
        detailsBox.getChildren().addAll(blockDetails);
        detailsBox.getChildren().add(updateButton);
        detailsBox.getChildren().add(deleteButton);
        detailsBox.getChildren().add(closeButton);

        Scene scene = new Scene(detailsBox, 450, 550);
        detailsStage.setScene(scene);
        detailsStage.show();
    }


    private void showUpdateBlockForm(Block block, Stage parentStage) {
        Stage updateStage = new Stage();
        updateStage.setTitle("Update Block: ID " + block.getBlockId());

        VBox updateBox = new VBox(15);
        updateBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Update Block Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        List<TextField> fieldInputs = new ArrayList<>();
        if (block instanceof MainTank mainTank) {
            fieldInputs.add(new TextField(String.valueOf(mainTank.getFillLevel())));
            fieldInputs.add(new TextField(String.valueOf(mainTank.getIsHotWater())));
            fieldInputs.add(new TextField(String.valueOf(mainTank.getTargetTemperature())));
            fieldInputs.add(new TextField(String.valueOf(mainTank.getTemperatureIncreaseRate())));
            fieldInputs.add(new TextField(String.valueOf(mainTank.getHoldTemperatureTime())));
            fieldInputs.add(new TextField(String.valueOf(mainTank.getIsDrainActive())));
        } else if (block instanceof SecondaryTank secondaryTank) {
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getFillLevel())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getIsHotWater())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getTargetTemperature())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getTemperatureIncreaseRate())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getHoldTemperatureTime())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getIsDrainActive())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getIsMixerActive())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getChemicalDose())));
            fieldInputs.add(new TextField(String.valueOf(secondaryTank.getDyeDose())));
        } else if (block instanceof Pump pump) {
            fieldInputs.add(new TextField(String.valueOf(pump.getRpm())));
            fieldInputs.add(new TextField(String.valueOf(pump.getCirculationTimeInOut())));
            fieldInputs.add(new TextField(String.valueOf(pump.getCirculationTimeOutIn())));
        }

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            updateStage.close();
            parentStage.close();
            refresh();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> updateStage.close());

        updateBox.getChildren().addAll(title);
        updateBox.getChildren().addAll(fieldInputs);
        updateBox.getChildren().add(saveButton);
        updateBox.getChildren().add(cancelButton);

        Scene scene = new Scene(updateBox, 450, 550);
        updateStage.setScene(scene);
        updateStage.show();
    }


    private void deleteBlock(Block block, Stage detailsStage) {
        try {
            String url = "http://localhost:8080/api/v1/recipes/blocks/" + block.getBlockId();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                showAlert("Block deleted successfully!");
                detailsStage.close();
                loadBlocks();
            } else {
                showAlert("Failed to delete block. HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            showAlert("Failed to delete block: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleAddBlockButton() {
        Stage addBlockStage = new Stage();
        addBlockStage.setTitle("Add New Block");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FFFFFF;");

        ComboBox<String> blockTypeComboBox = new ComboBox<>();
        blockTypeComboBox.getItems().addAll("MainTank", "SecondaryTank", "Pump");
        blockTypeComboBox.setPromptText("Select Block Type");

        List<TextField> fieldInputs = new ArrayList<>();
        blockTypeComboBox.setOnAction(e -> {
            formBox.getChildren().removeIf(node -> node instanceof TextField);
            fieldInputs.clear();

            String selectedType = blockTypeComboBox.getValue();
            if ("MainTank".equals(selectedType)) {
                TextField fillLevelField = new TextField();
                fillLevelField.setPromptText("Fill Level");
                fieldInputs.add(fillLevelField);

                TextField hotWaterField = new TextField();
                hotWaterField.setPromptText("Hot Water (true/false)");
                fieldInputs.add(hotWaterField);

                TextField targetTempField = new TextField();
                targetTempField.setPromptText("Target Temperature");
                fieldInputs.add(targetTempField);

                TextField tempIncreaseRateField = new TextField();
                tempIncreaseRateField.setPromptText("Temperature Increase Rate");
                fieldInputs.add(tempIncreaseRateField);

                TextField holdTempTimeField = new TextField();
                holdTempTimeField.setPromptText("Hold Temperature Time");
                fieldInputs.add(holdTempTimeField);

                TextField drainActiveField = new TextField();
                drainActiveField.setPromptText("Drain Active (true/false)");
                fieldInputs.add(drainActiveField);
            } else if ("SecondaryTank".equals(selectedType)) {
                TextField fillLevelField = new TextField();
                fillLevelField.setPromptText("Fill Level");
                fieldInputs.add(fillLevelField);

                TextField hotWaterField = new TextField();
                hotWaterField.setPromptText("Hot Water (true/false)");
                fieldInputs.add(hotWaterField);

                TextField targetTempField = new TextField();
                targetTempField.setPromptText("Target Temperature");
                fieldInputs.add(targetTempField);

                TextField tempIncreaseRateField = new TextField();
                tempIncreaseRateField.setPromptText("Temperature Increase Rate");
                fieldInputs.add(tempIncreaseRateField);

                TextField holdTempTimeField = new TextField();
                holdTempTimeField.setPromptText("Hold Temperature Time");
                fieldInputs.add(holdTempTimeField);

                TextField drainActiveField = new TextField();
                drainActiveField.setPromptText("Drain Active (true/false)");
                fieldInputs.add(drainActiveField);

                TextField mixerActiveField = new TextField();
                mixerActiveField.setPromptText("Mixer Active (true/false)");
                fieldInputs.add(mixerActiveField);

                TextField chemicalDoseField = new TextField();
                chemicalDoseField.setPromptText("Chemical Dose");
                fieldInputs.add(chemicalDoseField);

                TextField dyeDoseField = new TextField();
                dyeDoseField.setPromptText("Dye Dose");
                fieldInputs.add(dyeDoseField);
            } else if ("Pump".equals(selectedType)) {
                TextField rpmField = new TextField();
                rpmField.setPromptText("RPM");
                fieldInputs.add(rpmField);

                TextField circTimeInOutField = new TextField();
                circTimeInOutField.setPromptText("Circulation Time In-Out");
                fieldInputs.add(circTimeInOutField);

                TextField circTimeOutInField = new TextField();
                circTimeOutInField.setPromptText("Circulation Time Out-In");
                fieldInputs.add(circTimeOutInField);
            }

            formBox.getChildren().addAll(fieldInputs);
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                String selectedType = blockTypeComboBox.getValue();
                if (selectedType == null || selectedType.isEmpty()) {
                    showAlert("Please select a block type.");
                    return;
                }

                Block block;
                if ("MainTank".equals(selectedType)) {
                    MainTank mainTank = new MainTank();
                    mainTank.setFillLevel(Double.parseDouble(fieldInputs.get(0).getText()));
                    mainTank.setIsHotWater(Boolean.parseBoolean(fieldInputs.get(1).getText()));
                    mainTank.setTargetTemperature(Double.parseDouble(fieldInputs.get(2).getText()));
                    mainTank.setTemperatureIncreaseRate(Double.parseDouble(fieldInputs.get(3).getText()));
                    mainTank.setHoldTemperatureTime(Integer.parseInt(fieldInputs.get(4).getText()));
                    mainTank.setIsDrainActive(Boolean.parseBoolean(fieldInputs.get(5).getText()));
                    block = mainTank;
                } else if ("SecondaryTank".equals(selectedType)) {
                    SecondaryTank secondaryTank = new SecondaryTank();
                    secondaryTank.setFillLevel(Double.parseDouble(fieldInputs.get(0).getText()));
                    secondaryTank.setIsHotWater(Boolean.parseBoolean(fieldInputs.get(1).getText()));
                    secondaryTank.setTargetTemperature(Double.parseDouble(fieldInputs.get(2).getText()));
                    secondaryTank.setTemperatureIncreaseRate(Double.parseDouble(fieldInputs.get(3).getText()));
                    secondaryTank.setHoldTemperatureTime(Integer.parseInt(fieldInputs.get(4).getText()));
                    secondaryTank.setIsDrainActive(Boolean.parseBoolean(fieldInputs.get(5).getText()));
                    secondaryTank.setIsMixerActive(Boolean.parseBoolean(fieldInputs.get(6).getText()));
                    secondaryTank.setChemicalDose(Double.parseDouble(fieldInputs.get(7).getText()));
                    secondaryTank.setDyeDose(Double.parseDouble(fieldInputs.get(8).getText()));
                    block = secondaryTank;
                } else if ("Pump".equals(selectedType)) {
                    Pump pump = new Pump();
                    pump.setRpm(Integer.parseInt(fieldInputs.get(0).getText()));
                    pump.setCirculationTimeInOut(Integer.parseInt(fieldInputs.get(1).getText()));
                    pump.setCirculationTimeOutIn(Integer.parseInt(fieldInputs.get(2).getText()));
                    block = pump;
                } else {
                    throw new IllegalArgumentException("Unknown block type selected");
                }

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = mapper.writeValueAsString(block);


                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/recipes/blocks/new"))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 201) {
                    showAlert("Block added successfully.");
                    addBlockStage.close();
                    loadBlocks();
                } else {
                    showAlert("Failed to add block. HTTP Status: " + response.statusCode());
                }
            } catch (Exception ex) {
                showAlert("Failed to save block: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> addBlockStage.close());

        formBox.getChildren().addAll(blockTypeComboBox, saveButton, cancelButton);

        Scene scene = new Scene(formBox, 500, 600);
        addBlockStage.setScene(scene);
        addBlockStage.show();
    }


    private String getBlockType(Block block) {
        if (block instanceof MainTank) {
            return "MainTank";
        } else if (block instanceof SecondaryTank) {
            return "SecondaryTank";
        } else if (block instanceof Pump) {
            return "Pump";
        }
        return "Unknown";
    }

}
