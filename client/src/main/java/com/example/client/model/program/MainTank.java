package com.example.client.model.program;

import lombok.Data;

@Data
public class MainTank extends Block{
    private Double fillLevel;
    private Boolean isHotWater;
    private Double targetTemperature;
    private Double temperatureIncreaseRate;
    private Integer holdTemperatureTime;
    private Boolean isDrainActive;

}