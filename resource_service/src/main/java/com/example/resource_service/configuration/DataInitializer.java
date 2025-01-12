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
        resource1.setName("Cotton Fabric");
        resource1.setDescription("Premium cotton fabric for dyeing");
        resource1.setCurrentStock(1000.0);
        resource1.setUnit("kg");
        resource1.setReorderLevel(300.0);
        resourceRepository.save(resource1);

        simulateModificationForResource(resource1, 1100.0, null);
        simulateModificationForResource(resource1, 1150.0, null);
        simulateModificationForResource(resource1, 1200.0, null);

        Resource resource2 = new Resource();
        resource2.setName("Polyester Fabric");
        resource2.setDescription("Durable polyester fabric for dyeing");
        resource2.setCurrentStock(800.0);
        resource2.setUnit("kg");
        resource2.setReorderLevel(200.0);
        resourceRepository.save(resource2);

        simulateModificationForResource(resource2, 700.0, null);
        simulateModificationForResource(resource2, 750.0, null);
        simulateModificationForResource(resource2, null, 1200.0);

        Resource resource3 = new Resource();
        resource3.setName("Silk Fabric");
        resource3.setDescription("Luxurious silk fabric for dyeing");
        resource3.setCurrentStock(500.0);
        resource3.setUnit("kg");
        resource3.setReorderLevel(150.0);
        resourceRepository.save(resource3);

        simulateModificationForResource(resource3, 550.0, null);
        simulateModificationForResource(resource3, 580.0, null);
        simulateModificationForResource(resource3, 600.0, null);

        Resource resource4 = new Resource();
        resource4.setName("Nylon Fabric");
        resource4.setDescription("Synthetic nylon fabric for dyeing");
        resource4.setCurrentStock(1000.0);
        resource4.setUnit("kg");
        resource4.setReorderLevel(250.0);
        resourceRepository.save(resource4);

        simulateModificationForResource(resource4, 1050.0, null);

        Resource resource5 = new Resource();
        resource5.setName("Wool Fabric");
        resource5.setDescription("High-quality wool fabric for dyeing");
        resource5.setCurrentStock(500.0);
        resource5.setUnit("kg");
        resource5.setReorderLevel(100.0);
        resourceRepository.save(resource5);

        simulateModificationForResource(resource5, 550.0, null);

        Supplier supplier1 = new Supplier();
        supplier1.setName("Fabric World");
        supplier1.setContactInfo("contact@fabricworld.com");
        supplier1.setAddress("123 Textile Lane, DyeCity");
        addResourceToSupplier(supplier1, resource1);
        addResourceToSupplier(supplier1, resource2);
        supplierRepository.save(supplier1);

        simulateModificationForSupplier(supplier1, "contact@fabricworld.test.com");

        Supplier supplier2 = new Supplier();
        supplier2.setName("Silk Haven Supplies");
        supplier2.setContactInfo("sales@silkhaven.com");
        supplier2.setAddress("45 Silk Road, DyeCity");
        addResourceToSupplier(supplier2, resource3);
        addResourceToSupplier(supplier2, resource4);
        supplierRepository.save(supplier2);

        simulateModificationForSupplier(supplier2, "sales@silkhaven.test.com");

        Supplier supplier3 = new Supplier();
        supplier3.setName("Nylon Makers");
        supplier3.setContactInfo("contact@nylonmakers.com");
        supplier3.setAddress("789 Polymer Street, DyeCity");
        addResourceToSupplier(supplier3, resource4);
        addResourceToSupplier(supplier3, resource5);
        supplierRepository.save(supplier3);

        simulateModificationForSupplier(supplier3, "contact@nylonmakers.test.com");

        Supplier supplier4 = new Supplier();
        supplier4.setName("Wool Essentials Ltd.");
        supplier4.setContactInfo("support@woolessentials.com");
        supplier4.setAddress("321 Sheep Ave, DyeCity");
        addResourceToSupplier(supplier4, resource5);
        supplierRepository.save(supplier4);

        simulateModificationForSupplier(supplier4, "support@woolessentials.test.com");
    }

    private void simulateModificationForResource(Resource resource, Double newStock, Double newReorderLevel) {
        if (newStock != null) {
            resource.setCurrentStock(newStock);
        }
        if (newReorderLevel != null) {
            resource.setReorderLevel(newReorderLevel);
        }
        resourceRepository.save(resource);
    }

    private void simulateModificationForSupplier(Supplier supplier, String newContactInfo) {
        supplier.setContactInfo(newContactInfo);
        supplierRepository.save(supplier);
    }

    private void addResourceToSupplier(Supplier supplier, Resource resource) {
        if (!supplier.getResources().contains(resource)) {
            supplier.getResources().add(resource);
        }
    }
}


