package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.Block;
import com.example.recipe_management_service.service.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blocks")
@AllArgsConstructor
public class BlockController {
    private final BlockService blockService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllBlocks() {
        List<Block> blockList = blockService.getAll();
        if (blockList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No blocks found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", blockList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlockById(@PathVariable Long id) {
        try {
            Block block = blockService.getById(id);
            return ResponseEntity.ok(Map.of("response", block, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createBlock(@RequestBody Block block) {
        if (block == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid Block data", "ok", false));
        }
        Block newBlock = blockService.create(block);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newBlock, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateBlock(@PathVariable Long id, @RequestBody Block block) {
        try {
            Block updatedBlock = blockService.update(id, block);
            return ResponseEntity.ok(Map.of("response", updatedBlock, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlock(@PathVariable Long id) {
        boolean isDeleted = blockService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Block deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Block not found", "ok", false));
        }
    }
}
