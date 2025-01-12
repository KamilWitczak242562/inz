package com.example.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class ReportsController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private CheckBox includeChartCheckBox;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private ComboBox<String> metricComboBox;

    @FXML
    private ScrollPane pdfScrollPane;

    @FXML
    private Pane pdfContainer;


    private byte[] cachedPdfBytes;


    @FXML
    public void initialize() {
        setupCategoryComboBox();
    }

    private void resetPdfView() {
        pdfContainer.getChildren().clear();
        pdfScrollPane.setHvalue(0);
        pdfScrollPane.setVvalue(0);
    }


    @FXML
    private void onGenerateReportButtonClick() {
        try {
            resetPdfView();

            String responseHistory = sendGetRequestToGetHistoricalData();
            String responseCurrent = sendGetRequestToGetCurrentData();

            JSONObject body = processReportData(getCategoryKey(getSelectedCategory()), responseCurrent, responseHistory);

            System.out.println(body.toString());

            sendPostRequestToGeneratePDF(body);

            if (cachedPdfBytes != null) {
                renderPdfAsImages(cachedPdfBytes);
            }


        } catch (Exception e) {
            showAlert("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderPdfAsImages(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            pdfContainer.getChildren().clear();

            VBox imageContainer = new VBox();

            double adjustedWidth = pdfScrollPane.getWidth() - 20;

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bim = renderer.renderImageWithDPI(i, 150);
                WritableImage fxImage = SwingFXUtils.toFXImage(bim, null);
                ImageView imageView = new ImageView(fxImage);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(adjustedWidth);

                imageContainer.getChildren().add(imageView);
            }

            pdfContainer.getChildren().add(imageContainer);

            pdfScrollPane.setPannable(false);
        } catch (IOException e) {
            showAlert("Unable to render PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private JSONObject processReportData(String selectedCategory, String responseCurrent, String responseHistory) throws Exception {
        switch (selectedCategory) {
            case "dryer":
                return processDryerData(responseCurrent, responseHistory);
            case "dyeing":
                return processDyeingData(responseCurrent, responseHistory);
            case "job":
                return processJobData(responseCurrent, responseHistory);
            case "resource":
                return processResourceData(responseCurrent, responseHistory);
            case "supplier":
                return processSupplierData(responseCurrent, responseHistory);
            case "program":
                return processProgramData(responseCurrent, responseHistory);
            case "recipe":
                return processRecipeData(responseCurrent, responseHistory);
            default:
                throw new IllegalArgumentException("Unsupported category: " + selectedCategory);
        }
    }

    private JSONObject processProgramData(String currentResponse, String empty) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(currentResponse, Map.class);
        List<Map<String, Object>> currentList = (List<Map<String, Object>>) responseMap.get("response");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", true);

        JSONArray currentDataArray = new JSONArray();

        for (Map<String, Object> programEntry : currentList) {
            JSONObject programObject = new JSONObject();
            programObject.put("programId", programEntry.get("programId"));
            programObject.put("name", programEntry.get("name"));

            List<Integer> blockIds = (List<Integer>) programEntry.get("blockIds");
            JSONArray blocksArray = new JSONArray();

            for (Integer blockId : blockIds) {
                JSONObject blockDetails = fetchBlockById(blockId);

                blocksArray.put(blockDetails);
            }

            programObject.put("blocks", blocksArray);

            JSONObject wrapper = new JSONObject();
            wrapper.put("program", programObject);
            currentDataArray.put(wrapper);
        }

        result.put("currentData", currentDataArray);
        result.put("historicalData", new JSONArray());

        return result;
    }

    private JSONObject fetchBlockById(int blockId) {
        String url = "http://localhost:8080/api/v1/recipes/blocks/" + blockId;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");
                if (responseData != null) {
                    return new JSONObject(responseData);
                } else {
                    throw new IllegalArgumentException("Response does not contain expected data for block ID: " + blockId);
                }
            } else {
                throw new IllegalArgumentException("Failed to fetch block data from " + url + ": " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from " + url + ": " + e.getMessage());
        }
    }


    private JSONObject processRecipeData(String currentResponse, String empty) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> responseMap = mapper.readValue(currentResponse, Map.class);
        List<Map<String, Object>> currentList = (List<Map<String, Object>>) responseMap.get("response");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", true);

        JSONArray currentDataArray = new JSONArray();

        for (Map<String, Object> recipeEntry : currentList) {
            JSONObject recipeObject = new JSONObject();
            recipeObject.put("recipeId", recipeEntry.get("id"));
            recipeObject.put("name", recipeEntry.get("name"));
            recipeObject.put("description", recipeEntry.get("description"));

            Map<String, Double> resourcesQuantities = (Map<String, Double>) recipeEntry.get("resourcesQuantities");

            JSONArray resourcesArray = new JSONArray();
            for (Map.Entry<String, Double> resourceEntry : resourcesQuantities.entrySet()) {
                int resourceId = Integer.parseInt(resourceEntry.getKey());
                double quantity = resourceEntry.getValue();

                JSONObject resourceDetails = fetchResourceById(resourceId);
                JSONObject resourceObject = new JSONObject();
                resourceObject.put("resource", resourceDetails);
                resourceObject.put("quantity", quantity);

                resourcesArray.put(resourceObject);
            }

            recipeObject.put("resourcesQuantities", resourcesArray);

            JSONObject wrapper = new JSONObject();
            wrapper.put("recipe", recipeObject);
            currentDataArray.put(wrapper);
        }

        result.put("currentData", currentDataArray);
        result.put("historicalData", new JSONArray());

        return result;
    }


    private JSONObject fetchResourceById(Integer resourceId) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8080/api/v1/resources/resource/" + resourceId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");

                if (responseData != null) {
                    return new JSONObject(responseData);
                } else {
                    throw new IllegalArgumentException("Response does not contain expected data for resource ID: " + resourceId);
                }
            } else {
                throw new IllegalArgumentException("Failed to fetch resource for ID: " + resourceId + ". Response: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching resource details for ID: " + resourceId + ": " + e.getMessage(), e);
        }
    }


    private JSONObject processSupplierData(String currentResponse, String historicalResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> currentList = (List<Map<String, Object>>) mapper.readValue(currentResponse, Map.class).get("response");
        List<Map<String, Object>> historicalList = (List<Map<String, Object>>) mapper.readValue(historicalResponse, Map.class).get("response");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", true);

        JSONArray currentData = new JSONArray();
        for (Map<String, Object> supplier : currentList) {
            JSONObject supplierJson = new JSONObject();
            supplierJson.put("supplierId", supplier.get("supplierId"));
            supplierJson.put("name", supplier.get("name"));
            supplierJson.put("contactInfo", supplier.get("contactInfo"));
            supplierJson.put("address", supplier.get("address"));

            List<Map<String, Object>> resources = (List<Map<String, Object>>) supplier.get("resources");
            JSONArray resourcesArray = new JSONArray();
            for (Map<String, Object> resource : resources) {
                JSONObject resourceJson = new JSONObject();
                resourceJson.put("resourceId", resource.get("resourceId"));
                resourceJson.put("name", resource.get("name"));
                resourceJson.put("description", resource.get("description"));
                resourceJson.put("currentStock", resource.get("currentStock"));
                resourceJson.put("unit", resource.get("unit"));
                resourcesArray.put(resourceJson);
            }
            supplierJson.put("resources", resourcesArray);
            currentData.put(supplierJson);
        }
        result.put("currentData", currentData);

        JSONArray historicalData = new JSONArray();
        for (Map<String, Object> entry : historicalList) {
            JSONObject historicalEntry = new JSONObject();
            historicalEntry.put("supplierId", entry.get("supplierId"));
            historicalEntry.put("name", entry.get("name"));
            historicalEntry.put("revisionType", entry.get("revisionType"));

            String revisionDate = (String) entry.get("revisionDate");
            LocalDateTime parsedDate = LocalDateTime.parse(revisionDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            historicalEntry.put("revisionDate", parsedDate.format(outputFormatter));

            historicalData.put(historicalEntry);
        }
        result.put("historicalData", historicalData);

        return result;
    }


    private JSONObject processResourceData(String currentResponse, String historicalResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> currentList = (List<Map<String, Object>>) mapper.readValue(currentResponse, Map.class).get("response");
        List<Map<String, Object>> historicalList = (List<Map<String, Object>>) mapper.readValue(historicalResponse, Map.class).get("response");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", includeChartCheckBox.isSelected());

        JSONArray currentData = new JSONArray();
        for (Map<String, Object> item : currentList) {
            JSONObject resourceData = new JSONObject();
            resourceData.put("resourceId", item.get("resourceId"));
            resourceData.put("name", item.get("name"));
            resourceData.put("description", item.get("description"));
            resourceData.put("unit", item.get("unit"));
            resourceData.put("currentStock", item.get("currentStock"));
            resourceData.put("reorderLevel", item.get("reorderLevel"));
            currentData.put(resourceData);
        }
        result.put("currentData", currentData);

        JSONArray historicalData = new JSONArray();
        for (Map<String, Object> item : historicalList) {
            JSONObject historicalEntry = new JSONObject();
            Map<String, Object> resource = (Map<String, Object>) item.get("resource");
            JSONObject resourceData = new JSONObject();
            resourceData.put("resourceId", resource.get("resourceId"));
            resourceData.put("name", resource.get("name"));
            resourceData.put("description", resource.get("description"));
            resourceData.put("unit", resource.get("unit"));
            resourceData.put("currentStock", resource.get("currentStock"));
            resourceData.put("reorderLevel", resource.get("reorderLevel"));
            historicalEntry.put("resource", resourceData);
            historicalEntry.put("revisionType", item.get("revisionType"));
            historicalEntry.put("revisionDate", item.get("revisionDate"));
            historicalData.put(historicalEntry);
        }
        result.put("historicalData", historicalData);

        return result;
    }


    private JSONObject processJobData(String currentResponse, String historicalResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> currentList = (List<Map<String, Object>>) mapper.readValue(currentResponse, Map.class).get("response");
        List<Map<String, Object>> historicalList = (List<Map<String, Object>>) mapper.readValue(historicalResponse, Map.class).get("response");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", includeChartCheckBox.isSelected());

        JSONArray currentData = new JSONArray();
        for (Map<String, Object> item : currentList) {
            JSONObject wrapper = new JSONObject();
            JSONObject jobData = new JSONObject(item);

            jobData.put("machineName", fetchMachineName((boolean) jobData.get("dryer"), (int) jobData.get("machineId")));
            jobData.put("programName", fetchProgramName((int) jobData.get("programId")));
            jobData.put("recipeName", fetchRecipeName((int) jobData.get("recipeId")));
            jobData.put("isDryer", jobData.remove("dryer"));

            jobData.remove("machineId");
            jobData.remove("programId");
            jobData.remove("recipeId");

            truncateDateField(jobData, "startTime");
            truncateDateField(jobData, "endTime");

            wrapper.put("job", jobData);
            currentData.put(wrapper);
        }
        result.put("currentData", currentData);

        JSONArray historicalData = new JSONArray();
        for (Map<String, Object> item : historicalList) {
            JSONObject historicalEntry = new JSONObject();

            JSONObject jobData = new JSONObject((Map<String, Object>) item.get("job"));
            jobData.put("machineName", fetchMachineName((boolean) jobData.get("dryer"), (int) jobData.get("machineId")));
            jobData.put("programName", fetchProgramName((int) jobData.get("programId")));
            jobData.put("recipeName", fetchRecipeName((int) jobData.get("recipeId")));
            jobData.put("isDryer", jobData.remove("dryer"));

            jobData.remove("machineId");
            jobData.remove("programId");
            jobData.remove("recipeId");

            truncateDateField(jobData, "startTime");
            truncateDateField(jobData, "endTime");

            historicalEntry.put("job", jobData);

            String revisionDate = item.get("revisionDate").toString();
            LocalDateTime parsedDate = LocalDateTime.parse(revisionDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).truncatedTo(ChronoUnit.SECONDS);
            historicalEntry.put("revisionDate", parsedDate.format(outputFormatter));

            historicalEntry.put("revisionType", item.get("revisionType").toString());

            historicalData.put(historicalEntry);
        }
        result.put("historicalData", historicalData);

        return result;
    }


    private String fetchMachineName(boolean isDryer, int machineId) {
        String type = isDryer ? "dryer" : "dyeing";
        String url = String.format("http://localhost:8080/api/v1/machine/%s/%d", type, machineId);

        return sendGetRequestForName(url, "name");
    }


    private String fetchProgramName(int programId) {
        String url = String.format("http://localhost:8080/api/v1/recipes/program/%d", programId);

        return sendGetRequestForName(url, "name");
    }

    private String fetchRecipeName(int recipeId) {
        String url = String.format("http://localhost:8080/api/v1/recipes/recipe/%d", recipeId);

        return sendGetRequestForName(url, "name");
    }

    private String sendGetRequestForName(String url, String key) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");

                if (responseData != null && responseData.containsKey(key)) {
                    return responseData.get(key).toString();
                } else {
                    throw new IllegalArgumentException("Key '" + key + "' not found in response: " + response.body());
                }
            } else {
                throw new IllegalArgumentException("Failed to fetch data from " + url + ": " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from " + url + ": " + e.getMessage());
        }
    }


    private JSONObject processDyeingData(String currentResponse, String historicalResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> currentList = (List<Map<String, Object>>) mapper.readValue(currentResponse, Map.class).get("response");
        List<Map<String, Object>> historicalList = (List<Map<String, Object>>) mapper.readValue(historicalResponse, Map.class).get("response");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", includeChartCheckBox.isSelected());

        JSONArray currentData = new JSONArray();
        for (Map<String, Object> item : currentList) {
            JSONObject wrapper = new JSONObject();
            JSONObject dyeingData = new JSONObject(item);

            truncateDateField(dyeingData, "startWork");

            wrapper.put("dyeing", dyeingData);
            currentData.put(wrapper);
        }
        result.put("currentData", currentData);

        JSONArray historicalData = new JSONArray();
        for (Map<String, Object> item : historicalList) {
            JSONObject historicalEntry = new JSONObject();

            JSONObject dyeingData = new JSONObject((Map<String, Object>) item.get("dyeing"));
            truncateDateField(dyeingData, "startWork");

            historicalEntry.put("dyeing", dyeingData);

            String revisionDate = item.get("revisionDate").toString();
            LocalDateTime parsedDate = LocalDateTime.parse(revisionDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).truncatedTo(ChronoUnit.SECONDS);
            historicalEntry.put("revisionDate", parsedDate.format(outputFormatter));

            historicalEntry.put("revisionType", item.get("revisionType").toString());

            historicalData.put(historicalEntry);
        }
        result.put("historicalData", historicalData);

        return result;
    }


    private JSONObject processDryerData(String currentResponse, String historicalResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> currentList = (List<Map<String, Object>>) mapper.readValue(currentResponse, Map.class).get("response");
        List<Map<String, Object>> historicalList = (List<Map<String, Object>>) mapper.readValue(historicalResponse, Map.class).get("response");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        JSONObject result = new JSONObject();
        result.put("reportType", getSelectedMetric());
        result.put("isVisualization", includeChartCheckBox.isSelected());

        JSONArray currentData = new JSONArray();
        for (Map<String, Object> item : currentList) {
            JSONObject wrapper = new JSONObject();
            JSONObject dryerData = new JSONObject(item);

            truncateDateField(dryerData, "startWork");
            truncateDateField(dryerData, "endWork");

            wrapper.put("dryer", dryerData);
            currentData.put(wrapper);
        }
        result.put("currentData", currentData);

        JSONArray historicalData = new JSONArray();
        for (Map<String, Object> item : historicalList) {
            JSONObject historicalEntry = new JSONObject();

            JSONObject dryerData = new JSONObject((Map<String, Object>) item.get("dryer"));
            truncateDateField(dryerData, "startWork");
            truncateDateField(dryerData, "endWork");

            historicalEntry.put("dryer", dryerData);

            String revisionDate = item.get("revisionDate").toString();
            LocalDateTime parsedDate = LocalDateTime.parse(revisionDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).truncatedTo(ChronoUnit.SECONDS);
            historicalEntry.put("revisionDate", parsedDate.format(outputFormatter));

            historicalEntry.put("revisionType", item.get("revisionType").toString());

            historicalData.put(historicalEntry);
        }
        result.put("historicalData", historicalData);

        return result;
    }

    private void truncateDateField(JSONObject object, String fieldName) {
        if (object.has(fieldName) && object.get(fieldName) != JSONObject.NULL) {
            String rawDate = object.getString(fieldName);

            int truncateIndex = Math.min(rawDate.length(), 19);
            String truncatedDate = rawDate.substring(0, truncateIndex);

            object.put(fieldName, truncatedDate);
        }
    }

    @FXML
    private void onSavePdfButtonClick() {
        if (cachedPdfBytes == null) {
            showAlert("No PDF to save. Generate the report first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(cachedPdfBytes);
                showAlert("PDF saved successfully to " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Failed to save PDF: " + e.getMessage());
            }
        }
    }


    public void sendPostRequestToGeneratePDF(JSONObject body) {
        String url = buildUrlToGeneratePDF();
        cachedPdfBytes = null;


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                cachedPdfBytes = response.body();
            } else {
                throw new IllegalArgumentException("Generate Report request failed with status: " + response.statusCode() + ", response: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during Generate Report request: " + e.getMessage());
        }
    }

    public String sendGetRequestToGetCurrentData() {
        String url = buildUrlToGetCurrentData();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                return mapper.writeValueAsString(responseBody);
            } else {
                throw new IllegalArgumentException("Failed to fetch data: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during GET request: " + e.getMessage());
        }
    }

    private String getCategoryKey(String category) {
        switch (category.toLowerCase()) {
            case "dryer":
                return "dryer";
            case "dyeing":
                return "dyeing";
            case "job":
                return "job";
            case "resource":
                return "resource";
            case "supplier":
                return "supplier";
            case "program":
                return "program";
            case "recipe":
                return "recipe";
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }

    public String buildUrlToGetCurrentData() {
        String category = getSelectedCategory();

        if (category == null) {
            throw new IllegalArgumentException("Category, start date, and end date must be selected.");
        }

        String baseUrl = "http://localhost:8080/api/v1/";

        String categoryEndpoint = getCategoryEndpoint(category);

        String url = String.format("%s%s/%s/%s",
                baseUrl, categoryEndpoint, category.toLowerCase(), "getAll");

        return url;
    }

    public String sendGetRequestToGetHistoricalData() {
        String url = buildUrlToGetHistoricalData();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Client", getClientSecret())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
                return mapper.writeValueAsString(responseBody);
            } else if (response.statusCode() == 204) {
                showAlert("No data available for the selected parameters.");
                return "No data available for the selected parameters.";
            } else {
                throw new IllegalArgumentException("Failed to fetch data: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during GET request: " + e.getMessage());
        }
    }

    public String buildUrlToGetHistoricalData() {
        String category = getSelectedCategory();
        String startDate = getStartDate();
        String endDate = getEndDate();

        if (category == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Category, start date, and end date must be selected.");
        }

        String baseUrl = "http://localhost:8080/api/v1/";

        String categoryEndpoint = getCategoryEndpoint(category);

        String url = String.format("%s%s/%s/%s/%s&%s",
                baseUrl, categoryEndpoint, category.toLowerCase(), "history", startDate, endDate);

        return url;
    }

    private String getCategoryEndpoint(String category) {
        switch (category) {
            case "Dryer":
                return "machine";
            case "Dyeing":
                return "machine";
            case "Job":
                return "planning";
            case "Program":
                return "recipes";
            case "Recipe":
                return "recipes";
            case "Resource":
                return "resources";
            case "Supplier":
                return "resources";
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    public String buildUrlToGeneratePDF() {
        String category = getSelectedCategory();
        String metric = getSelectedMetric();
        String startDate = getStartDate();
        String endDate = getEndDate();

        if (category == null || metric == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Category, metric, start date, and end date must be selected.");
        }

        String baseUrl = "http://localhost:8080/api/v1/reporting/";

        String url = String.format("%s%s/generate",
                baseUrl, category.toLowerCase());

        return url;
    }


    public String getSelectedCategory() {
        return categoryComboBox.getValue();
    }

    public String getSelectedMetric() {
        return metricComboBox.getValue();
    }

    public String getStartDate() {
        if (startDatePicker.getValue() != null) {
            return formatDateTime(startDatePicker.getValue().atStartOfDay());
        }
        return null;
    }

    public String getEndDate() {
        if (endDatePicker.getValue() != null) {
            return formatDateTime(endDatePicker.getValue().atTime(23, 59, 59));
        }
        return null;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }

    private void setupCategoryComboBox() {
        categoryComboBox.setOnAction(e -> updateMetricComboBox());
    }

    private void updateMetricComboBox() {
        metricComboBox.getItems().clear();

        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null) {
            switch (selectedCategory) {
                case "Dryer":
                    metricComboBox.getItems().addAll("DRYER_USAGE", "DRYER_HISTORY");
                    break;
                case "Dyeing":
                    metricComboBox.getItems().addAll("DYEING_USAGE", "DYEING_HISTORY");
                    break;
                case "Job":
                    metricComboBox.getItems().addAll("JOB_OVERVIEW", "JOB_MACHINE_UTILIZATION", "JOB_PROGRAM_USAGE", "JOB_RECIPE_ANALYSIS");
                    break;
                case "Program":
                    metricComboBox.getItems().addAll("PROGRAM_OVERVIEW", "BLOCK_USAGE");
                    break;
                case "Recipe":
                    metricComboBox.getItems().addAll("RECIPE_OVERVIEW", "RESOURCE_DEPENDENCY");
                    break;
                case "Resource":
                    metricComboBox.getItems().addAll("RESOURCE_AVAILABILITY", "RESOURCE_REVISIONS");
                    break;
                case "Supplier":
                    metricComboBox.getItems().addAll("SUPPLIER_OVERVIEW", "SUPPLIER_RESOURCES");
                    break;
            }
        }
    }

    @FXML
    private void returnToMainView() {
        switchToView("main-view.fxml");
    }
}