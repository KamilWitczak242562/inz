package com.example.machine_service.controller;

import com.example.machine_service.model.Dryer;
import com.example.machine_service.service.DryerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dryer")
@AllArgsConstructor
public class DryerController {
    private final DryerService dryerService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDryers() {
        List<Dryer> dryerList = dryerService.getAll();
        return ResponseEntity.ok().body(Map.of("response", dryerList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDryerById(@PathVariable Long id) {
        Dryer dryer = dryerService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", dryer, "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createDryer(@RequestBody Dryer dryer) {
        Dryer newDryer = dryerService.create(dryer);
        return ResponseEntity.ok().body(Map.of("response", newDryer, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateDryer(@PathVariable Long id, @RequestBody Dryer dryer) {
        Dryer updatedDryer = dryerService.update(id, dryer);
        return ResponseEntity.ok().body(Map.of("response", updatedDryer, "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDryer(@PathVariable Long id) {
        boolean isDeleted = dryerService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted, "ok", true));
    }
}
