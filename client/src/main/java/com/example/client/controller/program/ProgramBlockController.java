package com.example.client.controller.program;

import com.example.client.model.program.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class ProgramBlockController {

    @FXML
    private GridPane programGrid;

    @FXML
    private Button addProgramButton;

    @FXML
    private Button addBlockButton;

    @FXML
    private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    public void initialize() {
        if ("ADMIN".equals(getRole())) {
            addProgramButton.setVisible(true);
            addBlockButton.setVisible(true);
            addProgramButton.setOnAction(e -> addProgram());
            addBlockButton.setOnAction(e -> addBlock());
        } else {
            addProgramButton.setVisible(false);
            addBlockButton.setVisible(false);
        }
        refreshButton.setOnAction(e -> refresh());
        loadPrograms();
    }

    @FXML
    private void loadPrograms() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() != null && !response.body().isEmpty()) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                List<Program> programs = objectMapper.convertValue(responseMap.get("response"),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Program.class));

                programGrid.getChildren().clear();

                int rowIndex = 0;
                for (Program program : programs) {
                    StackPane programTile = createProgramTile(program);
                    programTile.setOnMouseClicked(e -> showProgramDetails(program));
                    programGrid.add(programTile, 0, rowIndex);

                    int colIndex = 1;
                    for (Block block : program.getBlocks()) {
                        String blockType = getBlockType(block);
                        programGrid.add(createBlockTile(block, blockType), colIndex++, rowIndex);
                    }
                    rowIndex++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBlockType(Block block) {
        if (block instanceof MainTank) {
            return "MainTank";
        } else if (block instanceof SecondaryTank) {
            return "SecondaryTank";
        } else if (block instanceof Pump) {
            return "Pump";
        } else {
            return "Unknown";
        }
    }


    @FXML
    private void showProgramDetails(Program program) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Program Details");

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #FAFAFA;");

        Label idLabel = new Label("Program ID: " + program.getProgramId());
        Label nameLabel = new Label("Name: " + program.getName());
        Label blocksLabel = new Label("Blocks: " + program.getBlocks().size());

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateButton.setOnAction(e -> updateProgram(program, detailsStage));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteProgram(program, detailsStage));

        if (!"ADMIN".equals(getRole())) {
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsBox.getChildren().addAll(idLabel, nameLabel, blocksLabel, updateButton, deleteButton, closeButton);

        Scene scene = new Scene(detailsBox, 350, 350);
        detailsStage.setScene(scene);
        detailsStage.show();
    }


    @FXML
    private void updateProgram(Program program, Stage detailsStage) {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to update programs.");
            return;
        }

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Program: " + program.getProgramId());

        GridPane updateGrid = new GridPane();
        updateGrid.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F0F8FF; -fx-border-color: #B0C4DE; -fx-border-width: 2;");
        updateGrid.setHgap(20);
        updateGrid.setVgap(15);

        ComboBox<String> blockComboBox = new ComboBox<>();
        blockComboBox.getItems().addAll(fetchAvailableBlocks());

        TextField programNameField = new TextField(program.getName());

        Button addBlockButton = new Button("Add Block");
        addBlockButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        addBlockButton.setOnAction(e -> {
            try {
                Block selectedBlock = findBlockByName(blockComboBox.getValue());
                addBlockToProgram(program.getProgramId(), Long.valueOf(selectedBlock.getBlockId()));
            } catch (Exception ex) {
                showAlert("Failed to add block: " + ex.getMessage());
            }
        });

        Button removeBlockButton = new Button("Remove Block");
        removeBlockButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        removeBlockButton.setOnAction(e -> {
            try {
                Block selectedBlock = findBlockByName(blockComboBox.getValue());
                removeBlockFromProgram(program.getProgramId(), Long.valueOf(selectedBlock.getBlockId()));
            } catch (Exception ex) {
                showAlert("Failed to remove block: " + ex.getMessage());
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        saveButton.setOnAction(e -> {
            try {
                program.setName(programNameField.getText());
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/recipes/program/update"))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(program)))
                        .build();

                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                updateStage.close();
                detailsStage.close();
                loadPrograms();
            } catch (Exception ex) {
                showAlert("Failed to update program: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        cancelButton.setOnAction(e -> updateStage.close());

        updateGrid.add(new Label("Program Name:"), 0, 0);
        updateGrid.add(programNameField, 1, 0);

        updateGrid.add(new Label("Select Block:"), 0, 1);
        updateGrid.add(blockComboBox, 1, 1);

        updateGrid.add(addBlockButton, 0, 2);
        updateGrid.add(removeBlockButton, 1, 2);

        HBox actionButtons = new HBox(20, saveButton, cancelButton);
        actionButtons.setStyle("-fx-alignment: center;");

        VBox mainLayout = new VBox(30, updateGrid, actionButtons);
        mainLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #F0F8FF;");

        Scene scene = new Scene(mainLayout, 600, 400);
        updateStage.setScene(scene);
        updateStage.show();
    }

    private Block findBlockByName(String blockName) {
        try {
            String blockId = blockName.split(",")[0].replace("Block ID: ", "").trim();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/blocks/" + blockId))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() == null || response.body().isEmpty()) {
                throw new IllegalArgumentException("Block not found for block ID: " + blockId);
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);

            if (!responseMap.containsKey("response") || responseMap.get("response") == null) {
                throw new IllegalArgumentException("Block not found for block ID: " + blockId);
            }

            return objectMapper.convertValue(responseMap.get("response"), Block.class);

        } catch (Exception e) {
            throw new IllegalArgumentException("Block not found for block name: " + blockName, e);
        }
    }


    @FXML
    private void addBlockToProgram(Long programId, Long blockId) {
        try {
            String url = "http://localhost:8080/api/v1/recipes/program/" + programId + "/addBlock/" + blockId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                if (response.body() != null && !response.body().isEmpty()) {
                    Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                    showAlert(responseMap.getOrDefault("response", "Block added successfully").toString());
                } else {
                    showAlert("Block added successfully.");
                }
            } else if (response.statusCode() == 404) {
                showAlert("Program or Block not found. HTTP Status: 404");
            } else {
                showAlert("Failed to add block. HTTP Status: " + response.statusCode());
            }

            loadPrograms();
        } catch (Exception e) {
            showAlert("Failed to add block: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void removeBlockFromProgram(Long programId, Long blockId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/" + programId + "/removeBlock/" + blockId))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
            showAlert((String) responseMap.get("response"));
            loadPrograms();
        } catch (Exception e) {
            showAlert("Failed to remove block: " + e.getMessage());
        }
    }

    private List<String> fetchAvailableBlocks() {
        List<String> blocks = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/blocks/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() == null || response.body().isEmpty()) {
                showAlert("Failed to fetch blocks: Empty response from server.");
                return blocks;
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);

            if (!responseMap.containsKey("response") || responseMap.get("response") == null) {
                showAlert("No blocks found in response.");
                return blocks;
            }

            List<Block> availableBlocks = objectMapper.convertValue(
                    responseMap.get("response"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Block.class)
            );


            for (Block block : availableBlocks) {
                blocks.add(generateBlockDescription(block));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to fetch blocks: " + e.getMessage());
        }
        return blocks;
    }


    private String generateBlockDescription(Block block) {
        StringBuilder description = new StringBuilder();
        description.append("Block ID: ").append(block.getBlockId()).append(", Type: ").append(block.getDtype());

        if (block instanceof MainTank mainTank) {
            description.append(", Fill Level: ").append(mainTank.getFillLevel())
                    .append(", Hot Water: ").append(mainTank.getIsHotWater())
                    .append(", Target Temp: ").append(mainTank.getTargetTemperature());
        } else if (block instanceof SecondaryTank secondaryTank) {
            description.append(", Mixer Active: ").append(secondaryTank.getIsMixerActive())
                    .append(", Chemical Dose: ").append(secondaryTank.getChemicalDose());
        } else if (block instanceof Pump pump) {
            description.append(", RPM: ").append(pump.getRpm())
                    .append(", Circ Time In-Out: ").append(pump.getCirculationTimeInOut());
        }
        return description.toString();
    }


    @FXML
    private void deleteProgram(Program program, Stage detailsStage) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/delete/" + program.getProgramId()))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            detailsStage.close();
            loadPrograms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void addProgram() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/new"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            loadPrograms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addBlock() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/addBlock"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            loadPrograms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnToMainView() {
        switchToView("main-view.fxml");
    }

    @FXML
    private void refresh() {
        loadPrograms();
    }

    private StackPane createProgramTile(Program program) {
        StackPane tile = new StackPane();
        tile.setPrefSize(200, 100);
        tile.setStyle("-fx-border-color: #4CAF50; -fx-background-color: #E8F5E9; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox content = new VBox();
        content.setStyle("-fx-alignment: center; -fx-spacing: 10;");
        content.getChildren().addAll(
                new Label("Program ID: " + program.getProgramId()),
                new Label("Name: " + program.getName())
        );
        tile.getChildren().add(content);
        return tile;
    }

    private StackPane createBlockTile(Block block, String blockType) {
        StackPane tile = new StackPane();
        tile.setPrefSize(150, 80);
        tile.setStyle("-fx-border-color: #FF9800; -fx-background-color: #FFF3E0; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox content = new VBox();
        content.setStyle("-fx-alignment: center; -fx-spacing: 8;");
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
        closeButton.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsBox.getChildren().addAll(title);
        detailsBox.getChildren().addAll(blockDetails);
        detailsBox.getChildren().add(closeButton);

        Scene scene = new Scene(detailsBox, 400, 450);
        detailsStage.setScene(scene);
        detailsStage.show();
    }



}
