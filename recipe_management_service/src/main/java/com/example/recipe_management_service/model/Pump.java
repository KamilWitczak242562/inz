package com.example.recipe_management_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("Pump")
public class Pump extends Block{
    private Integer rpm;
    private Integer circulationTimeInOut;
    private Integer circulationTimeOutIn;
    private String type = "pump";

    public void updatePropertiesFrom(Block block) {
        if (block instanceof Pump pump) {
            this.rpm = pump.getRpm();
            this.circulationTimeInOut = pump.getCirculationTimeInOut();
            this.circulationTimeOutIn = pump.getCirculationTimeOutIn();
        }
    }

}
