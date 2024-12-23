package com.example.reporting_service.service;

import com.example.reporting_service.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupplierReportService {

    public byte[] generateReport(String reportType, List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        return switch (reportType.toUpperCase()) {
            case "SUPPLIER_OVERVIEW" -> generateSupplierOverviewReport(supplierData, historicalData, isVisualization);
            case "SUPPLIER_RESOURCES" -> generateSupplierResourcesReport(supplierData, historicalData, isVisualization);
            default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
        };
    }

    private byte[] generateSupplierOverviewReport(List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateSupplierVisualization(supplierData, historicalData);
        }
        return generatePdf("SUPPLIER_OVERVIEW", supplierData, historicalData, visualization);
    }

    private byte[] generateSupplierResourcesReport(List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, boolean isVisualization) {
        byte[] visualization = null;
        if (isVisualization) {
            visualization = generateSupplierVisualization(supplierData, historicalData);
        }
        return generatePdf("SUPPLIER_RESOURCES", supplierData, historicalData, visualization);
    }

    public byte[] generatePdf(String reportType, List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData, byte[] visualization) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(36, 36, 70, 36);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            document.add(new Paragraph("Supplier Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
            document.add(new Paragraph("Type: " + reportType, FontFactory.getFont(FontFactory.HELVETICA, 12)));

            if (historicalData != null && !historicalData.isEmpty()) {
                String dateRange = getDateRange(historicalData);
                document.add(new Paragraph("Date Range: " + dateRange, FontFactory.getFont(FontFactory.HELVETICA, 12)));
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

            if (supplierData != null && !supplierData.isEmpty()) {
                document.add(new Paragraph("Supplier Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(createSupplierTable(supplierData));
                document.add(new Paragraph(" "));
            }

            if (historicalData != null && !historicalData.isEmpty()) {
                document.add(new Paragraph("Historical Data", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(createHistoricalTable(historicalData));
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

        table.addCell("Supplier ID");
        table.addCell("Name");
        table.addCell("Contact Info");
        table.addCell("Address");

        for (Map<String, Object> supplier : data) {
            table.addCell(String.valueOf(supplier.get("supplierId")));
            table.addCell((String) supplier.get("name"));
            table.addCell((String) supplier.get("contactInfo"));
            table.addCell((String) supplier.get("address"));
        }

        return table;
    }

    private PdfPTable createHistoricalTable(List<Map<String, Object>> data) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        try {
            table.setWidths(new float[]{2, 2, 2, 2, 2});
        } catch (DocumentException e) {
            throw new RuntimeException("Error setting column widths for historical table", e);
        }

        table.addCell("Supplier ID");
        table.addCell("Name");
        table.addCell("Revision Type");
        table.addCell("Revision Date");

        for (Map<String, Object> history : data) {
            table.addCell(String.valueOf(history.get("supplierId")));
            table.addCell((String) history.get("name"));
            table.addCell((String) history.get("revisionType"));
            table.addCell(formatDate((String) history.get("revisionDate")));
        }

        return table;
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

    private String formatDate(String date) {
        try {
            FastDateFormat inputFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
            FastDateFormat outputFormat = FastDateFormat.getInstance("dd-MM-yyyy HH:mm:ss");
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            return date;
        }
    }

    private byte[] generateSupplierVisualization(List<Map<String, Object>> supplierData, List<Map<String, Object>> historicalData) {
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
