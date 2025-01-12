SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM supplier_resource_aud;
DELETE FROM suppliers_aud;
DELETE FROM resources_aud;
DELETE FROM suppliers;
DELETE FROM resources;
SET FOREIGN_KEY_CHECKS = 1;


ALTER TABLE resources
    AUTO_INCREMENT = 1;
ALTER TABLE suppliers
    AUTO_INCREMENT = 1;
ALTER TABLE resources_aud AUTO_INCREMENT = 1;
ALTER TABLE suppliers_aud AUTO_INCREMENT = 1;

