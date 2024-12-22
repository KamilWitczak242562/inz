package com.example.reporting_service.model;

import lombok.Data;

import java.util.Map;

@Data
public class ReportRequest {
    private String reportType;
    private Map<String, Object> data;
    private String format;
}
