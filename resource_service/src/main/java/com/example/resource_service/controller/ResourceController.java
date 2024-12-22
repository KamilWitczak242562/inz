package com.example.resource_service.controller;

import com.example.resource_service.model.Resource;
import com.example.resource_service.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().body(Map.of("response", resourceList,
                "ok", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getById(id);
        return ResponseEntity.ok().body(Map.of("response", resource,
                "ok", true));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createResource(@RequestBody Resource resource) {
        Resource newResource = resourceService.create(resource);
        return ResponseEntity.ok().body(Map.of("response", newResource,
                "ok", true));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Resource updatedResource = resourceService.update(id, resource);
        return ResponseEntity.ok().body(Map.of("response", updatedResource,
                "ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        boolean isDeleted = resourceService.delete(id);
        return ResponseEntity.ok().body(Map.of("response", isDeleted,
                "ok", true));
    }
}
