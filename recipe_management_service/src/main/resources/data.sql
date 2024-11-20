DELETE FROM recipe_resources;
DELETE FROM program_block;

DELETE FROM programs;
DELETE FROM recipes;
DELETE FROM main_tank;
DELETE FROM secondary_tank;
DELETE FROM pump;
DELETE FROM blocks;

ALTER TABLE blocks AUTO_INCREMENT = 1;
ALTER TABLE main_tank AUTO_INCREMENT = 1;
ALTER TABLE secondary_tank AUTO_INCREMENT = 1;
ALTER TABLE pump AUTO_INCREMENT = 1;
ALTER TABLE programs AUTO_INCREMENT = 1;
ALTER TABLE recipes AUTO_INCREMENT = 1;
ALTER TABLE program_block AUTO_INCREMENT = 1;
ALTER TABLE recipe_resources AUTO_INCREMENT = 1;


INSERT INTO blocks (block_id)
VALUES (1), (2), (3), (4), (5), (6), (7);

INSERT INTO main_tank (block_id, fill_level, is_hot_water, target_temperature, temperature_increase_rate, hold_temperature_time, is_drain_active)
VALUES
    (1, 50.0, TRUE, 75.0, 2.5, 30, FALSE),
    (2, 60.0, FALSE, 80.0, 3.0, 25, TRUE);

INSERT INTO secondary_tank (block_id, fill_level, is_hot_water, target_temperature, temperature_increase_rate, hold_temperature_time, is_drain_active, is_mixer_active, chemical_dose, dye_dose)
VALUES
    (3, 30.0, FALSE, 60.0, 1.8, 20, TRUE, TRUE, 0.5, 0.2),
    (4, 40.0, TRUE, 65.0, 2.0, 15, FALSE, FALSE, 0.4, 0.3);

INSERT INTO pump (block_id, rpm, circulation_time_in_out, circulation_time_out_in)
VALUES
    (5, 1500, 10, 15),
    (6, 2000, 12, 18);

INSERT INTO programs (program_id, name)
VALUES
    (1, 'Program A'),
    (2, 'Program B'),
    (3, 'Program C');

INSERT INTO program_block (program_id, block_id)
VALUES
    (1, 1), (1, 3), (1, 5),
    (2, 2), (2, 4), (2, 6),
    (3, 1), (3, 6);

INSERT INTO recipes (id, name, description)
VALUES
    (1, 'Recipe 1', 'Basic recipe for dyeing'),
    (2, 'Recipe 2', 'Advanced recipe with chemical treatment'),
    (3, 'Recipe 3', 'Experimental recipe with high temperature');

INSERT INTO recipe_resources (recipe_id, resource_id, quantity)
VALUES
    (1, 1, 500.0), (1, 2, 1000.0), (1, 3, 200.0),
    (2, 1, 300.0), (2, 2, 800.0), (2, 4, 150.0),
    (3, 3, 400.0), (3, 4, 250.0), (3, 5, 100.0);

INSERT INTO blocks (block_id)
VALUES (8), (9);

INSERT INTO main_tank (block_id, fill_level, is_hot_water, target_temperature, temperature_increase_rate, hold_temperature_time, is_drain_active)
VALUES
    (8, 70.0, TRUE, 85.0, 4.0, 10, TRUE);

INSERT INTO pump (block_id, rpm, circulation_time_in_out, circulation_time_out_in)
VALUES
    (9, 2500, 8, 10);

INSERT INTO program_block (program_id, block_id)
VALUES
    (3, 8), (3, 9);

INSERT INTO recipes (id, name, description)
VALUES
    (4, 'Recipe 4', 'Special recipe for premium dyeing');

INSERT INTO recipe_resources (recipe_id, resource_id, quantity)
VALUES
    (4, 5, 600.0), (4, 6, 300.0);
