package com.example.reporting_service.controller;

import com.example.reporting_service.model.ReportRequest;
import com.example.reporting_service.service.ResourceReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/resource")
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
