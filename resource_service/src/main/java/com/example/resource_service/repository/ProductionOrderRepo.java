package com.example.resource_service.repository;

import com.example.resource_service.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderRepo extends JpaRepository<ProductionOrder, Long> {
}
