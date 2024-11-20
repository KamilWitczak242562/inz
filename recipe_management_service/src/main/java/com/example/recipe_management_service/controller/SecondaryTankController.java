package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.SecondaryTank;
import com.example.recipe_management_service.service.SecondaryTankService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/secondaryTank")
@AllArgsConstructor
public class SecondaryTankController {
    private final SecondaryTankService secondaryTankService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllSecondaryTanks() {
        List<SecondaryTank> secondaryTankList = secondaryTankService.getAll();
        if (secondaryTankList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No secondary tanks found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", secondaryTankList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSecondaryTankById(@PathVariable Long id) {
        try {
            SecondaryTank secondaryTank = secondaryTankService.getById(id);
            return ResponseEntity.ok(Map.of("response", secondaryTank, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createSecondaryTank(@RequestBody SecondaryTank secondaryTank) {
        if (secondaryTank == null || secondaryTank.getFillLevel() == null || secondaryTank.getTargetTemperature() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid SecondaryTank data", "ok", false));
        }
        SecondaryTank newSecondaryTank = secondaryTankService.create(secondaryTank);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newSecondaryTank, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateSecondaryTank(@PathVariable Long id, @RequestBody SecondaryTank secondaryTank) {
        try {
            if (secondaryTank == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid SecondaryTank data", "ok", false));
            }
            SecondaryTank updatedSecondaryTank = secondaryTankService.update(id, secondaryTank);
            return ResponseEntity.ok(Map.of("response", updatedSecondaryTank, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSecondaryTank(@PathVariable Long id) {
        boolean isDeleted = secondaryTankService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "SecondaryTank deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "SecondaryTank not found", "ok", false));
        }
    }
}
