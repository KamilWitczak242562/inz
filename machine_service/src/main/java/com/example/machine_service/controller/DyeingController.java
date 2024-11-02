package com.example.machine_service.controller;

import com.example.machine_service.model.Dyeing;
import com.example.machine_service.service.DyeingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dyeing")
@AllArgsConstructor
public class DyeingController {
    private final DyeingService dyeingService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDyeings() {
        List<Dyeing> dyeingList = dyeingService.getAll();
        return ResponseEntity.ok().body(Map.of("response", dyeingList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDyeingById(@PathVariable Long id) {
        Dyeing dyeing = dyeingService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", dyeing, "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createDyeing(@RequestBody Dyeing dyeing) {
        Dyeing newDyeing = dyeingService.create(dyeing);
        return ResponseEntity.ok().body(Map.of("response", newDyeing, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateDyeing(@PathVariable Long id, @RequestBody Dyeing dyeing) {
        Dyeing updatedDyeing = dyeingService.update(id, dyeing);
        return ResponseEntity.ok().body(Map.of("response", updatedDyeing, "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDyeing(@PathVariable Long id) {
        boolean isDeleted = dyeingService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted, "ok", true));
    }
}
