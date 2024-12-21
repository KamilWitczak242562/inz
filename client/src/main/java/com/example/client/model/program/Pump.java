package com.example.client.model.program;

import lombok.Data;

@Data
public class Pump extends Block{
    private Integer rpm;
    private Integer circulationTimeInOut;
    private Integer circulationTimeOutIn;

    public void updatePropertiesFrom(Block block) {
        if (block instanceof Pump pump) {
            this.rpm = pump.getRpm();
            this.circulationTimeInOut = pump.getCirculationTimeInOut();
            this.circulationTimeOutIn = pump.getCirculationTimeOutIn();
        }
    }

}
