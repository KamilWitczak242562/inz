package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceReportService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        return switch (reportType.toUpperCase()) {
            case "RESOURCE_USAGE" ->
                    generateResourceUsageReport(reportType, currentData, historicalData, isVisualization);
            case "RESOURCE_AVAILABILITY" ->
                    generateResourceAvailabilityReport(reportType, currentData, isVisualization);
            case "RESOURCE_REVISIONS" -> generateResourceRevisionsReport(reportType, historicalData, isVisualization);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private byte[] generateResourceUsageReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateVisualization("RESOURCE_USAGE", currentData, historicalData);
        }
        return generatePdf(reportType, currentData, historicalData, visualization);
    }

    private byte[] generateResourceAvailabilityReport(String reportType, List<Map<String, Object>> currentData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateVisualization("RESOURCE_AVAILABILITY", currentData, null);
        }
        return generatePdf(reportType, currentData, null, visualization);
    }

    private byte[] generateResourceRevisionsReport(String reportType, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateVisualization("RESOURCE_REVISIONS", null, historicalData);
        }
        return generatePdf(reportType, null, historicalData, visualization);
    }

    public byte[] generatePdf(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, byte[] visualization) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Resource Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            if (historicalData != null && !historicalData.isEmpty()) {
                String dateRange = getDateRange(historicalData);
                document.add(new Paragraph("Date Range: " + dateRange, FontFactory.getFont(FontFactory.HELVETICA, 12)));
            }


            if (reportType.equalsIgnoreCase("RESOURCE_USAGE")) {
                document.add(new Paragraph("Usage: Analysis of resource consumption over time.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            } else if (reportType.equalsIgnoreCase("RESOURCE_AVAILABILITY")) {
                document.add(new Paragraph("Usage: Overview of current resource availability.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            } else if (reportType.equalsIgnoreCase("RESOURCE_REVISIONS")) {
                document.add(new Paragraph("Usage: Historical changes to resource data.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            }

            document.add(new Paragraph(" "));

            if (visualization != null && visualization.length > 0) {
                document.add(new Paragraph("Visualization", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                Image chartImage = Image.getInstance(visualization);
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                document.add(new Paragraph(" "));
            }

            if (currentData != null && !currentData.isEmpty()) {
                document.add(new Paragraph("Current Resource Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(createResourceTable(currentData));
                document.add(new Paragraph(" "));
            }

            if ("RESOURCE_REVISIONS".equalsIgnoreCase(reportType) && historicalData != null && !historicalData.isEmpty()) {
                document.add(new Paragraph("Historical Resource Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(createHistoricalTable(historicalData));
                document.add(new Paragraph(" "));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }


    private String getDateRange(List<Map<String, Object>> historicalData) {
        List<String> dates = historicalData.stream()
                .map(entry -> (String) entry.get("revisionDate"))
                .sorted()
                .collect(Collectors.toList());
        if (dates.isEmpty()) {
            return "N/A";
        }
        String startDate = formatDate(dates.get(0));
        String endDate = formatDate(dates.get(dates.size() - 1));
        return startDate + " - " + endDate;
    }

    private PdfPTable createResourceTable(List<Map<String, Object>> data) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        try {
            table.setWidths(new float[]{2f, 2.5f, 4f, 2f, 1.5f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting column widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Resource ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Description", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Current Stock", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Unit", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> resource : data) {
            addCell(table, String.valueOf(resource.get("resourceId")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) resource.get("name"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) resource.get("description"), cellFont, Element.ALIGN_LEFT, BaseColor.WHITE);
            addCell(table, String.valueOf(resource.get("currentStock")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) resource.get("unit"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
        }

        return table;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + value, e);
            }
        }
        throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
    }

    private PdfPTable createHistoricalTable(List<Map<String, Object>> data) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        try {
            table.setWidths(new float[]{2f, 3f, 2f, 2.5f, 2f, 1.5f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting column widths for historical table", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Revision Type", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Revision Date", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Resource ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Current Stock", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Unit", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> history : data) {
            Map<String, Object> resource = (Map<String, Object>) history.get("resource");

            String revisionType = getReadableRevisionType((String) history.getOrDefault("revisionType", "Unknown"));
            String revisionDate = formatDate((String) history.getOrDefault("revisionDate", ""));
            String resourceId = String.valueOf(resource.getOrDefault("resourceId", "N/A"));
            String name = (String) resource.getOrDefault("name", "N/A");
            String currentStock = String.valueOf(resource.getOrDefault("currentStock", "0"));
            String unit = (String) resource.getOrDefault("unit", "N/A");

            addCell(table, revisionType, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, revisionDate, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, resourceId, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, name, cellFont, Element.ALIGN_LEFT, BaseColor.WHITE);
            addCell(table, currentStock, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, unit, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
        }

        return table;
    }

    private void addCell(PdfPTable table, String content, Font font, int alignment, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String getReadableRevisionType(String revisionType) {
        return switch (revisionType.toUpperCase()) {
            case "MOD" -> "Modification";
            case "ADD" -> "Addition";
            case "DEL" -> "Deletion";
            default -> "Unknown";
        };
    }

    private String formatDate(String date) {
        try {
            FastDateFormat inputFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
            FastDateFormat outputFormat = FastDateFormat.getInstance("dd-MM-yyyy HH:mm:ss");
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            return date;
        }
    }

    public byte[] generateVisualization(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        switch (reportType.toUpperCase()) {
            case "RESOURCE_USAGE":
                return generateBarChart("Resource Usage", "Resource", "Usage", historicalData);
            case "RESOURCE_AVAILABILITY":
                return generatePieChart("Resource Availability", currentData);
            case "RESOURCE_REVISIONS":
                return generateLineChart("Resource Revisions", "Revision", "Value", historicalData);
            default:
                throw new IllegalArgumentException("Unsupported visualization type: " + reportType);
        }
    }

    private byte[] generateBarChart(String title, String categoryAxisLabel, String valueAxisLabel, List<Map<String, Object>> historicalData) {
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();

        try {
            Map<String, List<Map<String, Object>>> groupedData = historicalData.stream()
                    .filter(entry -> entry.containsKey("resource"))
                    .collect(Collectors.groupingBy(entry -> {
                        Map<String, Object> resource = (Map<String, Object>) entry.get("resource");
                        return (String) resource.get("name");
                    }));

            for (Map.Entry<String, List<Map<String, Object>>> group : groupedData.entrySet()) {
                String resourceName = group.getKey();
                List<Map<String, Object>> resourceHistory = group.getValue();

                resourceHistory.sort((a, b) -> {
                    String dateA = (String) a.get("revisionDate");
                    String dateB = (String) b.get("revisionDate");
                    return dateA.compareTo(dateB);
                });

                Double totalUsage = 0.0;

                for (int i = 1; i < resourceHistory.size(); i++) {
                    Map<String, Object> previous = resourceHistory.get(i - 1);
                    Map<String, Object> current = resourceHistory.get(i);

                    Double previousStock = toDouble(((Map<String, Object>) previous.get("resource")).get("currentStock"));
                    Double currentStock = toDouble(((Map<String, Object>) current.get("resource")).get("currentStock"));

                    if (previousStock != null && currentStock != null) {
                        totalUsage += Math.abs(previousStock - currentStock);
                    }
                }

                chartDataset.addValue(totalUsage, resourceName, "");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while generating bar chart data", e);
        }

        JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, chartDataset);

        var plot = chart.getCategoryPlot();
        plot.getDomainAxis().setVisible(false);
        var renderer = plot.getRenderer();

        for (int i = 0; i < chartDataset.getRowCount(); i++) {
            renderer.setSeriesPaint(i, getColorForIndex(i));
        }

        return chartToByteArray(chart);
    }


    private java.awt.Color getColorForIndex(int index) {
        java.awt.Color[] colors = {
                java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.GREEN,
                java.awt.Color.ORANGE, java.awt.Color.CYAN, java.awt.Color.MAGENTA,
                java.awt.Color.YELLOW, java.awt.Color.PINK, java.awt.Color.GRAY
        };
        return colors[index % colors.length];
    }

    private byte[] generatePieChart(String title, List<Map<String, Object>> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map<String, Object> entry : data) {
            String resourceName = (String) entry.get("name");
            Double stock = toDouble(entry.get("currentStock"));
            dataset.setValue(resourceName, stock);
        }
        JFreeChart chart = ChartFactory.createPieChart(title, dataset);
        return chartToByteArray(chart);
    }

    private byte[] generateLineChart(String title, String categoryAxisLabel, String valueAxisLabel, List<Map<String, Object>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            for (Map<String, Object> entry : data) {
                String revisionDate = (String) entry.get("revisionDate");
                Map<String, Object> resource = (Map<String, Object>) entry.get("resource");
                String resourceName = (String) resource.get("name");
                Double currentStock = toDouble(resource.get("currentStock"));

                dataset.addValue(currentStock, resourceName, revisionDate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while generating line chart", e);
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




