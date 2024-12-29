package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobReportingService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;

        switch (reportType.toUpperCase()) {
            case "JOB_OVERVIEW" -> {
                return generatePdf(reportType, historicalData, historicalData, visualization, this::createJobOverviewTable);
            }
            case "JOB_MACHINE_UTILIZATION" -> {
                if (isVisualization) {
                    visualization = generatePieChartForMachineUtilization(currentData, historicalData);
                }
                return generatePdf(reportType, currentData, historicalData, visualization, this::createMachineUtilizationTable);
            }
            case "JOB_PROGRAM_USAGE" -> {
                if (isVisualization) {
                    visualization = generateBarChartForPrograms(currentData, historicalData);
                }
                return generatePdf(reportType, currentData, historicalData, visualization, this::createProgramUsageTable);
            }
            case "JOB_RECIPE_ANALYSIS" -> {
                if (isVisualization) {
                    visualization = generateBarChartForRecipes(currentData, historicalData);
                }
                return generatePdf(reportType, currentData, historicalData, visualization, this::createRecipeAnalysisTable);
            }
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    private byte[] generatePdf(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, byte[] visualization, TableGenerator tableGenerator) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Job Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            String usageDescription = switch (reportType.toUpperCase()) {
                case "JOB_OVERVIEW" -> "Summary of all completed jobs.";
                case "JOB_MACHINE_UTILIZATION" -> "Analysis of machine utilization over a given period.";
                case "JOB_PROGRAM_USAGE" -> "Statistics on the use of programs.";
                case "JOB_RECIPE_ANALYSIS" -> "Analysis of recipe utilization.";
                default -> "Unknown report type.";
            };

            document.add(new Paragraph("Usage: " + usageDescription, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            if (visualization != null) {
                document.add(new Paragraph("Visualization", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph(" "));
                Image chartImage = Image.getInstance(visualization);
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
            }

            if (currentData != null && !currentData.isEmpty()) {
                document.add(new Paragraph("Current Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph(" "));
                document.add(tableGenerator.generateTable(currentData, historicalData));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private PdfPTable createJobOverviewTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.5f, 2f, 2f, 2f, 1.5f, 1.5f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Job ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Machine Type", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Start Time", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "End Time", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Machine Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Program Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Recipe Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> entry : currentData) {
            Map<String, Object> job = (Map<String, Object>) entry.get("job");
            addCell(table, String.valueOf(job.get("jobId")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, ((boolean) job.get("isDryer") ? "Dryer" : "Dyeing"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(job.get("startTime")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(job.get("endTime")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(job.get("machineName")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(job.get("programName")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(job.get("recipeName")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
        }

        return table;
    }

    private PdfPTable createMachineUtilizationTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2f, 2f, 2f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Machine Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Task Count", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Total Time (hrs)", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Avg Time per Task (min)", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("machineName")))
                .forEach((machineName, jobs) -> {
                    long totalTasks = jobs.size();
                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();

                    addCell(table, machineName, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.valueOf(totalTasks), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.format("%.2f", totalTime / 60.0), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.format("%.2f", totalTime / (double) totalTasks), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                });

        return table;
    }

    private PdfPTable createProgramUsageTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2f, 4f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Program Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Machines", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Avg Duration (min)", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("programName")))
                .forEach((programName, jobs) -> {
                    Set<String> machineNames = jobs.stream()
                            .map(job -> (String) job.get("machineName"))
                            .collect(Collectors.toSet());

                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();

                    addCell(table, programName, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.join(", ", machineNames), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.format("%.2f", totalTime / (double) jobs.size()), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                });

        return table;
    }


    private PdfPTable createRecipeAnalysisTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2f, 4f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Recipe Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Machines", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Avg Duration (min)", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("recipeName")))
                .forEach((recipeName, jobs) -> {
                    Set<String> machineNames = jobs.stream()
                            .map(job -> (String) job.get("machineName"))
                            .collect(Collectors.toSet());

                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();

                    addCell(table, recipeName, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.join(", ", machineNames), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                    addCell(table, String.format("%.2f", totalTime / (double) jobs.size()), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
                });

        return table;
    }

    private void addCell(PdfPTable table, String content, Font font, int alignment, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        table.addCell(cell);
    }

    public byte[] generatePieChartForMachineUtilization(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("machineName")))
                .forEach((machineName, jobs) -> {
                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();
                    dataset.setValue(machineName, totalTime / 60.0);
                });

        JFreeChart chart = ChartFactory.createPieChart(
                "Machine Utilization",
                dataset,
                false,
                true,
                false
        );

        customizePieChart(chart);
        return chartToByteArray(chart);
    }

    private void customizePieChart(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setCircular(true);
        plot.setLabelBackgroundPaint(Color.WHITE);
    }

    public byte[] generateBarChartForPrograms(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("programName")))
                .forEach((programName, jobs) -> {
                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();
                    double avgDuration = totalTime / (double) jobs.size();
                    dataset.addValue(avgDuration, "Avg Duration (min)", programName);
                });

        JFreeChart chart = ChartFactory.createBarChart(
                "Program Usage",
                "Program Name",
                "Avg Duration (min)",
                dataset
        );

        customizeChart(chart);
        return chartToByteArray(chart);
    }

    public byte[] generateBarChartForRecipes(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        mergeData(currentData, historicalData).stream()
                .map(entry -> (Map<String, Object>) entry.get("job"))
                .collect(Collectors.groupingBy(job -> (String) job.get("recipeName")))
                .forEach((recipeName, jobs) -> {
                    long totalTime = jobs.stream()
                            .mapToLong(job -> Duration.between(LocalDateTime.parse((String) job.get("startTime")), LocalDateTime.parse((String) job.get("endTime"))).toMinutes())
                            .sum();
                    double avgDuration = totalTime / (double) jobs.size();
                    dataset.addValue(avgDuration, "Avg Duration (min)", recipeName);
                });

        JFreeChart chart = ChartFactory.createBarChart(
                "Recipe Analysis",
                "Recipe Name",
                "Avg Duration (min)",
                dataset
        );

        customizeChart(chart);
        return chartToByteArray(chart);
    }

    private void customizeChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        chart.removeLegend();
    }

    private List<Map<String, Object>> mergeData(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        if (currentData == null) {
            return historicalData;
        } else if (historicalData == null) {
            return currentData;
        }

        return currentData.stream()
                .collect(Collectors.toList());
    }

    private byte[] chartToByteArray(JFreeChart chart) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating chart", e);
        }
    }

    private interface TableGenerator {
        PdfPTable generateTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData);
    }
}