package com.example.recipe_management_service.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class MainTank extends Block{
    private Double fillLevel;
    private Boolean isHotWater;
    private Double targetTemperature;
    private Double temperatureIncreaseRate;
    private Integer holdTemperatureTime;
    private Boolean isDrainActive;

}
