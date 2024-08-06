package com.example.resource_service.model;

import com.example.resource_service.model.enums.ScheduleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    private ProductionOrder productionOrder;

    private String task;
    private String startTime;
    private String endTime;
    private ScheduleStatus status;
}
