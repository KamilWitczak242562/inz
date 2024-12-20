package com.example.client.model.program;

import lombok.Data;

import java.util.List;

@Data
public class Block {
    private Long blockId;

    private List<Program> programs;

}
