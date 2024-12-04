package com.example.client.controller;

import com.example.client.model.machine.Dryer;
import com.example.client.model.machine.Dyeing;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
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

    public void initialize() {
        checkMachineStatus();
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
                    .GET()
                    .build();
            HttpResponse<String> dryerResponse = client.send(dryerRequest, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> dryerResponseMap = mapper.readValue(dryerResponse.body(), Map.class);
            List<Dryer> dryers = mapper.convertValue(dryerResponseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Dryer.class));

            HttpRequest dyeingRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/dyeing/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
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



    @FXML
    private void goToView1() {
        switchToView("view1.fxml");
    }

    @FXML
    private void goToView2() {
        switchToView("view2.fxml");
    }

    @FXML
    private void goToView3() {
        switchToView("view3.fxml");
    }

    @FXML
    private void goToView4() {
        switchToView("view4.fxml");
    }

    @FXML
    private void goToView5() {
        switchToView("machine-view.fxml");
    }

    @FXML
    private void logout() {
        switchToView("login-view.fxml");
    }

    @FXML
    private void exitApplication() {
        System.exit(0);
    }
}
