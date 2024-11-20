package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.Block;
import com.example.recipe_management_service.model.Program;
import com.example.recipe_management_service.service.ProgramService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/program")
@AllArgsConstructor
public class ProgramController {
    private final ProgramService programService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPrograms() {
        List<Program> programList = programService.getAll();
        if (programList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No programs found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", programList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProgramById(@PathVariable Long id) {
        try {
            Program program = programService.getById(id);
            return ResponseEntity.ok(Map.of("response", program, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createProgram(@RequestBody Program program) {
        if (program == null || program.getName() == null || program.getBlocks() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid Program data", "ok", false));
        }
        Program newProgram = programService.create(program);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newProgram, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateProgram(@PathVariable Long id, @RequestBody Program program) {
        try {
            if (program == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid Program data", "ok", false));
            }
            Program updatedProgram = programService.update(id, program);
            return ResponseEntity.ok(Map.of("response", updatedProgram, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProgram(@PathVariable Long id) {
        boolean isDeleted = programService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Program deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Program not found", "ok", false));
        }
    }

    @PostMapping("/{programId}/addBlock")
    public ResponseEntity<?> addBlockToProgram(@PathVariable Long programId, @RequestBody Block block) {
        try {
            if (block == null || block.getBlockId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid Block data", "ok", false));
            }
            programService.addBlockToProgram(programId, block);
            return ResponseEntity.ok(Map.of("response", "Block added successfully", "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{programId}/removeBlock/{blockId}")
    public ResponseEntity<?> removeBlockFromProgram(@PathVariable Long programId, @PathVariable Long blockId) {
        try {
            programService.removeBlockFromProgram(programId, blockId);
            return ResponseEntity.ok(Map.of("response", "Block removed successfully", "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }
}
