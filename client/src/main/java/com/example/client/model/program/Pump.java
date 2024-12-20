package com.example.client.model.program;

import lombok.Data;

@Data
public class Pump extends Block{
    private Integer rpm;
    private Integer circulationTimeInOut;
    private Integer circulationTimeOutIn;

}
