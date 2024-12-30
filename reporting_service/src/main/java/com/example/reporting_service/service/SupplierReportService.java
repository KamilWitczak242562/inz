package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class SupplierReportService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        return switch (reportType.toUpperCase()) {
            case "SUPPLIER_OVERVIEW" -> generateSupplierOverviewReport(supplierData, historicalData, isVisualization);
            case "SUPPLIER_RESOURCES" -> generateSupplierResourcesReport(supplierData, isVisualization);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private byte[] generateSupplierOverviewReport(List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateSupplierVisualization(supplierData);
        }
        return generatePdf("SUPPLIER_OVERVIEW", supplierData, null, visualization);
    }

    private byte[] generateSupplierResourcesReport(List<Map<String, Object>> supplierData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateSupplierVisualization(supplierData);
        }
        return generatePdf("SUPPLIER_RESOURCES", supplierData, null, visualization);
    }

    public byte[] generatePdf(String reportType, List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, byte[] visualization) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Supplier Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));

            document.add(new Paragraph(" "));

            if (visualization != null && visualization.length > 0) {
                document.add(new Paragraph("Visualization", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                Image chartImage = Image.getInstance(visualization);
                chartImage.scaleToFit(500, 400);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                document.add(new Paragraph(" "));
            }

            if (supplierData != null && !supplierData.isEmpty()) {
                if ("SUPPLIER_OVERVIEW".equalsIgnoreCase(reportType)) {
                    document.add(new Paragraph("Supplier Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                    document.add(createSupplierTable(supplierData));
                } else if ("SUPPLIER_RESOURCES".equalsIgnoreCase(reportType)) {
                    document.add(new Paragraph("Supplier Resources", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                    document.add(createSupplierResourceTable(supplierData));
                }
                document.add(new Paragraph(" "));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private PdfPTable createSupplierTable(List<Map<String, Object>> data) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.5f, 2f, 2f, 3f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting table widths", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Supplier ID", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Name", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Contact Info", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Address", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> supplier : data) {
            addCell(table, String.valueOf(supplier.get("supplierId")), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) supplier.get("name"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) supplier.get("contactInfo"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, (String) supplier.get("address"), cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
        }

        return table;
    }

    private PdfPTable createSupplierResourceTable(List<Map<String, Object>> data) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1.5f, 3.5f});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting column widths for supplier resource table", e);
        }

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        addCell(table, "Supplier", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
        addCell(table, "Resources", headerFont, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);

        for (Map<String, Object> supplier : data) {
            String supplierName = (String) supplier.get("name");
            List<Map<String, Object>> resources = (List<Map<String, Object>>) supplier.get("resources");

            StringBuilder resourceDetails = new StringBuilder();
            if (resources != null) {
                for (Map<String, Object> resource : resources) {
                    resourceDetails.append("Name: ").append(resource.get("name"))
                            .append(", Stock: ").append(resource.get("currentStock"))
                            .append(" ").append(resource.get("unit"))
                            .append("\n");
                }
            }

            addCell(table, supplierName, cellFont, Element.ALIGN_CENTER, BaseColor.WHITE);
            addCell(table, resourceDetails.toString().trim(), cellFont, Element.ALIGN_LEFT, BaseColor.WHITE);
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

    private byte[] generateSupplierVisualization(List<Map<String, Object>> supplierData) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map<String, Object> supplier : supplierData) {
            String name = (String) supplier.get("name");
            Integer resourcesCount = ((List<?>) supplier.get("resources")).size();
            dataset.setValue(name, resourcesCount);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Supplier Resources Overview",
                dataset,
                true,
                true,
                false
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating visualization", e);
        }
    }
}
