package com.example.recipe_management_service.controller;

import com.example.recipe_management_service.model.MainTank;
import com.example.recipe_management_service.service.MainTankService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mainTank")
@AllArgsConstructor
public class MainTankController {
    private final MainTankService mainTankService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllMainTanks() {
        List<MainTank> mainTankList = mainTankService.getAll();
        if (mainTankList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No MainTanks found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", mainTankList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMainTankById(@PathVariable Long id) {
        try {
            MainTank mainTank = mainTankService.getById(id);
            return ResponseEntity.ok(Map.of("response", mainTank, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createMainTank(@RequestBody MainTank mainTank) {
        if (mainTank == null || mainTank.getFillLevel() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid MainTank data", "ok", false));
        }
        MainTank newMainTank = mainTankService.create(mainTank);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newMainTank, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateMainTank(@PathVariable Long id, @RequestBody MainTank mainTank) {
        try {
            if (mainTank == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid MainTank data", "ok", false));
            }
            MainTank updatedMainTank = mainTankService.update(id, mainTank);
            return ResponseEntity.ok(Map.of("response", updatedMainTank, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMainTank(@PathVariable Long id) {
        boolean isDeleted = mainTankService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "MainTank deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "MainTank not found", "ok", false));
        }
    }
}
