DELETE
FROM dyeing_machines;
DELETE
FROM dryers;

ALTER TABLE dyeing_machines
    AUTO_INCREMENT = 1;
ALTER TABLE dryers
    AUTO_INCREMENT = 1;

DELETE
FROM dyeing_machines_aud;
DELETE
FROM dryers_aud;

ALTER TABLE dyeing_machines_aud
    AUTO_INCREMENT = 1;
ALTER TABLE dryers_aud
    AUTO_INCREMENT = 1;
