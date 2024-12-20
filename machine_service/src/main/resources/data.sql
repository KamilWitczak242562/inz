DELETE
FROM dyeing_machines;
DELETE
FROM dryers;

ALTER TABLE dyeing_machines
    AUTO_INCREMENT = 1;
ALTER TABLE dryers
    AUTO_INCREMENT = 1;

INSERT INTO dyeing_machines (machine_id, name, state, start_work, end_work, capacity, charge_diameter)
VALUES (1, 'Dyeing1', 'IDLE', '2024-08-10 08:00:00', '2024-08-10 16:00:00', 500, 120),
       (2, 'Dyeing2', 'WORKING', '2024-08-11 09:00:00', NULL, 600, 140),
       (3, 'Dyeing3', 'ERROR', '2024-08-12 10:00:00', NULL, 450, 100);

INSERT INTO dryers (machine_id, name, state, start_work, end_work, capacity, dryer_type)
VALUES (1, 'Dryer1', 'WAITING_FOR_ACTION', '2024-08-13 11:00:00', NULL, 350, 'PRESSURE'),
       (2, 'Dryer2', 'IDLE', '2024-08-14 12:00:00', '2024-08-14 18:00:00', 400, 'HIGH_FREQUENCY'),
       (3, 'Dryer3', 'WORKING', '2024-08-15 13:00:00', NULL, 500, 'PRESSURE');
