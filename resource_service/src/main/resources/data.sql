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
ALTER TABLE resources_aud AUTO_INCREMENT = 1;
ALTER TABLE suppliers_aud AUTO_INCREMENT = 1;

