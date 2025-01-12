DELETE
FROM recipe_resources;
DELETE
FROM program_block;
DELETE
FROM programs;
DELETE
FROM recipes;
DELETE
FROM main_tank;
DELETE
FROM secondary_tank;
DELETE
FROM pump;
DELETE
FROM blocks;

ALTER TABLE blocks AUTO_INCREMENT = 1;
ALTER TABLE main_tank AUTO_INCREMENT = 1;
ALTER TABLE secondary_tank AUTO_INCREMENT = 1;
ALTER TABLE pump AUTO_INCREMENT = 1;
ALTER TABLE programs AUTO_INCREMENT = 1;
ALTER TABLE recipes AUTO_INCREMENT = 1;
ALTER TABLE program_block AUTO_INCREMENT = 1;
ALTER TABLE recipe_resources AUTO_INCREMENT = 1;


DELETE
FROM recipe_resources_aud;
DELETE
FROM program_block_aud;
DELETE
FROM programs_aud;
DELETE
FROM recipes_aud;
DELETE
FROM main_tank_aud;
DELETE
FROM secondary_tank_aud;
DELETE
FROM pump_aud;
DELETE
FROM blocks_aud;

ALTER TABLE blocks_aud AUTO_INCREMENT = 1;
ALTER TABLE main_tank_aud AUTO_INCREMENT = 1;
ALTER TABLE secondary_tank_aud AUTO_INCREMENT = 1;
ALTER TABLE pump_aud AUTO_INCREMENT = 1;
ALTER TABLE programs_aud AUTO_INCREMENT = 1;
ALTER TABLE recipes_aud AUTO_INCREMENT = 1;
ALTER TABLE program_block_aud AUTO_INCREMENT = 1;
ALTER TABLE recipe_resources_aud AUTO_INCREMENT = 1;
