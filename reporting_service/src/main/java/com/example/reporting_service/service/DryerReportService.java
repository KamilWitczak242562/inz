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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DryerReportService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        return switch (reportType.toUpperCase()) {
            case "DRYER_USAGE" -> generateDryerUsageReport(reportType, currentData, historicalData, isVisualization);
            case "DRYER_HISTORY" ->
                    generateDryerHistoryReport(reportType, historicalData, isVisualization, currentData);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private byte[] generateDryerUsageReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generatePieChart(currentData, historicalData);
        }
        return generatePdf(reportType, currentData, historicalData, visualization);
    }

    private byte[] generateDryerHistoryReport(String reportType, List<Map<String, Object>> historicalData, boolean isVisualization, List<Map<String, Object>> currentData) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateLineChart("Dryer History", "Time", "Value", historicalData);
        }
        return generatePdf(reportType, currentData, historicalData, visualization);
    }

    public byte[] generatePdf(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, byte[] visualization) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Dryer Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            if (reportType.equalsIgnoreCase("DRYER_USAGE")) {
                document.add(new Paragraph("Usage: Analysis of dryer performance over time.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
                document.add(new Paragraph(" "));

                if (visualization != null) {
                    Image chartImage = Image.getInstance(visualization);
                    chartImage.scaleToFit(500, 400);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(chartImage);
                    document.add(new Paragraph(" "));
                }

                document.add(createUsageTable(currentData, historicalData));
            } else if (reportType.equalsIgnoreCase("DRYER_HISTORY")) {
                document.add(new Paragraph("Usage: Historical changes to dryer data.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
                document.add(createHistoryTable(historicalData, currentData));
            }

            document.add(new Paragraph(" "));
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private PdfPTable createUsageTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.5f, 2f, 2f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Dryer ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Total Errors", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Operational Duration (Hours)", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> dryerData : currentData) {
            Map<String, Object> dryer = (Map<String, Object>) dryerData.get("dryer");
            List<Map<String, Object>> dryerHistory = historicalData.stream()
                    .filter(entry -> ((Map<String, Object>) entry.get("dryer")).get("machineId").equals(dryer.get("machineId")))
                    .collect(Collectors.toList());

            Map<String, Long> stateDurations = calculateOperationalHoursWithStartEnd(dryerHistory);

            long operationalMinutes = stateDurations.getOrDefault("WORKING", 0L) + stateDurations.getOrDefault("WAITING_FOR_ACTION", 0L);
            long operationalHours = operationalMinutes / 60;

            int totalErrors = calculateErrorCount(dryerHistory);

            addCell(table, String.valueOf(dryer.get("machineId")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) dryer.get("name"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(totalErrors), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, String.valueOf(operationalHours), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
        }

        return table;
    }

    private int calculateErrorCount(List<Map<String, Object>> dryerHistory) {
        return (int) dryerHistory.stream()
                .filter(entry -> "ERROR".equals(((Map<String, Object>) entry.get("dryer")).get("state")))
                .count();
    }

    private byte[] generatePieChart(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map<String, Object> dryerData : currentData) {
            Map<String, Object> dryer = (Map<String, Object>) dryerData.get("dryer");
            List<Map<String, Object>> dryerHistory = historicalData.stream()
                    .filter(entry -> {
                        Map<String, Object> historyDryer = (Map<String, Object>) entry.get("dryer");
                        return historyDryer.get("machineId").equals(dryer.get("machineId"));
                    })
                    .collect(Collectors.toList());

            Map<String, Long> stateDurations = calculateOperationalHoursWithStartEnd(dryerHistory);

            long operationalMinutes = stateDurations.getOrDefault("WORKING", 0L) + stateDurations.getOrDefault("WAITING_FOR_ACTION", 0L);
            dataset.setValue((String) dryer.get("name"), operationalMinutes / 60.0);
        }

        JFreeChart chart = ChartFactory.createPieChart("Operational Time Distribution", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, Color.GREEN);
        plot.setSectionPaint(1, Color.ORANGE);
        plot.setLabelBackgroundPaint(Color.WHITE);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating pie chart", e);
        }
    }


    private Map<String, Long> calculateOperationalHoursWithStartEnd(List<Map<String, Object>> history) {
        Map<String, Long> stateDurations = new HashMap<>();
        LocalDateTime previousTimestamp = null;
        String previousState = null;

        for (Map<String, Object> entry : history) {
            Map<String, Object> dryer = (Map<String, Object>) entry.get("dryer");
            String currentState = (String) dryer.get("state");
            LocalDateTime currentTimestamp = LocalDateTime.parse((String) entry.get("revisionDate"));

            if (previousTimestamp != null && previousState != null) {
                long duration = Duration.between(previousTimestamp, currentTimestamp).toMinutes();
                stateDurations.put(previousState, stateDurations.getOrDefault(previousState, 0L) + duration);
            }

            previousTimestamp = currentTimestamp;
            previousState = currentState;
        }

        for (Map<String, Object> entry : history) {
            Map<String, Object> dryer = (Map<String, Object>) entry.get("dryer");
            if (dryer.get("startWork") != null && dryer.get("endWork") != null) {
                LocalDateTime startWork = LocalDateTime.parse((String) dryer.get("startWork"));
                LocalDateTime endWork = LocalDateTime.parse((String) dryer.get("endWork"));
                long duration = Duration.between(startWork, endWork).toMinutes();
                stateDurations.put("WORKING", stateDurations.getOrDefault("WORKING", 0L) + duration);
            }
        }

        return stateDurations;
    }

    private void addCell(PdfPTable table, String content, Font font, int alignment, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private PdfPTable createHistoryTable(List<Map<String, Object>> historicalData, List<Map<String, Object>> currentData) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.5f, 2f, 2f, 2f, 2f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Dryer ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Revision Type", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Revision Date", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "State", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        Map<String, String> revisionTypeMapping = Map.of(
                "MOD", "Modification",
                "ADD", "Addition",
                "DEL", "Deletion"
        );

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Map<String, Object> entry : historicalData) {
            Map<String, Object> dryer = (Map<String, Object>) entry.get("dryer");
            String revisionType = revisionTypeMapping.getOrDefault((String) entry.get("revisionType"), "Unknown");

            addCell(table, String.valueOf(dryer.get("machineId")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) dryer.get("name"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, revisionType, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, LocalDateTime.parse((String) entry.get("revisionDate")).format(dateFormatter), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);

            String state = getStateFromCurrentData(dryer.get("machineId"), currentData);
            addCell(table, state != null ? state : "Unknown", cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);

        }

        return table;
    }

    private String getStateFromCurrentData(Object machineId, List<Map<String, Object>> currentData) {
        if (currentData == null) {
            return null;
        }

        return currentData.stream()
                .map(data -> (Map<String, Object>) data.get("dryer"))
                .filter(dryer -> machineId.equals(dryer.get("machineId")))
                .map(dryer -> (String) dryer.get("state"))
                .findFirst()
                .orElse(null);
    }

    private byte[] generateLineChart(String title, String categoryAxisLabel, String valueAxisLabel, List<Map<String, Object>> historicalData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (historicalData != null) {
            for (Map<String, Object> entry : historicalData) {
                Map<String, Object> dryer = (Map<String, Object>) entry.get("dryer");
                dataset.addValue((Number) dryer.get("capacity"), (String) dryer.get("name"), (String) entry.get("revisionDate"));
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset);
        return chartToByteArray(chart);
    }

    private byte[] chartToByteArray(JFreeChart chart) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating chart", e);
        }
    }
}
