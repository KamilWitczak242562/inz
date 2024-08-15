package com.example.resource_service.service;

import com.example.resource_service.model.Resource;
import com.example.resource_service.repository.ResourceRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepo resourceRepo;

    @InjectMocks
    private ResourceService resourceService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        resourceService = new ResourceService(resourceRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreate() {
        Resource resource = new Resource();
        when(resourceRepo.save(resource)).thenReturn(resource);

        Resource result = resourceService.create(resource);

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    void testDeleteSuccess() {
        when(resourceRepo.existsById(1L)).thenReturn(true);

        boolean result = resourceService.delete(1L);

        assertTrue(result);
        verify(resourceRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFailure() {
        when(resourceRepo.existsById(1L)).thenReturn(false);

        boolean result = resourceService.delete(1L);

        assertFalse(result);
        verify(resourceRepo, never()).deleteById(1L);
    }

    @Test
    void testUpdate() {
        Resource existingResource = new Resource();
        Resource updatedResource = new Resource();
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(existingResource));
        when(resourceRepo.save(updatedResource)).thenReturn(updatedResource);

        Resource result = resourceService.update(1L, updatedResource);

        assertNotNull(result);
        assertEquals(updatedResource, result);
    }

    @Test
    void testGetById() {
        Resource resource = new Resource();
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource));

        Resource result = resourceService.getById(1L);

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    void testGetAll() {
        Resource resource1 = new Resource();
        Resource resource2 = new Resource();
        when(resourceRepo.findAll()).thenReturn(Arrays.asList(resource1, resource2));

        List<Resource> result = resourceService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(resource1));
        assertTrue(result.contains(resource2));
    }
}
