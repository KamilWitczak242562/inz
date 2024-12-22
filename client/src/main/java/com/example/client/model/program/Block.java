package com.example.client.model.program;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dtype"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MainTank.class, name = "MainTank"),
        @JsonSubTypes.Type(value = SecondaryTank.class, name = "SecondaryTank"),
        @JsonSubTypes.Type(value = Pump.class, name = "Pump")
})
public abstract class Block {
    private Long blockId;

    private String dtype;

    public abstract void updatePropertiesFrom(Block block);
}
