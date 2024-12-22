package com.example.recipe_management_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Data
@DiscriminatorValue("Pump")
@Audited
public class Pump extends Block {
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
