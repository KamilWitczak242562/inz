package com.example.resource_service.controller;

import com.example.resource_service.model.Resource;
import com.example.resource_service.service.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/resource")
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllResources() {
        List<Resource> resourceList = resourceService.getAll();
        return ResponseEntity.ok(resourceList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getById(id);
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createResource(@RequestBody Resource resource) {
        Resource newResource = resourceService.create(resource);
        return ResponseEntity.ok(newResource);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Resource updatedResource = resourceService.update(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        boolean isDeleted = resourceService.delete(id);
        return ResponseEntity.ok(isDeleted);
    }
}
