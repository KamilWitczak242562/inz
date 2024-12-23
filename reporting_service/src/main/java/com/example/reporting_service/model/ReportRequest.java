package com.example.reporting_service.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportRequest {
    private String reportType;
    private Boolean isVisualization;
    private List<Map<String, Object>> currentData;
    private List<Map<String, Object>> historicalData;
}
