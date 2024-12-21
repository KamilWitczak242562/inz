package com.example.recipe_management_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("SecondaryTank")
public class SecondaryTank extends Block {
    private Double fillLevel;
    private Boolean isHotWater;
    private Double targetTemperature;
    private Double temperatureIncreaseRate;
    private Integer holdTemperatureTime;
    private Boolean isDrainActive;

    private Boolean isMixerActive;
    private Double chemicalDose;
    private Double dyeDose;

    public void updatePropertiesFrom(Block block) {
        if (block instanceof SecondaryTank secondaryTank) {
            this.fillLevel = secondaryTank.getFillLevel();
            this.isHotWater = secondaryTank.getIsHotWater();
            this.targetTemperature = secondaryTank.getTargetTemperature();
            this.temperatureIncreaseRate = secondaryTank.getTemperatureIncreaseRate();
            this.holdTemperatureTime = secondaryTank.getHoldTemperatureTime();
            this.isDrainActive = secondaryTank.getIsDrainActive();
            this.isMixerActive = secondaryTank.getIsMixerActive();
            this.chemicalDose = secondaryTank.getChemicalDose();
            this.dyeDose = secondaryTank.getDyeDose();
        }
    }

}
