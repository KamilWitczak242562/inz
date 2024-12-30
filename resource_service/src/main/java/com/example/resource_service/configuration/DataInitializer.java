package com.example.resource_service.configuration;

import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.ResourceRepo;
import com.example.resource_service.repository.SupplierRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final SupplierRepo supplierRepository;
    private final ResourceRepo resourceRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initSuppliers();
    }

    private void initSuppliers() {
        Resource resource1 = new Resource();
        resource1.setName("Cotton");
        resource1.setDescription("High-quality cotton");
        resource1.setCurrentStock(1000.0);
        resource1.setUnit("kg");
        resource1.setReorderLevel(200.0);
        resourceRepository.save(resource1);

        Resource resource2 = new Resource();
        resource2.setName("Polyester");
        resource2.setDescription("Durable polyester");
        resource2.setCurrentStock(500.0);
        resource2.setUnit("kg");
        resource2.setReorderLevel(100.0);
        resourceRepository.save(resource2);

        simulateModificationForResource(resource1);
        simulateModificationForResource(resource2);


        Supplier supplier1 = new Supplier();
        supplier1.setName("Textile Solutions");
        supplier1.setContactInfo("textile_solutions@example.com");
        supplier1.setAddress("45 Innovation Blvd, Springfield");
        supplier1.getResources().add(resource2);
        supplierRepository.save(supplier1);

        simulateModificationForSupplier(supplier1);

        Supplier supplier2 = new Supplier();
        supplier2.setName("Global Supplies");
        supplier2.setContactInfo("global_supplies@example.com");
        supplier2.setAddress("12 Elm Street, Shelbyville");
        supplier2.getResources().add(resource1);
        supplierRepository.save(supplier2);

        simulateModificationForSupplier(supplier2);
    }

    private void simulateModificationForSupplier(Supplier supplier) {
        supplier.setContactInfo(supplier.getContactInfo().replace("@example.com", "@test.com"));
        supplierRepository.save(supplier);
    }

    private void simulateModificationForResource(Resource resource) {
        resource.setCurrentStock(resource.getCurrentStock() - 50.0);
        resource.setReorderLevel(resource.getReorderLevel() + 50.0);
        resourceRepository.save(resource);
    }
}
