package com.example.resource_service.service;

import com.example.resource_service.model.ProductionOrder;
import com.example.resource_service.repository.ProductionOrderRepo;
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
class ProductionOrderServiceTest {

    @Mock
    private ProductionOrderRepo productionOrderRepo;

    @InjectMocks
    private ProductionOrderService productionOrderService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        productionOrderService = new ProductionOrderService(productionOrderRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreate() {
        ProductionOrder order = new ProductionOrder();
        when(productionOrderRepo.save(order)).thenReturn(order);

        ProductionOrder result = productionOrderService.create(order);

        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    void testDeleteSuccess() {
        when(productionOrderRepo.existsById(1L)).thenReturn(true);

        boolean result = productionOrderService.delete(1L);

        assertTrue(result);
        verify(productionOrderRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteFailure() {
        when(productionOrderRepo.existsById(1L)).thenReturn(false);

        boolean result = productionOrderService.delete(1L);

        assertFalse(result);
        verify(productionOrderRepo, never()).deleteById(1L);
    }

    @Test
    void testUpdate() {
        ProductionOrder existingOrder = new ProductionOrder();
        ProductionOrder updatedOrder = new ProductionOrder();
        when(productionOrderRepo.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(productionOrderRepo.save(updatedOrder)).thenReturn(updatedOrder);

        ProductionOrder result = productionOrderService.update(1L, updatedOrder);

        assertNotNull(result);
        assertEquals(updatedOrder, result);
    }

    @Test
    void testGetById() {
        ProductionOrder order = new ProductionOrder();
        when(productionOrderRepo.findById(1L)).thenReturn(Optional.of(order));

        ProductionOrder result = productionOrderService.getById(1L);

        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    void testGetAll() {
        ProductionOrder order1 = new ProductionOrder();
        ProductionOrder order2 = new ProductionOrder();
        when(productionOrderRepo.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<ProductionOrder> result = productionOrderService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
    }
}
