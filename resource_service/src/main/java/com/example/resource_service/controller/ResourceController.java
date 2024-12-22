package com.example.resource_service.controller;

import com.example.resource_service.model.Resource;
import com.example.resource_service.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllResources() {
        List<Resource> resourceList = resourceService.getAll();
        if (resourceList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("response", "No resources found", "ok", false));
        }
        return ResponseEntity.ok(Map.of("response", resourceList, "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable Long id) {
        try {
            Resource resource = resourceService.getById(id);
            return ResponseEntity.ok(Map.of("response", resource, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createResource(@RequestBody Resource resource) {
        if (resource == null || resource.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", "Invalid resource data", "ok", false));
        }
        Resource newResource = resourceService.create(resource);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("response", newResource, "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        try {
            if (resource == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("response", "Invalid resource data", "ok", false));
            }
            Resource updatedResource = resourceService.update(id, resource);
            return ResponseEntity.ok(Map.of("response", updatedResource, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        boolean isDeleted = resourceService.delete(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("response", "Resource deleted successfully", "ok", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("response", "Resource not found", "ok", false));
        }
    }

    @GetMapping("/history/{startTime}&{endTime}")
    public ResponseEntity<?> getResourceHistory(
            @PathVariable LocalDateTime startTime,
            @PathVariable LocalDateTime endTime,
            @RequestParam(defaultValue = "ALL") String revisionType) {
        try {
            List<Map<String, Object>> history = resourceService.getHistory(startTime, endTime, revisionType);
            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("response", "No history found", "ok", false));
            }
            return ResponseEntity.ok(Map.of("response", history, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("response", e.getMessage(), "ok", false));
        }
    }

}
