package com.example.reporting_service.controller;

import com.example.reporting_service.model.ReportRequest;
import com.example.reporting_service.service.ResourceReportService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/resources")
public class ResourceReportController {

    private final ResourceReportService resourceReportService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateResourceReport(@RequestBody ReportRequest request) {

        byte[] reportContent = resourceReportService.generateReport(
                request.getReportType(),
                request.getCurrentData(),
                request.getHistoricalData(),
                request.getIsVisualization()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=resource_report.pdf")
                .header("Content-Type", "application/pdf")
                .body(reportContent);
    }
}
