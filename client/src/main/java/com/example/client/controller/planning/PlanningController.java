package com.example.client.controller.planning;

import com.example.client.model.machine.Dyeing;
import com.example.client.model.planning.Job;
import com.example.client.model.machine.Dryer;
import com.example.client.model.program.Program;
import com.example.client.model.recipe.Recipe;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.client.utils.Utils.*;

public class PlanningController {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Job> jobTable;

    @FXML
    private TableColumn<Job, Long> jobIdColumn;

    @FXML
    private TableColumn<Job, String> startTimeColumn;

    @FXML
    private TableColumn<Job, String> endTimeColumn;

    @FXML
    private TableColumn<Job, String> machineNameColumn;

    @FXML
    private TableColumn<Job, String> machineStatusColumn;

    @FXML
    private TableColumn<Job, String> programNameColumn;

    @FXML
    private TableColumn<Job, String> recipeNameColumn;

    @FXML
    private TableColumn<Job, Button> detailsColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button goToMainViewButton;

    @FXML
    private Button addJobButton;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private List<Map<String, Object>> machinesCache;
    private List<Map<String, Object>> programsCache;
    private List<Map<String, Object>> recipesCache;

    public void initialize() {
        setupTableColumns();
        setupRowStyles();
        loadJobs();
        initializeDataCaches();
        refreshButton.setOnAction(e -> loadJobs());
        refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        goToMainViewButton.setOnAction(e -> switchToView("main-view.fxml"));
        addJobButton.setVisible(getRole().equals("ADMIN"));
        addJobButton.setOnAction(e -> showAddJobDialog());
    }

    private void initializeDataCaches() {
        machinesCache = fetchMachines();
        programsCache = fetchPrograms();
        recipesCache = fetchRecipes();
    }

    private List<Map<String, Object>> fetchMachines() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestDryer = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/dryer/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpRequest requestDyeing = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/machine/dyeing/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> responseDryer = client.send(requestDryer, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseDyeing = client.send(requestDyeing, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<Map<String, Object>> dryers = (List<Map<String, Object>>) mapper.readValue(responseDryer.body(), Map.class).get("response");
            List<Map<String, Object>> dyeings = (List<Map<String, Object>>) mapper.readValue(responseDyeing.body(), Map.class).get("response");

            List<Map<String, Object>> machines = new ArrayList<>();
            machines.addAll(dryers);
            machines.addAll(dyeings);

            return machines;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> fetchPrograms() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/program/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return (List<Map<String, Object>>) mapper.readValue(response.body(), Map.class).get("response");
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> fetchRecipes() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/recipes/recipe/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return (List<Map<String, Object>>) mapper.readValue(response.body(), Map.class).get("response");
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private Long findIdByNameSafe(List<Map<String, Object>> data, String name, String idKey) {
        return data.stream()
                .filter(item -> name.equals(item.get("name")))
                .map(item -> {
                    Object id = item.get(idKey);
                    if (id == null) {
                        showAlert("Data inconsistency: Missing " + idKey + " for item " + name);
                        return null;
                    }
                    return Long.parseLong(id.toString());
                })
                .findFirst()
                .orElse(null);
    }

    private void showAddJobDialog() {
        Dialog<Job> dialog = new Dialog<>();
        dialog.setTitle("Add New Job");

        ComboBox<String> machineComboBox = new ComboBox<>(extractNames(machinesCache));
        ComboBox<String> programComboBox = new ComboBox<>(extractNames(programsCache));
        ComboBox<String> recipeComboBox = new ComboBox<>(extractNames(recipesCache));
        DatePicker startDatePicker = new DatePicker();

        TextField startHourField = new TextField();
        startHourField.setPromptText("HH");
        TextField startMinuteField = new TextField();
        startMinuteField.setPromptText("MM");

        DatePicker endDatePicker = new DatePicker();

        TextField endHourField = new TextField();
        endHourField.setPromptText("HH");
        TextField endMinuteField = new TextField();
        endMinuteField.setPromptText("MM");

        VBox form = new VBox(10,
                new Label("Machine"), machineComboBox,
                new Label("Program"), programComboBox,
                new Label("Recipe"), recipeComboBox,
                new Label("Start Date"), startDatePicker,
                new Label("Start Time"), new HBox(5, new Label("Hour"), startHourField, new Label("Minute"), startMinuteField),
                new Label("End Date"), endDatePicker,
                new Label("End Time"), new HBox(5, new Label("Hour"), endHourField, new Label("Minute"), endMinuteField)
        );

        dialog.getDialogPane().setContent(form);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                Job newJob = new Job();
                String selectedMachine = machineComboBox.getValue();

                if (selectedMachine == null || programComboBox.getValue() == null || recipeComboBox.getValue() == null ||
                        startDatePicker.getValue() == null || endDatePicker.getValue() == null ||
                        startHourField.getText().isEmpty() || startMinuteField.getText().isEmpty() ||
                        endHourField.getText().isEmpty() || endMinuteField.getText().isEmpty()) {
                    showAlert("Please fill in all fields before submitting.");
                    return null;
                }

                try {
                    int startHour = Integer.parseInt(startHourField.getText());
                    int startMinute = Integer.parseInt(startMinuteField.getText());
                    int endHour = Integer.parseInt(endHourField.getText());
                    int endMinute = Integer.parseInt(endMinuteField.getText());

                    Long machineId = findIdByNameSafe(machinesCache, selectedMachine, "machineId");
                    Long programId = findIdByNameSafe(programsCache, programComboBox.getValue(), "programId");
                    Long recipeId = findIdByNameSafe(recipesCache, recipeComboBox.getValue(), "id");

                    if (machineId == null || programId == null || recipeId == null) {
                        showAlert("Failed to retrieve required data from the cache.");
                        return null;
                    }

                    newJob.setMachineId(machineId);
                    newJob.setDryer(selectedMachine.toLowerCase().contains("dryer"));
                    newJob.setProgramId(programId);
                    newJob.setRecipeId(recipeId);
                    newJob.setStartTime(LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(startHour, startMinute)));
                    newJob.setEndTime(LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(endHour, endMinute)));
                    submitNewJob(newJob);
                } catch (NumberFormatException e) {
                    showAlert("Invalid time format. Please enter valid numbers.");
                } catch (Exception e) {
                    showAlert("Error occurred while adding job: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    private ObservableList<String> extractNames(List<Map<String, Object>> data) {
        return FXCollections.observableArrayList(
                data.stream().map(item -> item.get("name").toString()).toList()
        );
    }

    private void submitNewJob(Job newJob) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String requestBody = mapper.writeValueAsString(newJob);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/planning/job/new"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                showAlert("Job added successfully!");
                loadJobs();
            } else {
                showAlert("Failed to add job: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error occurred while adding job: " + e.getMessage());
        }
    }


    private void setupTableColumns() {
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));

        startTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime startTime = cellData.getValue().getStartTime();
            String formattedStartTime = startTime != null ? startTime.format(dateFormatter) : "N/A";
            return new SimpleStringProperty(formattedStartTime);
        });

        endTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime endTime = cellData.getValue().getEndTime();
            String formattedEndTime = endTime != null ? endTime.format(dateFormatter) : "N/A";
            return new SimpleStringProperty(formattedEndTime);
        });

        machineNameColumn.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String machineName = fetchMachineName(job.getMachineId(), job.isDryer());
            return new SimpleStringProperty(machineName);
        });

        machineStatusColumn.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String machineStatus = fetchMachineStatus(job.getMachineId(), job.isDryer());
            return new SimpleStringProperty(machineStatus);
        });

        programNameColumn.setCellValueFactory(cellData -> {
            String programName = fetchProgramName(cellData.getValue().getProgramId());
            return new SimpleStringProperty(programName);
        });

        recipeNameColumn.setCellValueFactory(cellData -> {
            String recipeName = fetchRecipeName(cellData.getValue().getRecipeId());
            return new SimpleStringProperty(recipeName);
        });

        detailsColumn.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            Button detailsButton = new Button("Details");
            detailsButton.setOnAction(e -> showJobDetails(job));
            return new SimpleObjectProperty<>(detailsButton);
        });
    }

    private void loadJobs() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/planning/job/getAll"))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            List<Job> jobs = mapper.convertValue(responseMap.get("response"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Job.class));

            if (jobs != null) {
                jobTable.getItems().setAll(jobs);
            } else {
                jobTable.getItems().clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load jobs: " + e.getMessage());
        }
    }

    private void setupRowStyles() {
        jobTable.setRowFactory(tableView -> new TableRow<Job>() {
            @Override
            protected void updateItem(Job job, boolean empty) {
                super.updateItem(job, empty);
                if (job == null || empty) {
                    setStyle("");
                } else {
                    String machineStatus = fetchMachineStatus(job.getMachineId(), job.isDryer()).toUpperCase();
                    switch (machineStatus) {
                        case "ERROR":
                            setStyle("-fx-background-color: #f8d7da;");
                            break;
                        case "WORKING":
                            setStyle("-fx-background-color: #d4edda;");
                            break;
                        case "IDLE":
                            setStyle("-fx-background-color: #d1ecf1;");
                            break;
                        case "WAITING_FOR_ACTION":
                            setStyle("-fx-background-color: #fff3cd;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }


    private String fetchMachineName(Long machineId, boolean isDryer) {
        String endpoint = isDryer ? "http://localhost:8080/api/v1/machine/dryer/" : "http://localhost:8080/api/v1/machine/dyeing/";
        Class<?> machineType = isDryer ? Dryer.class : Dyeing.class;
        Object machine = fetchEntityFromApi(endpoint + machineId, machineType);

        if (machine instanceof Dryer && isDryer) {
            Dryer dryer = (Dryer) machine;
            return dryer.getName() != null ? dryer.getName() : "Unknown Machine";
        } else if (machine instanceof Dyeing && !isDryer) {
            Dyeing dyeing = (Dyeing) machine;
            return dyeing.getName() != null ? dyeing.getName() : "Unknown Machine";
        }
        return "Unknown Machine";
    }

    private String fetchMachineStatus(Long machineId, boolean isDryer) {
        String endpoint = isDryer ? "http://localhost:8080/api/v1/machine/dryer/" : "http://localhost:8080/api/v1/machine/dyeing/";
        Class<?> machineType = isDryer ? Dryer.class : Dyeing.class;
        Object machine = fetchEntityFromApi(endpoint + machineId, machineType);

        if (machine instanceof Dryer && isDryer) {
            return ((Dryer) machine).getState();
        } else if (machine instanceof Dyeing && !isDryer) {
            return ((Dyeing) machine).getState();
        }
        return "Unknown Status";
    }


    private String fetchProgramName(Long programId) {
        Program program = fetchEntityFromApi("http://localhost:8080/api/v1/recipes/program/" + programId, Program.class);
        return program != null && program.getName() != null ? program.getName() : "Unknown Program";
    }

    private String fetchRecipeName(Long recipeId) {
        Recipe recipe = fetchEntityFromApi("http://localhost:8080/api/v1/recipes/recipe/" + recipeId, Recipe.class);
        return recipe != null && recipe.getName() != null ? recipe.getName() : "Unknown Recipe";
    }

    private <T> T fetchEntityFromApi(String url, Class<T> type) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body() == null || response.body().isEmpty()) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            return mapper.convertValue(responseMap.get("response"), type);
        } catch (Exception e) {
            return null;
        }
    }

    private String findNameByIdSafe(List<Map<String, Object>> data, Long id, String idKey) {
        return data.stream()
                .filter(item -> item.containsKey(idKey) && id.equals(Long.parseLong(item.get(idKey).toString())))
                .map(item -> item.get("name").toString())
                .findFirst()
                .orElse("Unknown");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return dateTime.format(formatter);
    }


    private void showJobDetails(Job job) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Job Details: " + job.getJobId());

        GridPane detailsGrid = new GridPane();
        detailsGrid.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F0F8FF; -fx-border-color: #B0C4DE; -fx-border-width: 2;");
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);

        detailsGrid.add(createStyledLabel("Job ID:"), 0, 0);
        detailsGrid.add(createValueLabel(String.valueOf(job.getJobId())), 1, 0);

        detailsGrid.add(createStyledLabel("Machine:"), 0, 1);
        detailsGrid.add(createValueLabel(findNameByIdSafe(machinesCache, job.getMachineId(), "machineId")), 1, 1);

        detailsGrid.add(createStyledLabel("Program:"), 0, 2);
        detailsGrid.add(createValueLabel(findNameByIdSafe(programsCache, job.getProgramId(), "programId")), 1, 2);

        detailsGrid.add(createStyledLabel("Recipe:"), 0, 3);
        detailsGrid.add(createValueLabel(findNameByIdSafe(recipesCache, job.getRecipeId(), "id")), 1, 3);

        detailsGrid.add(createStyledLabel("Start Time:"), 0, 4);
        detailsGrid.add(createValueLabel(formatDateTime(job.getStartTime())), 1, 4);

        String formattedEndTime = (job.getEndTime() != null) ? formatDateTime(job.getEndTime()) : "Not finished";
        detailsGrid.add(createStyledLabel("End Time:"), 0, 5);
        detailsGrid.add(createValueLabel(formattedEndTime), 1, 5);

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateButton.setOnAction(e -> updateJob(job, detailsStage));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this job?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteJob(job, detailsStage);
                }
            });
        });

        if (!"ADMIN".equals(getRole())) {
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");
        closeButton.setOnAction(e -> detailsStage.close());

        HBox actionButtons = new HBox(10, updateButton, deleteButton, closeButton);
        actionButtons.setStyle("-fx-alignment: center;");

        VBox mainLayout = new VBox(20, detailsGrid, actionButtons);
        mainLayout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F0F8FF;");

        Scene scene = new Scene(mainLayout, 450, 450);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        return label;
    }

    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        return label;
    }


    private void deleteJob(Job job, Stage detailsStage) {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to delete jobs.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/planning/job/" + job.getJobId()))
                    .header("Authorization", "Bearer " + getAuthToken())
                    .header("Client", getClientSecret())
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            detailsStage.close();
            loadJobs();
        } catch (Exception e) {
            showAlert("Failed to delete job: " + e.getMessage());
        }
    }

    private void updateJob(Job job, Stage detailsStage) {
        if (!"ADMIN".equals(getRole())) {
            showAlert("You don't have permission to update jobs.");
            return;
        }

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Job: " + job.getJobId());

        GridPane updateGrid = new GridPane();
        updateGrid.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F0F8FF; -fx-border-color: #B0C4DE; -fx-border-width: 2;");
        updateGrid.setHgap(20);
        updateGrid.setVgap(15);

        ComboBox<String> machineComboBox = new ComboBox<>(extractNames(machinesCache));
        machineComboBox.setValue(findNameByIdSafe(machinesCache, job.getMachineId(), "machineId"));

        ComboBox<String> programComboBox = new ComboBox<>(extractNames(programsCache));
        programComboBox.setValue(findNameByIdSafe(programsCache, job.getProgramId(), "programId"));

        ComboBox<String> recipeComboBox = new ComboBox<>(extractNames(recipesCache));
        recipeComboBox.setValue(findNameByIdSafe(recipesCache, job.getRecipeId(), "id"));

        DatePicker startDatePicker = new DatePicker(job.getStartTime().toLocalDate());
        TextField startHourField = new TextField(String.valueOf(job.getStartTime().getHour()));
        TextField startMinuteField = new TextField(String.valueOf(job.getStartTime().getMinute()));

        DatePicker endDatePicker = new DatePicker(job.getEndTime().toLocalDate());
        TextField endHourField = new TextField(String.valueOf(job.getEndTime().getHour()));
        TextField endMinuteField = new TextField(String.valueOf(job.getEndTime().getMinute()));

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        saveButton.setOnAction(e -> {
            try {
                int startHour = Integer.parseInt(startHourField.getText());
                int startMinute = Integer.parseInt(startMinuteField.getText());
                int endHour = Integer.parseInt(endHourField.getText());
                int endMinute = Integer.parseInt(endMinuteField.getText());

                job.setMachineId(findIdByNameSafe(machinesCache, machineComboBox.getValue(), "machineId"));
                job.setProgramId(findIdByNameSafe(programsCache, programComboBox.getValue(), "programId"));
                job.setRecipeId(findIdByNameSafe(recipesCache, recipeComboBox.getValue(), "id"));
                job.setStartTime(LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(startHour, startMinute)));
                job.setEndTime(LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(endHour, endMinute)));

                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String requestBody = mapper.writeValueAsString(job);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/planning/job/" + job.getJobId()))
                        .header("Authorization", "Bearer " + getAuthToken())
                        .header("Client", getClientSecret())
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                updateStage.close();
                detailsStage.close();
                loadJobs();
            } catch (Exception ex) {
                showAlert("Failed to update job: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
        cancelButton.setOnAction(e -> updateStage.close());

        updateGrid.add(createStyledLabel("Machine"), 0, 0);
        updateGrid.add(machineComboBox, 1, 0);

        updateGrid.add(createStyledLabel("Program"), 0, 1);
        updateGrid.add(programComboBox, 1, 1);

        updateGrid.add(createStyledLabel("Recipe"), 0, 2);
        updateGrid.add(recipeComboBox, 1, 2);

        updateGrid.add(createStyledLabel("Start Date"), 0, 3);
        updateGrid.add(startDatePicker, 1, 3);

        updateGrid.add(createStyledLabel("Start Time"), 0, 4);
        updateGrid.add(new HBox(10, createSmallLabel("Hour"), startHourField, createSmallLabel("Minute"), startMinuteField), 1, 4);

        updateGrid.add(createStyledLabel("End Date"), 0, 5);
        updateGrid.add(endDatePicker, 1, 5);

        updateGrid.add(createStyledLabel("End Time"), 0, 6);
        updateGrid.add(new HBox(10, createSmallLabel("Hour"), endHourField, createSmallLabel("Minute"), endMinuteField), 1, 6);

        HBox actionButtons = new HBox(20, saveButton, cancelButton);
        actionButtons.setStyle("-fx-alignment: center;");

        VBox mainLayout = new VBox(30, updateGrid, actionButtons);
        mainLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #F0F8FF;");

        Scene scene = new Scene(mainLayout, 700, 750);
        updateStage.setScene(scene);
        updateStage.show();
    }

    private Label createSmallLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        return label;
    }

}
