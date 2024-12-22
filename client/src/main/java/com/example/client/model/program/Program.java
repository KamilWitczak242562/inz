package com.example.client.model.program;

import lombok.Data;

import java.util.List;

@Data
public class Program {
    private Long programId;

    private String name;

    private List<Long> blockIds;
}
