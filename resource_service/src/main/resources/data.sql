DELETE
FROM suppliers_resources;
DELETE
FROM recipe_resources;
DELETE
FROM resources;
DELETE
FROM recipes;
DELETE
FROM suppliers;
DELETE
FROM notifications;
DELETE
FROM production_orders;

ALTER TABLE recipe_resources
    AUTO_INCREMENT = 1;
ALTER TABLE resources
    AUTO_INCREMENT = 1;
ALTER TABLE recipes
    AUTO_INCREMENT = 1;
ALTER TABLE suppliers
    AUTO_INCREMENT = 1;
ALTER TABLE suppliers_resources
    AUTO_INCREMENT = 1;
ALTER TABLE production_orders
    AUTO_INCREMENT = 1;
ALTER TABLE notifications
    AUTO_INCREMENT = 1;

INSERT INTO notifications (timestamp, type, message)
VALUES ('2024-08-10T10:00:00', 'INFO', 'System started successfully'),
       ('2024-08-11T11:30:00', 'ERROR', 'Failed to connect to database'),
       ('2024-08-12T14:45:00', 'WARNING', 'Low disk space on server');

INSERT INTO production_orders (machine_id, description, start_date, end_date, status)
VALUES (101, 'Order for product A', '2024-08-01', '2024-08-05', 'PENDING'),
       (102, 'Order for product B', '2024-08-06', '2024-08-10', 'COMPLETED'),
       (103, 'Order for product C', '2024-08-11', '2024-08-15', 'IN_PROGRESS');

INSERT INTO resources (name, description, current_stock, unit, reorder_level)
VALUES ('Blue Dye', 'High-quality blue dye for fabrics', 500.0, 'l', 50.0),
       ('Water', 'Filtered water for dyeing processes', 1000.0, 'l', 200.0),
       ('Red Dye', 'High-quality red dye for fabrics', 300.0, 'l', 30.0);

INSERT INTO suppliers (name, contact_info, address)
VALUES ('Supplier A', '123-456-7890', '123 Supplier Street, Supplier City'),
       ('Supplier B', '098-765-4321', '456 Supplier Avenue, Supplier Town');

INSERT INTO suppliers_resources (supplier_supplier_id, resources_resource_id)
VALUES (1, 1),
       (1, 2),
       (2, 3);
