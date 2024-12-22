package com.example.recipe_management_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("MainTank")
public class MainTank extends Block {
    private Double fillLevel;
    private Boolean isHotWater;
    private Double targetTemperature;
    private Double temperatureIncreaseRate;
    private Integer holdTemperatureTime;
    private Boolean isDrainActive;

    public void updatePropertiesFrom(Block block) {
        if (block instanceof MainTank mainTank) {
            this.fillLevel = mainTank.getFillLevel();
            this.isHotWater = mainTank.getIsHotWater();
            this.targetTemperature = mainTank.getTargetTemperature();
            this.temperatureIncreaseRate = mainTank.getTemperatureIncreaseRate();
            this.holdTemperatureTime = mainTank.getHoldTemperatureTime();
            this.isDrainActive = mainTank.getIsDrainActive();
        }
    }
}
