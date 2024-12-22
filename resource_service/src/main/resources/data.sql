DELETE
FROM suppliers_resources;
DELETE
FROM resources;
DELETE
FROM suppliers;

DELETE
FROM resources_aud;
DELETE
FROM suppliers_aud;

ALTER TABLE resources
    AUTO_INCREMENT = 1;
ALTER TABLE suppliers
    AUTO_INCREMENT = 1;
ALTER TABLE suppliers_resources
    AUTO_INCREMENT = 1;
ALTER TABLE resources_aud AUTO_INCREMENT = 1;
ALTER TABLE suppliers_aud AUTO_INCREMENT = 1;

INSERT INTO resources (name, description, current_stock, unit, reorder_level)
VALUES ('Blue Dye', 'High-quality blue dye for fabrics', 500.0, 'l', 50.0),
       ('Water', 'Filtered water for dyeing processes', 1000.0, 'l', 200.0),
       ('Red Dye', 'High-quality red dye for fabrics', 300.0, 'l', 30.0),
       ('Restock resource', 'just testing purpose', 20.0, 'kg', 60.0);

INSERT INTO suppliers (name, contact_info, address)
VALUES ('Supplier A', '123-456-7890', '123 Supplier Street, Supplier City'),
       ('Supplier B', '098-765-4321', '456 Supplier Avenue, Supplier Town');

INSERT INTO suppliers_resources (supplier_supplier_id, resources_resource_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (2, 4);
