package com.example.resource_service.service;

import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.SupplierRepo;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepo supplierRepo;

    @InjectMocks
    private SupplierService supplierService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        supplierService = new SupplierService(supplierRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreate() {
        Supplier supplier = new Supplier();
        when(supplierRepo.save(supplier)).thenReturn(supplier);

        Supplier result = supplierService.create(supplier);

        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testDeleteSuccess() {
        when(supplierRepo.existsById(1L)).thenReturn(true);

        boolean result = supplierService.delete(1L);

        assertTrue(result);
        verify(supplierRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFailure() {
        when(supplierRepo.existsById(1L)).thenReturn(false);

        boolean result = supplierService.delete(1L);

        assertFalse(result);
        verify(supplierRepo, never()).deleteById(1L);
    }

    @Test
    void testUpdate() {
        Supplier existingSupplier = new Supplier();
        Supplier updatedSupplier = new Supplier();
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepo.save(updatedSupplier)).thenReturn(updatedSupplier);

        Supplier result = supplierService.update(1L, updatedSupplier);

        assertNotNull(result);
        assertEquals(updatedSupplier, result);
    }

    @Test
    void testGetById() {
        Supplier supplier = new Supplier();
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));

        Supplier result = supplierService.getById(1L);

        assertNotNull(result);
        assertEquals(supplier, result);
    }

    @Test
    void testGetAll() {
        Supplier supplier1 = new Supplier();
        Supplier supplier2 = new Supplier();
        when(supplierRepo.findAll()).thenReturn(Arrays.asList(supplier1, supplier2));

        List<Supplier> result = supplierService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(supplier1));
        assertTrue(result.contains(supplier2));
    }
}
