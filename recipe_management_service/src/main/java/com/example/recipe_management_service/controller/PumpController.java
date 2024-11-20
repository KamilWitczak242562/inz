package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.Pump;
import com.example.recipe_management_service.service.PumpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pump")
@AllArgsConstructor
public class PumpController {
    private final PumpService pumpService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPumps() {
        List<Pump> pumpList = pumpService.getAll();
        if (pumpList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No pumps found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", pumpList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPumpById(@PathVariable Long id) {
        try {
            Pump pump = pumpService.getById(id);
            return ResponseEntity.ok(Map.of("response", pump, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createPump(@RequestBody Pump pump) {
        if (pump == null || pump.getRpm() == null || pump.getCirculationTimeInOut() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid Pump data", "ok", false));
        }
        Pump newPump = pumpService.create(pump);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newPump, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updatePump(@PathVariable Long id, @RequestBody Pump pump) {
        try {
            if (pump == null || pump.getRpm() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid Pump data", "ok", false));
            }
            Pump updatedPump = pumpService.update(id, pump);
            return ResponseEntity.ok(Map.of("response", updatedPump, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePump(@PathVariable Long id) {
        boolean isDeleted = pumpService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Pump deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Pump not found", "ok", false));
        }
    }
}
