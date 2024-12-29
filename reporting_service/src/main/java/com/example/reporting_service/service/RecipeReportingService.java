package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipeReportingService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        return switch (reportType.toUpperCase()) {
            case "RECIPE_OVERVIEW" -> generateRecipeOverviewReport(currentData, isVisualization);
            case "RESOURCE_DEPENDENCY" -> generateResourceDependencyReport(currentData, isVisualization);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private byte[] generateRecipeOverviewReport(List<Map<String, Object>> currentData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateRecipeOverviewChart(currentData);
        }
        return generatePdf("RECIPE_OVERVIEW", currentData, null, visualization);
    }

    private byte[] generateResourceDependencyReport(List<Map<String, Object>> currentData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateResourceDependencyChart(currentData);
        }
        return generatePdf("RESOURCE_DEPENDENCY", currentData, null, visualization);
    }

    private byte[] generatePdf(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, byte[] visualization) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Recipe Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            String description = switch (reportType) {
                case "RECIPE_OVERVIEW" -> "Overview of all recipes.";
                case "RESOURCE_DEPENDENCY" -> "Analysis of resource dependencies across recipes.";
                default -> "Unknown report type.";
            };
            document.add(new Paragraph("Description: " + description, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            if (visualization != null && visualization.length > 0) {
                document.add(new Paragraph("Visualization", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                Image chartImage = Image.getInstance(visualization);
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                document.add(new Paragraph(" "));
            }

            if (currentData != null && !currentData.isEmpty()) {
                document.add(new Paragraph("Current Recipe Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                PdfPTable table = switch (reportType) {
                    case "RECIPE_OVERVIEW" -> createRecipeOverviewTable(currentData);
                    case "RESOURCE_DEPENDENCY" -> createResourceDependencyTable(currentData);
                    default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
                };
                document.add(table);
                document.add(new Paragraph(" "));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }



    public PdfPTable createRecipeOverviewTable(List<Map<String, Object>> currentData) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Recipe Name", "Description", "Number of Resources", "Total Resource Quantities");

        currentData.forEach(entry -> {
            Map<String, Object> recipe = (Map<String, Object>) entry.get("recipe");
            List<Map<String, Object>> resources = (List<Map<String, Object>>) recipe.get("resourcesQuantities");
            String recipeName = (String) recipe.get("name");
            String description = (String) recipe.get("description");

            double totalQuantity = resources.stream()
                    .mapToDouble(resource -> resource.get("quantity") != null ? (double) resource.get("quantity") : 0.0)
                    .sum();

            addTableRow(table,
                    recipeName != null ? recipeName : "Unknown",
                    description != null ? description : "No description",
                    String.valueOf(resources.size()),
                    String.format("%.2f", totalQuantity));
        });

        return table;
    }

    private PdfPTable createResourceDependencyTable(List<Map<String, Object>> currentData) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addTableHeader(table, "Resource Name", "Used In Recipes", "Total Quantity Across Recipes");

        currentData.stream()
                .flatMap(entry -> {
                    Map<String, Object> recipe = (Map<String, Object>) entry.get("recipe");
                    List<Map<String, Object>> resourcesQuantities = (List<Map<String, Object>>) recipe.get("resourcesQuantities");

                    return resourcesQuantities.stream()
                            .map(resourceQuantity -> {
                                Map<String, Object> resource = (Map<String, Object>) resourceQuantity.get("resource");
                                return Map.of(
                                        "resource", resource,
                                        "recipeName", recipe.get("name"),
                                        "quantity", resourceQuantity.get("quantity")
                                );
                            });
                })
                .collect(Collectors.groupingBy(
                        entry -> (String) ((Map<String, Object>) entry.get("resource")).get("name"),
                        Collectors.toList()
                ))
                .forEach((resourceName, entries) -> {
                    if (resourceName != null) {
                        String usedInRecipes = entries.stream()
                                .map(entry -> (String) entry.get("recipeName"))
                                .distinct()
                                .collect(Collectors.joining(", "));
                        double totalQuantity = entries.stream()
                                .mapToDouble(entry -> (Double) entry.get("quantity"))
                                .sum();
                        addTableRow(table, resourceName, usedInRecipes, String.valueOf(totalQuantity));
                    }
                });

        return table;
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(headerCell);
        }
    }

    private void addTableRow(PdfPTable table, String... values) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    public byte[] generateRecipeOverviewChart(List<Map<String, Object>> currentData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        currentData.forEach(entry -> {
            Map<String, Object> recipe = (Map<String, Object>) entry.get("recipe");
            List<Map<String, Object>> resources = (List<Map<String, Object>>) recipe.get("resourcesQuantities");
            String recipeName = (String) recipe.get("name");

            if (recipeName != null) {
                double totalQuantity = resources.stream()
                        .mapToDouble(resource -> resource.get("quantity") != null ? (double) resource.get("quantity") : 0.0)
                        .sum();
                dataset.addValue(totalQuantity, "Total Quantities", recipeName);
            }
        });

        JFreeChart chart = ChartFactory.createBarChart("Recipe Overview", "Recipe Name", "Total Quantities", dataset);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, java.awt.Color.BLUE);
        chart.removeLegend();

        return chartToByteArray(chart);
    }

    public byte[] generateResourceDependencyChart(List<Map<String, Object>> currentData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Long> resourceCounts = currentData.stream()
                .flatMap(entry -> {
                    Map<String, Object> recipe = (Map<String, Object>) entry.get("recipe");
                    List<Map<String, Object>> resourcesQuantities = (List<Map<String, Object>>) recipe.get("resourcesQuantities");
                    return resourcesQuantities.stream().map(resourceQuantity -> {
                        Map<String, Object> resource = (Map<String, Object>) resourceQuantity.get("resource");
                        return (String) resource.get("name");
                    });
                })
                .collect(Collectors.groupingBy(resourceName -> resourceName, Collectors.counting()));

        resourceCounts.forEach((resourceName, count) -> {
            dataset.setValue(resourceName, count);
        });

        JFreeChart chart = ChartFactory.createPieChart(
                "Resource Dependency",
                dataset,
                true,
                true,
                false
        );

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
