package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ProgramReportService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;

        switch (reportType.toUpperCase()) {
            case "PROGRAM_OVERVIEW" -> {
                if (isVisualization) {
                    visualization = generatePieChartForProgramOverview(currentData, historicalData);
                }
                return generatePdf(reportType, currentData, historicalData, visualization, this::createProgramOverviewTable);
            }
            case "BLOCK_USAGE" -> {
                if (isVisualization) {
                    visualization = generatePieChartForBlockUsage(currentData, historicalData);
                }
                return generatePdf(reportType, currentData, historicalData, visualization, this::createBlockUsageTable);
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

            document.add(new Paragraph("Program Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            String description = switch (reportType.toUpperCase()) {
                case "PROGRAM_OVERVIEW" -> "Overview of all programs.";
                case "BLOCK_USAGE" -> "Analysis of block usage in programs.";
                default -> "Unknown report type.";
            };
            document.add(new Paragraph("Description: " + description, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            if (visualization != null) {
                document.add(new Paragraph("Visualization", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph(" "));
                Image chartImage = Image.getInstance(visualization);
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
            }

            if (currentData != null && !currentData.isEmpty()) {
                document.add(new Paragraph("Current Program Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph(" "));
                document.add(tableGenerator.generateTable(currentData, historicalData));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private PdfPTable createProgramOverviewTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addTableHeader(table, "Program Name", "Number of Blocks", "Block Types");

        currentData.forEach(entry -> {
            Map<String, Object> program = (Map<String, Object>) entry.get("program");
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) program.get("blocks");
            String programName = (String) program.get("name");

            String blockTypes = blocks.stream()
                    .map(block -> (String) block.get("dtype"))
                    .distinct()
                    .collect(Collectors.joining(", "));

            addTableRow(table, programName, String.valueOf(blocks.size()), blockTypes);
        });

        return table;
    }

    private PdfPTable createBlockUsageTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addTableHeader(table, "Block Type", "Used In Programs", "Number of Blocks");

        currentData.stream()
                .flatMap(entry -> {
                    Map<String, Object> program = (Map<String, Object>) entry.get("program");
                    if (program == null || program.get("blocks") == null) {
                        return Stream.empty();
                    }

                    List<Map<String, Object>> blocks = (List<Map<String, Object>>) program.get("blocks");

                    return blocks.stream().map(block -> {
                        String blockType = (String) block.get("dtype");
                        return Map.of(
                                "blockType", blockType,
                                "programName", program.get("name")
                        );
                    });
                })
                .collect(Collectors.groupingBy(
                        entry -> (String) entry.get("blockType"),
                        Collectors.mapping(entry -> (String) entry.get("programName"), Collectors.toSet())
                ))
                .forEach((blockType, programs) -> {
                    String usedInPrograms = String.join(", ", programs);
                    addTableRow(table, blockType, usedInPrograms, String.valueOf(programs.size()));
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

    private byte[] generatePieChartForProgramOverview(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        currentData.forEach(entry -> {
            Map<String, Object> program = (Map<String, Object>) entry.get("program");
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) program.get("blocks");

            int blockCount = blocks.size();
            dataset.setValue(program.get("name") + " (" + blockCount + " blocks)", blockCount);
        });

        JFreeChart chart = ChartFactory.createPieChart(
                "Program Overview",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));

        return chartToByteArray(chart);
    }






    private byte[] generatePieChartForBlockUsage(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        currentData.stream()
                .flatMap(entry -> {
                    Map<String, Object> program = (Map<String, Object>) entry.get("program");
                    List<Map<String, Object>> blocks = (List<Map<String, Object>>) program.get("blocks");
                    return blocks.stream().map(block -> (String) block.get("dtype"));
                })
                .collect(Collectors.groupingBy(blockType -> blockType, Collectors.counting()))
                .forEach((blockType, count) -> dataset.setValue(blockType, count));

        JFreeChart chart = ChartFactory.createPieChart(
                "Block Usage",
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

    private interface TableGenerator {
        PdfPTable generateTable(List<Map<String, Object>> currentData, List<Map<String, Object>> historicalData);
    }
}

