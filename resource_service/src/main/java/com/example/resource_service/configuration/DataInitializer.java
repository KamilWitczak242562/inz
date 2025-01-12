package com.example.resource_service.configuration;

import com.example.resource_service.model.Resource;
import com.example.resource_service.model.Supplier;
import com.example.resource_service.repository.ResourceRepo;
import com.example.resource_service.repository.SupplierRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final SupplierRepo supplierRepository;
    private final ResourceRepo resourceRepository;

    @Override
    public void run(String... args) {
        initResources();
        initSuppliers();
    }

    @Transactional
    public void initResources() {
        List<Resource> resources = Arrays.asList(
                createResource("Cotton Fabric", "Premium cotton fabric for dyeing", 1000.0, "kg", 300.0),
                createResource("Polyester Fabric", "Durable polyester fabric for dyeing", 800.0, "kg", 200.0),
                createResource("Silk Fabric", "Luxurious silk fabric for dyeing", 500.0, "kg", 150.0),
                createResource("Nylon Fabric", "Synthetic nylon fabric for dyeing", 1000.0, "kg", 250.0),
                createResource("Wool Fabric", "High-quality wool fabric for dyeing", 500.0, "kg", 100.0)
        );

        resources.forEach(resource -> {
            resourceRepository.save(resource);
            resourceRepository.flush();
            performResourceUpdates(resource, Arrays.asList(1100.0, 1150.0, 1200.0)); // Lista zmian
        });
    }

    @Transactional
    public void initSuppliers() {
        List<Resource> resources = resourceRepository.findAll();
        Supplier supplier1 = createSupplier("Fabric World", "contact@fabricworld.com", "123 Textile Lane, DyeCity",
                resources.subList(0, 2));
        Supplier supplier2 = createSupplier("Silk Haven Supplies", "sales@silkhaven.com", "45 Silk Road, DyeCity",
                resources.subList(2, 4));
        Supplier supplier3 = createSupplier("Nylon Makers", "contact@nylonmakers.com", "789 Polymer Street, DyeCity",
                resources.subList(3, 5));
        Supplier supplier4 = createSupplier("Wool Essentials Ltd.", "support@woolessentials.com", "321 Sheep Ave, DyeCity",
                resources.subList(4, 5));

        List<Supplier> suppliers = Arrays.asList(supplier1, supplier2, supplier3, supplier4);

        suppliers.forEach(supplier -> {
            supplierRepository.save(supplier);
            supplierRepository.flush();
            performSupplierUpdates(supplier, supplier.getContactInfo().replace(".com", ".test.com"));
        });
    }

    private Resource createResource(String name, String description, Double currentStock, String unit, Double reorderLevel) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setDescription(description);
        resource.setCurrentStock(currentStock);
        resource.setUnit(unit);
        resource.setReorderLevel(reorderLevel);
        return resource;
    }

    private Supplier createSupplier(String name, String contactInfo, String address, List<Resource> resources) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactInfo(contactInfo);
        supplier.setAddress(address);
        supplier.getResources().addAll(resources);
        return supplier;
    }

    @Transactional
    public void performResourceUpdates(Resource resource, List<Double> stockChanges) {
        stockChanges.forEach(stock -> {
            resource.setCurrentStock(stock);
            resourceRepository.save(resource);
        });
    }

    @Transactional
    public void performSupplierUpdates(Supplier supplier, String newContactInfo) {
        supplier.setContactInfo(newContactInfo);
        supplierRepository.save(supplier);
    }
}
