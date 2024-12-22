package com.example.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class ReportsController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> metricComboBox;

    @FXML
    private ComboBox<String> formatComboBox;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button generateReportButton;

    @FXML
    private Button exportChartButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupEventHandlers();
    }

    private void setupComboBoxes() {
        metricComboBox.getItems().addAll("Performance", "Costs", "Quality", "Resource Usage", "Availability");

    }

    private void setupEventHandlers() {
        generateReportButton.setOnAction(e -> fetchAndVisualizeData());
        exportChartButton.setOnAction(e -> exportChartAsImage());
    }

    private void fetchAndVisualizeData() {
        try {
            String metric = metricComboBox.getValue();
            if (metric == null || metric.isEmpty()) {
                showAlert("Please select a metric.");
                return;
            }

            lineChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(metric);

            series.getData().add(new XYChart.Data<>("2023-01", 100));
            series.getData().add(new XYChart.Data<>("2023-02", 200));
            series.getData().add(new XYChart.Data<>("2023-03", 150));
            series.getData().add(new XYChart.Data<>("2023-04", 300));

            lineChart.getData().add(series);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to fetch and visualize data: " + ex.getMessage());
        }
    }

    private void exportChartAsImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Chart");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            File file = fileChooser.showSaveDialog(exportChartButton.getScene().getWindow());

            if (file != null) {
                WritableImage snapshot = lineChart.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
                ImageIO.write(bufferedImage, "png", file);
                showAlert("Chart exported successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to export chart: " + ex.getMessage());
        }
    }

    @FXML
    private void returnToMainView() {
        switchToView("main-view.fxml");
    }

}